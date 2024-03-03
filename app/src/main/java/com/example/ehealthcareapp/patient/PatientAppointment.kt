package com.example.ehealthcareapp.patient

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log

import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView

import androidx.appcompat.app.AppCompatActivity
import com.example.ehealthcareapp.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat

import java.util.*

class PatientAppointment : AppCompatActivity() {
    private lateinit var selectSlot: ImageButton
    private lateinit var doctorName: TextView
    private lateinit var patientFirstName: TextView
    private lateinit var pAge: EditText
    private var patientAge: String = ""

    private lateinit var pDescription: EditText
    private var patientDescription: String = ""
    private var availabilityId: String = ""
    private var receivedFirstName: String = ""
    private var doctorId: String = ""
    private var patientName: String = ""
    private var patientId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.patient_appointment_activity)

        selectSlot = findViewById(R.id.selectSlot)
        doctorName = findViewById(R.id.doctorName)
        patientFirstName = findViewById(R.id.patientFirstName)

        doctorName.setEnabled(false);
        patientFirstName.setEnabled(false);
        pAge = findViewById(R.id.pAge)

        pDescription = findViewById(R.id.pDescription)



        // Find the TextViews in your layout
        val startDateTextView = findViewById<TextView>(R.id.startDate1)
        val endDateTextView = findViewById<TextView>(R.id.endDate1)
        fetchDoctorAvailability(doctorId, startDateTextView, endDateTextView)

        receivedFirstName = intent.getStringExtra("doctorName") ?: ""

        patientName = intent.getStringExtra("patientName") ?: ""
        doctorId = intent.getStringExtra("doctorId")?:""
        patientId = intent.getStringExtra("patientId") ?: ""
        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference("DoctorAvailability")
        val query = reference.orderByChild("doctorId").equalTo(doctorId)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {

                    for (snapshot in dataSnapshot.children) {
                        availabilityId =
                            snapshot.child("availabilityId").getValue(String::class.java) ?: ""

                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })







        doctorName.text = receivedFirstName
        patientFirstName.text = patientName

        selectSlot.setOnClickListener {
            showDatePickerDialog()
        }
    }


    private fun showDatePickerDialog() {
        if (validateFields()) {

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)


        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(selectedYear, selectedMonth, selectedDay)
                val selectedDayOfWeek = selectedCalendar.get(Calendar.DAY_OF_WEEK)

                val daySelected = getSlotFromDay(selectedDayOfWeek)


                val selectedDate = formatDate(selectedCalendar.time)

                patientAge = pAge.text.toString()
                patientDescription = pDescription.text.toString()

                Log.d("selectedDate", selectedDate)
                val intent = Intent(this@PatientAppointment, TimeSlotActivity::class.java)
                intent.putExtra("selectedDay", daySelected)
                intent.putExtra("selectedDate", selectedDate)
                intent.putExtra("doctorId", doctorId)
                intent.putExtra("availabilityId", availabilityId)
                intent.putExtra("doctorName", receivedFirstName)
                intent.putExtra("patientName", patientName)
                intent.putExtra("patientId", patientId)
                intent.putExtra("patientAge", patientAge)
                intent.putExtra("patientDescription", patientDescription)

                startActivity(intent)
            },
            year,
            month,
            day
        )
        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        datePickerDialog.show()
    }
}
    private fun formatDate(date: Date): String {
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return sdf.format(date)
    }
    private fun getSlotFromDay(dayOfWeek: Int): String {
        return when (dayOfWeek) {
            Calendar.SUNDAY -> "sunday"
            Calendar.MONDAY -> "monday"
            Calendar.TUESDAY -> "tuesday"
            Calendar.WEDNESDAY -> "wednesday"
            Calendar.THURSDAY -> "thursday"
            Calendar.FRIDAY -> "friday"
            Calendar.SATURDAY -> "saturday"
            else -> "defaultSlots"
        }
    }
    private fun validateFields(): Boolean {
        val age = pAge.text.toString().trim()
        val description = pDescription.text.toString().trim()

        if (age.isEmpty()) {
            pAge.error = "Please fill this field"
            return false
        }

        if (description.isEmpty()) {
            pDescription.error = "Please fill this field"
            return false
        }

        return true
    }
    private fun fetchDoctorAvailability(doctorId: String, startDateTextView: TextView, endDateTextView: TextView) {
        val dbRef = FirebaseDatabase.getInstance().reference
        val doctorId1 = intent.getStringExtra("doctorId").toString()
        dbRef.child("DoctorAvailability").orderByChild("doctorId").equalTo(doctorId1)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (availabilitySnapshot in dataSnapshot.children) {
                            // Fetch startDate and endDate from dataSnapshot
                            val startDate = availabilitySnapshot.child("startDate").getValue(String::class.java)
                            val endDate = availabilitySnapshot.child("endDate").getValue(String::class.java)

                            // Log the values for debugging
                            Log.d("Availability", "startDate: $startDate, endDate: $endDate")

                            // Display startDate in one TextView
                            startDateTextView.text = ":$startDate"

                            // Display endDate in another TextView
                            endDateTextView.text = ":$endDate"
                        }
                    } else {
                        Log.d("DoctorAvailability", "No availability data found for doctorId: $doctorId")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("DoctorAvailability", "Error fetching data: ${error.message}")
                }
            })
    }



}
