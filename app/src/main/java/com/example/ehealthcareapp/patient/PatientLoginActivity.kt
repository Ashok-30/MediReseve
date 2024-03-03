package com.example.ehealthcareapp.patient

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ehealthcareapp.R
import com.example.ehealthcareapp.patient.models.PatientData


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class PatientLoginActivity : AppCompatActivity() {
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button

    private lateinit var database:DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.patient_login_activity)
        val sharedPrefs = getSharedPreferences("UserData", MODE_PRIVATE)
        val patientId = sharedPrefs.getString("patientId", "")

        emailEditText = findViewById(R.id.editTxtPatientEmail)
        passwordEditText = findViewById(R.id.editTxtPatientPassword)
        loginButton = findViewById(R.id.btnPatientLogin)
        registerButton = findViewById(R.id.btnPatientRegister)

        database = FirebaseDatabase.getInstance().getReference("Patients")

        registerButton.setOnClickListener {
            val intent = Intent(this@PatientLoginActivity, PatientRegisterActivity::class.java)
            startActivity(intent)
        }

        if (patientId.isNullOrEmpty()) {
        loginButton.setOnClickListener {
            val inputEmail = emailEditText.text.toString().trim()
            val inputPassword = passwordEditText.text.toString().trim()
            if (inputEmail.isEmpty() || inputPassword.isEmpty()) {
                Toast.makeText(this, "Email or password cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            FirebaseAuth.getInstance().signInWithEmailAndPassword(inputEmail, inputPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {


                        val loggedInUserEmail = inputEmail
                        retrievePatientDataAndStore(loggedInUserEmail)
                    } else {

                        Toast.makeText(
                            this@PatientLoginActivity,
                            "Incorrect email or password",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }
        else {
            // Data exists in SharedPreferences, navigate to the home screen
            val intent = Intent(this@PatientLoginActivity, PatientHomeScreen::class.java) // Replace HomeActivity with your home screen activity
            startActivity(intent)
            finish()
        }

    }
    private fun retrievePatientDataAndStore(userEmail: String) {
        val query = database.orderByChild("email").equalTo(userEmail)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val patientData = snapshot.getValue(PatientData::class.java)


                    val sharedPrefs = getSharedPreferences("UserData", MODE_PRIVATE)
                    val editor = sharedPrefs.edit()
                    editor.putString("patientId", patientData?.patientId)
                    editor.putString("first", patientData?.first)
                    editor.putString("last", patientData?.last)
                    editor.putString("gender", patientData?.gender)
                    editor.putString("niNumber", patientData?.niNumber)
                    editor.putString("phone", patientData?.phone)
                    editor.putString("email", patientData?.email)
                    editor.putString("type", patientData?.type)



                    editor.apply()


                    val intent = Intent(this@PatientLoginActivity, PatientOtpActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle onCancelled event if needed
            }
        })
    }
}