package com.example.ehealthcareapp.doctor

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.ehealthcareapp.R
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DoctorRescheduleActivity : AppCompatActivity() {

    private lateinit var doctorName: TextView
    private lateinit var patientName: TextView
    private lateinit var bookingDate: TextView
    private lateinit var bookingTime: TextView
    private lateinit var patientDescription: TextView
    private lateinit var rescheduleButton: Button
    private lateinit var cancelButton: Button
    private var selectedDayName: String = "Unknown"



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.doctor_reschedule_activity)


        val pName = intent.getStringExtra("patientName")
        val dName = intent.getStringExtra("doctorName")
        val bDate = intent.getStringExtra("bookingDate")
        val pDesc = intent.getStringExtra("patientDescription")
        val bTime = intent.getStringExtra("bookingTime")


        doctorName = findViewById(R.id.doctorName)
        patientName = findViewById(R.id.patientName)
        bookingDate = findViewById(R.id.bookingDate)
        bookingTime = findViewById(R.id.bookingTime)
        patientDescription = findViewById(R.id.patientDescription)
        rescheduleButton = findViewById(R.id.rescheduleButton)
        cancelButton = findViewById(R.id.cancelButton)


        doctorName.text = dName
        patientName.text = pName

        bookingDate.text = bDate
        bookingTime.text = bTime
        patientDescription.text= pDesc
        val editTextDate = findViewById<TextView>(R.id.editBookingDate)
        val spinner = findViewById<Spinner>(R.id.editBookingTime)
        cancelButton.setOnClickListener {
            val bId = intent.getStringExtra("bookingId").toString()

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Confirm Cancellation")
            builder.setMessage("Do you really want to cancel the appointment?")

            builder.setPositiveButton("Yes") { _, _ ->
                // User clicked Yes button, delete fields from the Booking table
                val databaseReference = FirebaseDatabase.getInstance().reference
                val bookingRef = databaseReference.child("Booking").child(bId)

                bookingRef.removeValue()
                    .addOnSuccessListener {
                        // Appointment canceled successfully
                        Toast.makeText(
                            this@DoctorRescheduleActivity,
                            "Appointment Canceled",
                            Toast.LENGTH_SHORT
                        ).show()


                    }
                    .addOnFailureListener { e ->
                        // Handle cancellation failure
                        Log.e("CancellationError", "Error canceling appointment: ${e.message}")
                        Toast.makeText(
                            this@DoctorRescheduleActivity,
                            "Failed to cancel appointment",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }

            builder.setNegativeButton("No") { dialog, _ ->
                // Dismiss the dialog when "No" is clicked
                dialog.dismiss()
            }


            val dialog = builder.create()
            dialog.show()
        }

        rescheduleButton.setOnClickListener {
            val selectedSpinnerIndex = spinner.selectedItemPosition
            val dropdownValues = resources.getIntArray(R.array.dropdown_values)
            val selectedDate = editTextDate.text.toString()
            val bId = intent.getStringExtra("bookingId").toString()
            val availabilityId = intent.getStringExtra("availabilityId") // Retrieve availabilityId from intent

            if (selectedDate.isNotEmpty() && selectedSpinnerIndex != AdapterView.INVALID_POSITION &&
                selectedSpinnerIndex < dropdownValues.size && availabilityId != null
            ) {
                val selectedValue = dropdownValues[selectedSpinnerIndex].toString()

                // Update the 'Booking' table
                val databaseReference = FirebaseDatabase.getInstance().reference
                val bookingRef = databaseReference.child("Booking").child(bId)

                bookingRef.child("selectedDate").setValue(selectedDate)
                bookingRef.child("selectedDay").setValue(selectedDayName)
                bookingRef.child("slot").setValue(selectedValue)
                    .addOnSuccessListener {
                        // Booking table update successful
                        Log.d("BookingUpdateStatus", "Booking updated successfully")

                        // Now update the 'DoctorAvailability' table
                        val doctorAvailabilityRef = databaseReference.child("DoctorAvailability").child(availabilityId)
                        val dayPath = "$selectedDayName/$selectedValue"

                        val updateMap = HashMap<String, Any>()
                        updateMap[dayPath] = 0 // Update the slot value to 1 for availability

                        doctorAvailabilityRef.updateChildren(updateMap)
                            .addOnSuccessListener {
                                // DoctorAvailability table update successful
                                Log.d("DoctorAvailabilityUpdateStatus", "DoctorAvailability updated successfully")
                                Toast.makeText(
                                    this@DoctorRescheduleActivity,
                                    "Reschedule Successful",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            .addOnFailureListener { e ->
                                // Handle DoctorAvailability update failure
                                Log.e("DoctorAvailabilityUpdateStatus", "Error updating DoctorAvailability: ${e.message}")
                                Toast.makeText(
                                    this@DoctorRescheduleActivity,
                                    "DoctorAvailability Update Failed",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                    .addOnFailureListener { e ->
                        // Handle Booking table update failure
                        Log.e("BookingUpdateStatus", "Error updating Booking: ${e.message}")
                        Toast.makeText(
                            this@DoctorRescheduleActivity,
                            "Booking Update Failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            } else {
                // Handle invalid selection
                Log.e("Error", "Invalid selection or availabilityId is null")
                Toast.makeText(
                    this@DoctorRescheduleActivity,
                    "Invalid selection or availabilityId is null",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


        editTextDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            // Inside your DatePickerDialog listener
            val datePickerDialog = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->

                    val calendar = Calendar.getInstance()
                    calendar.set(year, monthOfYear, dayOfMonth) // Set the selected date in the calendar

                    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

                    val selectedDate = dateFormat.format(calendar.time)
                    editTextDate.text = selectedDate


                    val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
                    selectedDayName = when (dayOfWeek) {
                        Calendar.MONDAY -> "monday"
                        Calendar.TUESDAY -> "tuesday"
                        Calendar.WEDNESDAY -> "wednesday"
                        Calendar.THURSDAY -> "thursday"
                        Calendar.FRIDAY -> "friday"
                        Calendar.SATURDAY -> "saturday"
                        Calendar.SUNDAY -> "sunday"
                        else -> "Unknown"
                    }

                    Log.d("SelectedDay", selectedDayName)
                },
                year, month, day
            )
            datePickerDialog.show()
        }



    }


}
