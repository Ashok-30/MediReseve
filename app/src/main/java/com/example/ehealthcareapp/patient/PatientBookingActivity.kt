package com.example.ehealthcareapp.patient


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.ehealthcareapp.R
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ehealthcareapp.MainActivity
import com.example.ehealthcareapp.patient.adapter.BookingAdapter

import com.example.ehealthcareapp.patient.models.Booking

import com.google.android.material.navigation.NavigationView

import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class PatientBookingActivity : AppCompatActivity() {

    private lateinit var bookingRecyclerView: RecyclerView
    private lateinit var bookingArrayList: ArrayList<Booking>
    private lateinit var displayMsg: TextView
    private lateinit var navigationView: NavigationView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var hamburgerButton: ImageButton
    private lateinit var profileImage: ImageView
    private lateinit var storageReference: StorageReference
    private lateinit var patientId: String
    private  var patientName:String=""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.patient_booking_activity)
        storageReference = FirebaseStorage.getInstance().reference
        val sharedPrefs = getSharedPreferences("UserData", MODE_PRIVATE)
        patientId = sharedPrefs.getString("patientId", "").toString()
        displayUploadedProfileImage()

        patientName = sharedPrefs.getString("first", "").toString()



        drawerLayout = findViewById(R.id.drawer_layout)
        hamburgerButton = findViewById(R.id.hamburgerButton)
        navigationView = findViewById(R.id.navigation_view)

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
        displayMsg.text = "Welcome, $patientName"


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
        val sharedPrefs = getSharedPreferences("UserData", MODE_PRIVATE)
        val patientId = sharedPrefs.getString("patientId", "")

        val dbRef = FirebaseDatabase.getInstance().reference

        dbRef.child("Booking").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val filteredBookings = ArrayList<Booking>()

                for (bookingSnapshot in snapshot.children) {
                    var booking = bookingSnapshot.getValue(Booking::class.java)
                    booking = booking?.copy(bookingId = bookingSnapshot.key ?: "")

                    if (booking != null && booking.patientId == patientId && booking.pstatus == "0") {
                        dbRef.child("Doctors").child(booking.doctorId)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(doctorSnapshot: DataSnapshot) {
                                    val doctorName = doctorSnapshot.child("first").getValue(String::class.java)
                                    booking.doctorName = doctorName ?: ""

                                    // Fetch patient's name based on patientId
                                    dbRef.child("Patients").child(booking.patientId)
                                        .addListenerForSingleValueEvent(object : ValueEventListener {
                                            override fun onDataChange(patientSnapshot: DataSnapshot) {
                                                val patientName =
                                                    patientSnapshot.child("first").getValue(String::class.java)
                                                booking.patientName = patientName ?: ""

                                                filteredBookings.add(booking) // Add updated booking to the list
                                                val adapter = BookingAdapter(filteredBookings)
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

            }

            override fun onCancelled(error: DatabaseError) {
                // Handle cancellation if needed
            }
        })
    }




    private fun displayUploadedProfileImage() {
        val storageRef = storageReference.child("images/$patientId/profile.jpg")
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

                    startActivity(Intent(this, PatientHomeScreen::class.java))
                }
                R.id.nav_bookings -> {
                    startActivity(Intent(this, PatientHomePageActivity::class.java))
                }
                R.id.nav_appointment -> {
                    startActivity(Intent(this, PatientBookingActivity::class.java))
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, PatientProfileActivity::class.java))
                }
                R.id.nav_bmi -> {
                    startActivity(Intent(this, PatientBMIActivity::class.java))
                }
                R.id.nav_vip -> {
                    startActivity(Intent(this, PatientVIPActivity::class.java))
                }
                R.id.nav_prescription -> {
                    startActivity(Intent(this, MedicineActivity::class.java))
                }
                R.id.nav_logout -> {
                    val sharedPrefs = getSharedPreferences("UserData", MODE_PRIVATE)
                    val editor = sharedPrefs.edit()
                    editor.clear()
                    editor.apply()

                    // Redirect to your login screen
                    val intent = Intent(this@PatientBookingActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }

            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }
    private fun showAlert(message: String) {
        val alertDialog = AlertDialog.Builder(this)
            .setTitle("Appointments")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        alertDialog.show()
    }
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}








