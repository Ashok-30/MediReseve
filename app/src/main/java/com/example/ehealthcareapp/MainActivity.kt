package com.example.ehealthcareapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.widget.ImageView
import com.example.ehealthcareapp.doctor.DoctorLoginActivity
import com.example.ehealthcareapp.patient.PatientLoginActivity


class MainActivity : AppCompatActivity() {

    private lateinit var patientButton: ImageView
    private lateinit var doctorButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



            setContentView(R.layout.activity_main)
            patientButton = findViewById(R.id.patientButton)
            doctorButton = findViewById(R.id.doctorButton)


            patientButton.setOnClickListener {
                val intent = Intent(this@MainActivity, PatientLoginActivity::class.java)
                startActivity(intent)
            }

            doctorButton.setOnClickListener {
                val intent = Intent(this@MainActivity, DoctorLoginActivity::class.java)
                startActivity(intent)
            }


        }
    }

