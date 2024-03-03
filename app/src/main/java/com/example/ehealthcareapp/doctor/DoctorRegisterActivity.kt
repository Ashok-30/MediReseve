package com.example.ehealthcareapp.doctor


import android.content.Intent
import android.os.Bundle
import android.widget.Button

import android.widget.EditText


import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import com.example.ehealthcareapp.R

import com.example.ehealthcareapp.doctor.models.DoctorData

import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class DoctorRegisterActivity : AppCompatActivity() {
    private lateinit var txtspecialization: EditText
    private lateinit var txtDoctorFirstName: EditText
    private lateinit var txtDoctorSurname: EditText
    private lateinit var txtDoctorPhone: EditText
    private lateinit var txtDoctorId: EditText
    private lateinit var txtDoctorEmail: EditText
    private lateinit var txtDoctorPassword: EditText

    private lateinit var btnDocConfirm: Button

    private lateinit var dbRef: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.doctor_register_activity)


        txtDoctorFirstName = findViewById(R.id.txtDoctorFirstName)
        txtDoctorSurname = findViewById(R.id.txtDoctorSurname)
        txtspecialization = findViewById(R.id.txtspecialization)
        txtDoctorPhone = findViewById(R.id.txtDoctorPhone)
        txtDoctorId = findViewById(R.id.txtDoctorId)
        txtDoctorEmail = findViewById(R.id.txtDoctorEmail)
        txtDoctorPassword = findViewById(R.id.txtDoctorPassword)

        btnDocConfirm = findViewById(R.id.btnDocConfirm)

        dbRef = FirebaseDatabase.getInstance().getReference("Doctors")

        btnDocConfirm.setOnClickListener {
            saveDoctorsData()

        }
    }

    private fun saveDoctorsData(){

        val first=txtDoctorFirstName.text.toString()
        val firstName = "Dr. $first"
        val surName= txtDoctorSurname.text.toString()
        val specialization =txtspecialization.text.toString()
        val phone= txtDoctorPhone.text.toString()
        val doctorId = txtDoctorId.text.toString()
        val email= txtDoctorEmail.text.toString()
        val password= txtDoctorPassword.text.toString()

        // Ensure data is entered and not empty
        if (firstName.isEmpty()) {
            txtDoctorFirstName.error="Enter First Name"
            return
        }
        if (surName.isEmpty()) {
            txtDoctorSurname.error="Enter Sur Name"
            return
        }
        if (specialization.isEmpty()) {
            txtspecialization.error="Enter Specialization"
            return
        }
        if (phone.isEmpty()) {
            txtDoctorPhone.error="Enter Phone"
            return
        }
        if (doctorId.isEmpty()) {
            txtDoctorId.error="Enter NiNumber"
            return
        }
        if (email.isEmpty()) {
            txtDoctorEmail.error="Enter EMAIL"
            return
        }
        if (password.isEmpty()) {
            txtDoctorPassword.error="Enter Password"
            return
        }
        val id = FirebaseAuth.getInstance().currentUser?.uid ?: dbRef.push().key!!
        val doctor = DoctorData(id, firstName,surName,specialization,phone,
            doctorId,email,password)
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    dbRef.child(id).setValue(doctor).addOnCompleteListener {
                        Toast.makeText(
                            this,
                            "Registered successfully, Please Login",
                            Toast.LENGTH_LONG
                        ).show()
                        val intent =
                            Intent(this@DoctorRegisterActivity, DoctorLoginActivity::class.java)
                        startActivity(intent)
                    }.addOnFailureListener { err ->
                        //Display error message
                        Toast.makeText(this, "Error ${err.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
    }
}
