package com.example.ehealthcareapp.patient

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.ehealthcareapp.R
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*


class TimeSlotActivity : AppCompatActivity() {
    private lateinit var dateSelected:TextView
    private lateinit var daySubmit:Button
    private lateinit var unSelect:Button

    private lateinit var selectedDay: String
    private lateinit var selectedDate: String
    private lateinit var doctorId: String
    private lateinit var doctorName: String
    private lateinit var patientId: String
    private lateinit var patientName: String
    private lateinit var patientAge: String
    private lateinit var patientDescription: String
    private lateinit var availabilityId: String

    private var slotSelected = false
    private var lastSelectedIndex=-1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.time_slot_activity)

            doctorId = intent.getStringExtra("doctorId") ?: ""
        doctorName = intent.getStringExtra("doctorName") ?: ""
         patientId = intent.getStringExtra("patientId") ?: ""
         patientName = intent.getStringExtra("patientName") ?: ""
         patientAge = intent.getStringExtra("patientAge") ?: ""
         patientDescription = intent.getStringExtra("patientDescription") ?: ""
        selectedDay = intent.getStringExtra("selectedDay") ?: ""
        availabilityId = intent.getStringExtra("availabilityId") ?: ""
        selectedDate = intent.getStringExtra("selectedDate") ?: ""

        dateSelected=findViewById(R.id.date)
        daySubmit=findViewById(R.id.daySubmit)
        unSelect=findViewById(R.id.unSelect)



        val date = getString(R.string.select_time_slot, "for", selectedDate)
        dateSelected.text = date


        val buttons = listOf<Button>(
            findViewById(R.id.slot1),
            findViewById(R.id.slot2),
            findViewById(R.id.slot3),
            findViewById(R.id.slot4),
            findViewById(R.id.slot5),
            findViewById(R.id.slot6),
            findViewById(R.id.slot7),
            findViewById(R.id.slot8),
            findViewById(R.id.slot9),
            findViewById(R.id.slot10),
            findViewById(R.id.slot11),
            findViewById(R.id.slot12)
        )

        val databaseReference = FirebaseDatabase.getInstance().getReference("DoctorAvailability")
            .child(availabilityId)
            .child(selectedDay)
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val valuesList = mutableListOf<Int>()
                    for (i in 0 until 12) {
                        val value = dataSnapshot.child(i.toString()).getValue(Int::class.java)
                        value?.let { valuesList.add(it) }
                    }
                    if (valuesList.all { it == 0 }) {
                        // All values in the list are zero
                        Toast.makeText(this@TimeSlotActivity    , "Please select another date", Toast.LENGTH_SHORT).show()
                    }
                    buttons.forEachIndexed { index, button ->
                        if (valuesList.size > index) {
                            if (valuesList[index] == 0) {
                                button.isEnabled = false
                            } else {
                                button.isEnabled = true
                                button.setOnClickListener {
                                    if (!slotSelected) {
                                        slotSelected = true
                                        lastSelectedIndex = index
                                        button.isEnabled = false
                                        unSelect.setOnClickListener {
                                            if (lastSelectedIndex != -1) {
                                                // Enable the button based on the last selected index
                                                buttons[lastSelectedIndex].isEnabled = true
                                                lastSelectedIndex = -1 // Reset lastSelectedIndex
                                                slotSelected=false
                                                Toast.makeText(
                                                    applicationContext,
                                                    "Slot deselected",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            } else {
                                                Toast.makeText(
                                                    applicationContext,
                                                    "No slot selected to deselect",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                            daySubmit.setOnClickListener {

                                                val slot = index.toString()
                                                databaseReference.child(index.toString())
                                                    .setValue(0)
                                                    .addOnCompleteListener { task ->
                                                        if (task.isSuccessful) {
                                                            val intent = Intent(
                                                                this@TimeSlotActivity,
                                                                BookingActivity::class.java
                                                            )
                                                            intent.putExtra(
                                                                "selectedDay",
                                                                selectedDay
                                                            )
                                                            intent.putExtra(
                                                                "selectedDate",
                                                                selectedDate
                                                            )
                                                            intent.putExtra(
                                                                "doctorId",
                                                                doctorId
                                                            )
                                                            intent.putExtra(
                                                                "availabilityId",
                                                                availabilityId
                                                            )
                                                            intent.putExtra(
                                                                "doctorName",
                                                                doctorName
                                                            )
                                                            intent.putExtra(
                                                                "patientName",
                                                                patientName
                                                            )
                                                            intent.putExtra(
                                                                "patientId",
                                                                patientId
                                                            )
                                                            intent.putExtra(
                                                                "patientAge",
                                                                patientAge
                                                            )
                                                            intent.putExtra(
                                                                "patientDescription",
                                                                patientDescription
                                                            )
                                                            intent.putExtra(
                                                                "availabilityId",
                                                                availabilityId
                                                            )
                                                            intent.putExtra("slot", slot)

                                                            startActivity(intent)
                                                        } else {

                                                            Toast.makeText(
                                                                applicationContext,
                                                                "Failed to update value in DB.",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                    }
                                            }
                                        } else{
                                            Toast.makeText(
                                                applicationContext,
                                                "Slot already selected",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }




                                }
                            }
                        }
                    }

                } else {
                    Toast.makeText(applicationContext, "Data doesn't exist", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(applicationContext, "Error occurred", Toast.LENGTH_SHORT).show()
            }
        })

    }
}
