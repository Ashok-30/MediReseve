package com.example.ehealthcareapp.doctor
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import com.example.ehealthcareapp.R

class DoctorOtpActivity : AppCompatActivity() {

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
                Toast.makeText(
                    this@DoctorOtpActivity,
                    "Login successful",
                    Toast.LENGTH_SHORT
                ).show()
                val sharedPrefs = getSharedPreferences("DoctorData", MODE_PRIVATE)
                val doctorId = sharedPrefs.getString("doctorId", "").toString()
                Log.d("doctorId",doctorId)

                if (doctorId != null) {

                    startDoctorHomePage(doctorId)
                } else {
                    Toast.makeText(this@DoctorOtpActivity, "Doctor ID not found", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this@DoctorOtpActivity, "Invalid OTP", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startDoctorHomePage(doctorId: String) {
        val intent = Intent(this, DoctorHomeScreenActivity::class.java)
        intent.putExtra("DOCTOR_ID", doctorId)
        startActivity(intent)
        finish()
    }
}
