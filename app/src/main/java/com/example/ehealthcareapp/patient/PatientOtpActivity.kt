package com.example.ehealthcareapp.patient

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import com.example.ehealthcareapp.R

class PatientOtpActivity : AppCompatActivity() {

    private lateinit var editTextOTP: EditText
    private lateinit var buttonVerifyOTP: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.doctor_otp_activity)

        editTextOTP = findViewById(R.id.doctorTextOTP)
        buttonVerifyOTP = findViewById(R.id.doctorVerifyOTP)

        buttonVerifyOTP.setOnClickListener {
            val enteredOTP = editTextOTP.text.toString()

            if (enteredOTP == "123456") {
                Toast.makeText(this@PatientOtpActivity,"Login successful",
                    Toast.LENGTH_SHORT
                ).show()
                val intent = Intent(this@PatientOtpActivity, PatientHomeScreen::class.java)
                startActivity(intent)
                finish()
            } else {

                Toast.makeText(this@PatientOtpActivity, "Invalid OTP", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
