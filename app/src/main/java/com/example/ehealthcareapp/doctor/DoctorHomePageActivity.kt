package com.example.ehealthcareapp.doctor

import android.app.AlertDialog
import android.content.Intent
import android.icu.util.Calendar
import android.icu.util.TimeZone
import com.example.ehealthcareapp.R
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.example.ehealthcareapp.MainActivity
import com.example.ehealthcareapp.doctor.models.DoctorAvailability
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DoctorHomePageActivity : AppCompatActivity() {
    private lateinit var navigationView: NavigationView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var hamburgerButton: ImageButton
    private lateinit var editTextStartDate: TextInputEditText
    private lateinit var editTextEndDate: TextInputEditText
    private lateinit var profileImage: ImageView
    private lateinit var storageReference: StorageReference
    private lateinit var doctorId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.doctorhomepage_activity)
        drawerLayout = findViewById(R.id.drawer_layout)
        hamburgerButton = findViewById(R.id.hamburgerButton)
        navigationView = findViewById(R.id.navigation_view)
        storageReference = FirebaseStorage.getInstance().reference
        val sharedPrefs = getSharedPreferences("DoctorData", MODE_PRIVATE)
        doctorId = sharedPrefs.getString("doctorId", "").toString()
        displayUploadedProfileImage()
        val first = sharedPrefs.getString("first", "").toString()
        val email = sharedPrefs.getString("email", "").toString()
        val headerView = navigationView.getHeaderView(0)
        profileImage = headerView.findViewById(R.id.profileImage)
        val userNameTextView = headerView.findViewById<TextView>(R.id.userName)
        val emailTextView = headerView.findViewById<TextView>(R.id.emailTextView)

        userNameTextView.text = first
        emailTextView.text = email
        setupDrawer()
        setupNavigationView()
        val dialog = AlertDialog.Builder(this)
            .setTitle("Reminder")
            .setMessage("Please select only five of the below available dates. You will be able to change the dates anytime.")
            .setPositiveButton("Okay") { _, _ ->

            }
            .create()

        dialog.show()


        editTextStartDate = findViewById(R.id.editTextStartDate)
        editTextEndDate = findViewById(R.id.editTextEndDate)
        val doctorMon = findViewById<CheckBox>(R.id.doctorMon)
        val doctorTue = findViewById<CheckBox>(R.id.doctorTue)
        val doctorWed = findViewById<CheckBox>(R.id.doctorWed)
        val doctorThu = findViewById<CheckBox>(R.id.doctorThu)
        val doctorFri = findViewById<CheckBox>(R.id.doctorFri)
        val doctorSat = findViewById<CheckBox>(R.id.doctorSat)
        val doctorSun = findViewById<CheckBox>(R.id.doctorSun)
        val submitButton = findViewById<Button>(R.id.daySubmit)
        editTextStartDate.setOnClickListener {
            openDatePicker(true)
        }


        editTextEndDate.setOnClickListener {
            openDatePicker(false)
        }

        val checkBoxes = listOf(
            doctorMon, doctorTue, doctorWed, doctorThu, doctorFri, doctorSat, doctorSun
        )

        for (checkBox in checkBoxes) {
            checkBox.setOnCheckedChangeListener { _, _ ->
                val selectedDays = checkBoxes.count { it.isChecked }
                if (selectedDays > 5) {
                    checkBox.isChecked = false
                    Toast.makeText(
                        this,
                        "Please select only 5 days",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }


        submitButton.setOnClickListener {
            val selectedDays = checkBoxes.filter { it.isChecked }
            if (selectedDays.size <= 5) {
                val database = FirebaseDatabase.getInstance().getReference("DoctorAvailability")
                val startDate = editTextStartDate.text.toString()
                Log.d("startDate",startDate)
                val endDate = editTextEndDate.text.toString()
                Log.d("endDate",endDate)
                val availabilityQuery = database.orderByChild("doctorId").equalTo(doctorId)
                availabilityQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (snapshot in dataSnapshot.children) {
                                val availabilityId = snapshot.key
                                if (availabilityId != null) {
                                    val doctorAvailability = DoctorAvailability(
                                        availabilityId,
                                        doctorId,startDate,endDate,
                                        if (doctorMon.isChecked) MutableList(12) { 1 } else MutableList(12) { 0 },
                                        if (doctorTue.isChecked) MutableList(12) { 1 } else MutableList(12) { 0 },
                                        if (doctorWed.isChecked) MutableList(12) { 1 } else MutableList(12) { 0 },
                                        if (doctorThu.isChecked) MutableList(12) { 1 } else MutableList(12) { 0 },
                                        if (doctorFri.isChecked) MutableList(12) { 1 } else MutableList(12) { 0 },
                                        if (doctorSat.isChecked) MutableList(12) { 1 } else MutableList(12) { 0 },
                                        if (doctorSun.isChecked) MutableList(12) { 1 } else MutableList(12) { 0 },
                                    ) // Update the existing DoctorAvailability entry
                                    database.child(availabilityId).setValue(doctorAvailability)
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                Toast.makeText(
                                                    applicationContext,
                                                    "Availability updated.",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            } else {
                                                Toast.makeText(
                                                    applicationContext,
                                                    "Failed to update availability.",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                }
                                break
                            }
                        } else {

                            val newAvailabilityRef = database.push()
                            val availabilityId = newAvailabilityRef.key

                            if (availabilityId != null) {
                                val doctorAvailability = DoctorAvailability(
                                    availabilityId,doctorId,startDate,endDate,

                                    if (doctorMon.isChecked) MutableList(12) { 1 } else MutableList(12) { 0 },
                                    if (doctorTue.isChecked) MutableList(12) { 1 } else MutableList(12) { 0 },
                                    if (doctorWed.isChecked) MutableList(12) { 1 } else MutableList(12) { 0 },
                                    if (doctorThu.isChecked) MutableList(12) { 1 } else MutableList(12) { 0 },
                                    if (doctorFri.isChecked) MutableList(12) { 1 } else MutableList(12) { 0 },
                                    if (doctorSat.isChecked) MutableList(12) { 1 } else MutableList(12) { 0 },
                                    if (doctorSun.isChecked) MutableList(12) { 1 } else MutableList(12) { 0 },
                                )


                                newAvailabilityRef.setValue(doctorAvailability)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Toast.makeText(
                                                applicationContext,
                                                "Availability set.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            Toast.makeText(
                                                applicationContext,
                                                "Failed to set availability.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                            }
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Handle error
                        Toast.makeText(
                            applicationContext,
                            "Database error occurred.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            }
        }
        hamburgerButton.setOnClickListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }

    }
    private fun setupDrawer() {
        hamburgerButton.setOnClickListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }
    }
    private fun setupNavigationView() {
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {

                    startActivity(Intent(this, DoctorHomeScreenActivity::class.java))
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, DoctorProfileActivity::class.java))
                }
                R.id.nav_availability -> {
                    startActivity(Intent(this, DoctorHomePageActivity::class.java))
                }
                R.id.nav_appointment -> {
                    startActivity(Intent(this, DoctorViewAppointment::class.java))
                }
                R.id.nav_prescription -> {
                    startActivity(Intent(this, DoctorViewPrescription::class.java))
                }

                R.id.nav_rating -> {
                    startActivity(Intent(this, DoctorViewRating::class.java))
                }
                R.id.nav_logout -> {
                    val sharedPrefs = getSharedPreferences("DoctorData", MODE_PRIVATE)
                    val editor = sharedPrefs.edit()
                    editor.clear()
                    editor.apply()

                    // Redirect to your login screen
                    val intent = Intent(this@DoctorHomePageActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }

            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun displayUploadedProfileImage() {
        val storageRef = storageReference.child("images/$doctorId/profile.jpg")
        storageRef.downloadUrl.addOnSuccessListener { uri ->

            Glide.with(this).load(uri).into(profileImage)

        }.addOnFailureListener { e ->
            // Handle any errors while fetching the image
            Log.e("PatientProfileActivity", "Failed to fetch image: ${e.message}")
        }
    }
    private fun openDatePicker(isStartDate: Boolean) {
        val constraintsBuilder = CalendarConstraints.Builder()

        val today = MaterialDatePicker.todayInUtcMilliseconds()
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calendar.timeInMillis = today

        // Set the minimum date to today to restrict selection
        val startDate = calendar.timeInMillis
        constraintsBuilder.setStart(startDate)

        val builder = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Date")
            .setSelection(today) // Pre-select today's date
            .setCalendarConstraints(constraintsBuilder.build())

        val picker = builder.build()

        picker.addOnPositiveButtonClickListener { selection ->
            val selectedCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            selectedCalendar.timeInMillis = selection

            val selectedDate = selectedCalendar.time

            // Validation: Ensure selected date is not in the past
            if (selectedDate.before(Date())) {
                // Display an error message or handle the invalid selection
                Toast.makeText(this, "Please select a date in the future.", Toast.LENGTH_SHORT).show()
                return@addOnPositiveButtonClickListener
            }

            val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

            if (isStartDate) {
                editTextStartDate.setText(sdf.format(selectedDate)).toString()
            } else {
                editTextEndDate.setText(sdf.format(selectedDate)).toString()
            }
        }

        picker.show(supportFragmentManager, picker.toString())
    }



}
