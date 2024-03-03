package com.example.ehealthcareapp.doctor

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ehealthcareapp.R

import com.example.ehealthcareapp.doctor.models.DoctorData



import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class DoctorLoginActivity : AppCompatActivity() {
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button

    private lateinit var database:DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.doctor_login_activity)
        val sharedPrefs = getSharedPreferences("DoctorData", MODE_PRIVATE)
        val doctorId = sharedPrefs.getString("doctorId", "").toString()
        Log.d("doctorId",doctorId)
        emailEditText = findViewById(R.id.editTxtDoctorEmail)
        passwordEditText = findViewById(R.id.editTxtDoctorPassword)
        loginButton = findViewById(R.id.btnDoctorLogin)
        registerButton = findViewById(R.id.btnDoctorRegister)

        database = FirebaseDatabase.getInstance().getReference("Doctors")

        registerButton.setOnClickListener {
            val intent = Intent(this@DoctorLoginActivity, DoctorRegisterActivity::class.java)
            startActivity(intent)
        }

        if (doctorId.isNullOrEmpty()) {
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
                            retrieveDoctorDataAndStore(loggedInUserEmail)
                        } else {

                            Toast.makeText(
                                this@DoctorLoginActivity,
                                "Incorrect email or password",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }
        else {
            // Data exists in SharedPreferences, navigate to the home screen
            val intent = Intent(this@DoctorLoginActivity, DoctorHomeScreenActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
    private fun retrieveDoctorDataAndStore(userEmail: String) {
        val query = database.orderByChild("email").equalTo(userEmail)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (snapshot in dataSnapshot.children) {
                    val doctorData = snapshot.getValue(DoctorData::class.java)


                    val sharedPrefs = getSharedPreferences("DoctorData", MODE_PRIVATE)
                    val editor = sharedPrefs.edit()
                    editor.putString("doctorId", doctorData?.doctorId)
                    editor.putString("first", doctorData?.first)
                    editor.putString("last", doctorData?.last)
                    editor.putString("specialization", doctorData?.specialization)
                    editor.putString("phone", doctorData?.phone)
                    editor.putString("doctorNum", doctorData?.doctornum)
                    editor.putString("email", doctorData?.email)
                    editor.apply()


                    val intent = Intent(this@DoctorLoginActivity, DoctorOtpActivity::class.java)
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