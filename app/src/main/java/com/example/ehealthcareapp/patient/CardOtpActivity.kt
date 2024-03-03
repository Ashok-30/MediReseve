package com.example.ehealthcareapp.patient

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import com.example.ehealthcareapp.R
import com.example.ehealthcareapp.patient.models.CardDetails
import com.google.firebase.database.FirebaseDatabase

class CardOtpActivity : AppCompatActivity() {

    private lateinit var cardOTP: EditText
    private lateinit var cardVerify: Button

    private lateinit var patientId: String
    private lateinit var cardNumber: String
    private lateinit var cardName: String
    private lateinit var expiryDate: String
    private lateinit var expiryYear: String
    private lateinit var cvv: String



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.card_otp_activity)
        Toast.makeText(
            this@CardOtpActivity,
            "OTP sent to registered mobile number",
            Toast.LENGTH_SHORT
        ).show()
        cardOTP= findViewById(R.id.cardOtp)
        cardVerify= findViewById(R.id.cardVerify)
        val sharedPrefs = getSharedPreferences("UserData", MODE_PRIVATE)

        patientId = sharedPrefs.getString("patientId", "").toString()
        cardNumber=intent.getStringExtra("cardNumber") ?: ""
        cardName=intent.getStringExtra("cardName") ?: ""
        expiryDate=intent.getStringExtra("expiryDate") ?: ""
        expiryYear=intent.getStringExtra("expiryYear") ?: ""
        cvv=intent.getStringExtra("cvv") ?: ""


        cardVerify.setOnClickListener {
            val enteredOTP = cardOTP.text.toString()

            if (enteredOTP == "123456") {
                // Update the Patients table to set type="VIP" for the specific patientId
                val dbRef = FirebaseDatabase.getInstance().getReference("Patients")
                val patientTypeRef = dbRef.child(patientId).child("type")
                patientTypeRef.setValue("VIP")
                    .addOnSuccessListener {
                        // If the update in Patients table is successful, save card details to CardDetails table
                        val cardDetailsRef = FirebaseDatabase.getInstance().getReference("CardDetails")
                        val cardDetailsId = cardDetailsRef.push().key
                        val cardDetails = CardDetails(patientId, cardNumber, cardName, expiryDate, expiryYear, cvv)

                        cardDetailsId?.let { cardId ->
                            cardDetailsRef.child(cardId).setValue(cardDetails)
                                .addOnSuccessListener {
                                    Toast.makeText(this@CardOtpActivity, "Payment successful", Toast.LENGTH_SHORT).show()
                                    val intent =
                                        Intent(this@CardOtpActivity, PatientVIPActivity::class.java)
                                    startActivity(intent)
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this@CardOtpActivity, "Failed to save card details", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(this@CardOtpActivity, "Failed to update user type", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this@CardOtpActivity, "Invalid OTP", Toast.LENGTH_SHORT).show()
            }
        }

    }

}
