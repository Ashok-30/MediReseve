package com.example.ehealthcareapp.patient

import android.content.Intent
import android.os.Bundle


import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.example.ehealthcareapp.R

class BookingActivity : AppCompatActivity() {

    private lateinit var availabilityId: String
    private lateinit var doctorId: String
    private lateinit var doctorName: String
    private lateinit var patientId: String
    private lateinit var patientName: String
    private lateinit var selectedDate: String
    private lateinit var selectedDay: String
    private lateinit var patientAge: String
    private lateinit var patientDescription: String
    private lateinit var slot: String
    private lateinit var type: String
    private lateinit var pstatus: String
    private lateinit var dstatus: String



    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.booking_activity)
        val sharedPrefs = getSharedPreferences("UserData", MODE_PRIVATE)

        type = sharedPrefs.getString("type", "").toString()



        databaseReference = FirebaseDatabase.getInstance().getReference("Booking")
        availabilityId = intent.getStringExtra("availabilityId") ?: ""
        doctorId = intent.getStringExtra("doctorId") ?: ""
        doctorName=intent.getStringExtra("doctorName") ?: ""
        patientId = intent.getStringExtra("patientId") ?: ""
        patientName=intent.getStringExtra("patientName") ?: ""
        selectedDate = intent.getStringExtra("selectedDate") ?: ""
        selectedDay = intent.getStringExtra("selectedDay") ?: ""
        patientAge = intent.getStringExtra("patientAge") ?: ""
        patientDescription = intent.getStringExtra("patientDescription") ?: ""
        slot = intent.getStringExtra("slot") ?: ""
        pstatus="0"
        dstatus="0"





        findViewById<TextView>(R.id.doctorName).text = "Doctor: $doctorName"

        findViewById<TextView>(R.id.PatientName).text = "Patient Name:$patientName"
        findViewById<TextView>(R.id.selectedDate).text = "Date:$selectedDate"
        findViewById<TextView>(R.id.selectedDay).text = "Day:$selectedDay"
        findViewById<TextView>(R.id.patientAge).text = "Patient Age:$patientAge"
        findViewById<TextView>(R.id.patientDescription).text = "Description:$patientDescription"

        val slotAsInt = slot.toInt()
        var slotAsText: String = when (slotAsInt) {
            0 -> "9AM-9:30AM"
            1 -> "9:30AM-10AM"
            2 -> "10AM-10:30AM"
            3 -> "10:30AM-11AM"
            4 -> "11AM-11:30AM"
            5 -> "12PM-12:30AM"
            6 -> "12:30PM-1PM"
            7 -> "2PM-2:30PM"
            8 -> "2:30PM-3PM"
            9 -> "3PM-3:30PM"
            10 -> "3:30PM-4PM"
            11 -> "4PM-4:30PM"
            else -> "Unknown Slot"
        }

        findViewById<TextView>(R.id.slot).text = "Slot:$slotAsText"
        if(type=="VIP") {

            findViewById<TextView>(R.id.type).setText(R.string.VIP)

        }
        else{
            findViewById<TextView>(R.id.type).setText(R.string.normal   )

        }

        findViewById<Button>(R.id.book).setOnClickListener {
            storeDataInDatabase()
        }
    }

    // Function to store data in Firebase Realtime Database
    private fun storeDataInDatabase() {
        val bookingData = hashMapOf(
            "availabilityId" to availabilityId,
            "doctorId" to doctorId,
            "patientId" to patientId,
            "selectedDate" to selectedDate,
            "selectedDay" to selectedDay,
            "patientAge" to patientAge,
            "patientDescription" to patientDescription,
            "slot" to slot,
            "type" to type,
            "pstatus" to pstatus,
            "dstatus" to dstatus
        )

        // Push data to Firebase under "Booking" table
        databaseReference.push().setValue(bookingData)
            .addOnSuccessListener {
                val intent = Intent(this@BookingActivity, PatientBookingActivity::class.java)
                startActivity(intent)
                Toast.makeText(
                    this@BookingActivity,
                    "Booking Successful!",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener {
                Toast.makeText(
                    this@BookingActivity,
                    "Failed to make booking. Please try again.",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}
