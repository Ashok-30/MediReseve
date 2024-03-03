package com.example.ehealthcareapp.patient

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import com.example.ehealthcareapp.R


class PatientPaymentActivity : AppCompatActivity() {

    private lateinit var cardNumber: EditText
    private lateinit var cardName: EditText
    private lateinit var expiryDate: EditText
    private lateinit var expiryYear: EditText
    private lateinit var cvv: EditText
    private lateinit var cardPayment: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.patient_payment_activity)

        cardNumber = findViewById(R.id.cardNumber)
        cardName = findViewById(R.id.cardName)
        expiryDate = findViewById(R.id.expiryDate)
        expiryYear = findViewById(R.id.expiryYear)
        cvv = findViewById(R.id.cvv)

        cardPayment = findViewById(R.id.cardPayment)

        cardPayment.setOnClickListener {
            val enteredCardNumber = cardNumber.text.toString()
            val enteredCardName = cardName.text.toString()
            val enteredExpiryDate = expiryDate.text.toString()
            val enteredExpiryYear = expiryYear.text.toString()
            val enteredCvv = cvv.text.toString()

            // Perform validations here
            if (enteredCardNumber.isNotBlank()
                && enteredCardName.isNotBlank()
                && enteredExpiryDate.matches(Regex("^(0[1-9]|1[0-2])\$"))
                && enteredExpiryYear.matches(Regex("^202[4-9]|20[3-9][0-9]\$"))
                && enteredCvv.isNotBlank()
            ) {
                val intent = Intent(this@PatientPaymentActivity, CardOtpActivity::class.java)
                intent.putExtra("cardNumber", enteredCardNumber)
                intent.putExtra("cardName", enteredCardName)
                intent.putExtra("expiryDate", enteredExpiryDate)
                intent.putExtra("expiryYear", enteredExpiryYear)
                intent.putExtra("cvv", enteredCvv)
                startActivity(intent)
            } else {

                 Toast.makeText(this@PatientPaymentActivity, "Invalid card details", Toast.LENGTH_SHORT).show()
            }
        }
    }
}