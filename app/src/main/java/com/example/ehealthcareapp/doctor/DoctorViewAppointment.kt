package com.example.ehealthcareapp.doctor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.ehealthcareapp.R
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ehealthcareapp.MainActivity
import com.example.ehealthcareapp.doctor.adapter.AppointmentAdapter
import com.example.ehealthcareapp.patient.models.Booking
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DoctorViewAppointment : AppCompatActivity() {

    private lateinit var bookingRecyclerView: RecyclerView
    private lateinit var bookingArrayList: ArrayList<Booking>
    private lateinit var displayMsg: TextView
    private lateinit var navigationView: NavigationView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var hamburgerButton: ImageButton
    private lateinit var profileImage: ImageView
    private lateinit var storageReference: StorageReference
    private lateinit var doctorId: String

    private  var doctorName:String=""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.doctor_viewappointment)
        val sharedPrefs = getSharedPreferences("DoctorData", MODE_PRIVATE)
        storageReference = FirebaseStorage.getInstance().reference
        doctorName = sharedPrefs.getString("first", "").toString()

        drawerLayout = findViewById(R.id.drawer_layout)
        hamburgerButton = findViewById(R.id.hamburgerButton)
        navigationView = findViewById(R.id.navigation_view)
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

        bookingRecyclerView = findViewById(R.id.bookingData)
        bookingRecyclerView.layoutManager = LinearLayoutManager(this)
        bookingRecyclerView.setHasFixedSize(true)
        displayMsg = findViewById(R.id.displayMsg)
        displayMsg.text = "Welcome, $doctorName"


        bookingArrayList = ArrayList()
        getBookingData()
        setupDrawer()
        setupNavigationView()

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

    private fun getBookingData() {
        val sharedPrefs = getSharedPreferences("DoctorData", MODE_PRIVATE)
        val doctorId = sharedPrefs.getString("doctorId", "")

        val dbRef = FirebaseDatabase.getInstance().reference

        dbRef.child("Booking").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val filteredBookings = ArrayList<Booking>()

                var openAppointmentFound = false // Flag to check if any open appointments are found

                val currentDate = getCurrentDate() // Get current date in DD-MM-YYYY format

                for (bookingSnapshot in snapshot.children) {
                    var booking = bookingSnapshot.getValue(Booking::class.java)
                    booking = booking?.copy(bookingId = bookingSnapshot.key ?: "")

                    if (booking != null && booking.doctorId == doctorId && booking.dstatus == "0" &&
                        isCurrentDateBeforeSelectedDate(currentDate, booking.selectedDate)) {
                        openAppointmentFound = true // Set flag to true if open appointment is found

                        dbRef.child("Patients").child(booking.patientId)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(doctorSnapshot: DataSnapshot) {
                                    val patientName = doctorSnapshot.child("first").getValue(String::class.java)
                                    booking.patientName = patientName ?: ""
                                    // Fetch patient's name based on patientId
                                    dbRef.child("Doctors").child(booking.doctorId).addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onDataChange(patientSnapshot: DataSnapshot) {
                                            val doctorName = patientSnapshot.child("first").getValue(String::class.java)
                                            booking.doctorName = doctorName ?: ""

                                            filteredBookings.add(booking) // Add updated booking to the list
                                            val adapter = AppointmentAdapter(filteredBookings)
                                            bookingRecyclerView.adapter = adapter
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            // Handle cancellation if needed
                                        }
                                    })
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    // Handle cancellation if needed
                                }
                            })
                    }
                }


                if (!openAppointmentFound) {
                    val alertDialogBuilder = AlertDialog.Builder(this@DoctorViewAppointment)


                    alertDialogBuilder.setTitle("Appointments Alert")
                    alertDialogBuilder.setMessage("Currently No open Appointments.")

//                    alertDialogBuilder.setPositiveButton("Okay") { dialog, which ->
//
//                         val intent = Intent(this@DoctorViewAppointment, DoctorHomeScreenActivity::class.java)
//                         startActivity(intent)
//                    }

                    val alertDialog = alertDialogBuilder.create()

                    // Get the positive button from the AlertDialog
                    alertDialog.setOnShowListener {
                        val button = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        button.setTextColor(ContextCompat.getColor(this@DoctorViewAppointment, R.color.white))
                        button.setBackgroundColor(ContextCompat.getColor(this@DoctorViewAppointment, R.color.button))
                    }

                    // Show the alert dialog
                    alertDialog.show()
                }

            }

            override fun onCancelled(error: DatabaseError) {
                // Handle cancellation if needed
            }
        })
    }

    // Function to get current date in DD-MM-YYYY format
    private fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    // Function to check if the current date is before the selected date
    private fun isCurrentDateBeforeSelectedDate(currentDate: String, selectedDate: String): Boolean {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val current = dateFormat.parse(currentDate)
        val selected = dateFormat.parse(selectedDate)
        return current.before(selected)
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
                    val intent = Intent(this@DoctorViewAppointment, MainActivity::class.java)
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
}








