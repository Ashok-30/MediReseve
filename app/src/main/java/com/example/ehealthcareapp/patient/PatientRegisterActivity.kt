package com.example.ehealthcareapp.patient


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

import android.widget.RadioButton
import android.widget.Toast
import com.example.ehealthcareapp.R
import com.example.ehealthcareapp.patient.models.PatientData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class PatientRegisterActivity : AppCompatActivity() {
    private lateinit var txtgender: EditText
    private lateinit var txtPatientFirstName: EditText
    private lateinit var txtPatientSurname: EditText
    private lateinit var txtPatientPhone: EditText
    private lateinit var txtPatientNiNumber: EditText
    private lateinit var txtPatientEmail: EditText
    private lateinit var txtPatientPassword: EditText
    private lateinit var btnNormalPatient: RadioButton

    private lateinit var btnConfirm: Button

    private lateinit var dbRef: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.patient_register_activity)

        txtgender = findViewById(R.id.txtgender)
        txtPatientFirstName = findViewById(R.id.txtPatientFirstName)
        txtPatientSurname = findViewById(R.id.txtPatientSurname)
        txtPatientPhone = findViewById(R.id.txtPatientPhone)
        txtPatientNiNumber = findViewById(R.id.txtPatientNiNumber)
        txtPatientEmail = findViewById(R.id.txtPatientEmail)
        txtPatientPassword = findViewById(R.id.txtPatientPassword)
        btnNormalPatient = findViewById(R.id.btnNormalPatient)
        btnConfirm = findViewById(R.id.btnPatientConfirm)

        dbRef = FirebaseDatabase.getInstance().getReference("Patients")

        btnConfirm.setOnClickListener {
            savePatientsData()

        }
    }

    private fun savePatientsData() {
        val gender = txtgender.text.toString()
        val firstName = txtPatientFirstName.text.toString()
        val surName = txtPatientSurname.text.toString()
        val phone = txtPatientPhone.text.toString()
        val niNumber = txtPatientNiNumber.text.toString()
        val email = txtPatientEmail.text.toString()
        val password = txtPatientPassword.text.toString()
        val type = if (btnNormalPatient.isChecked) "normal" else "VIP"
        if (gender.isEmpty()) {
            txtgender.error = "Enter this field"
            return
        }
        if (firstName.isEmpty()) {
            txtPatientFirstName.error = "Enter this field"
            return
        }
        if (surName.isEmpty()) {
            txtPatientSurname.error = "Enter this field"
            return
        }
        if (phone.isEmpty()) {
            txtPatientPhone.error = "Enter this field"
            return
        }
        if (niNumber.isEmpty()) {
            txtPatientNiNumber.error = "Enter this field"
            return
        }
        if (email.isEmpty()) {
            txtPatientEmail.error = "Enter this field"
            return
        }
        if (password.isEmpty()) {
            txtPatientPassword.error = "Enter this field"
            return
        }
        val id = FirebaseAuth.getInstance().currentUser?.uid ?: dbRef.push().key!!
        val patient = PatientData(
            id,  firstName, surName, gender,phone,
            niNumber, email, password, type
        )
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    dbRef.child(id).setValue(patient).addOnCompleteListener {
                        Toast.makeText(
                            this,
                            "Registered successfully, Please Login",
                            Toast.LENGTH_LONG
                        ).show()
                        val intent =
                            Intent(this@PatientRegisterActivity, PatientLoginActivity::class.java)
                        startActivity(intent)
                    }.addOnFailureListener { err ->
                        Toast.makeText(this, "Error ${err.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
    }
}




