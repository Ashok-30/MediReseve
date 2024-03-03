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
import com.example.ehealthcareapp.patient.adapter.DoctorAdapter
import com.example.ehealthcareapp.patient.models.Doctors
import com.google.android.material.navigation.NavigationView

import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class PatientHomePageActivity : AppCompatActivity() {

    private lateinit var dbRef: DatabaseReference
    private lateinit var doctorRecyclerview: RecyclerView
    private lateinit var doctorArrayList: ArrayList<Doctors>
    private lateinit var displayMsg: TextView
    private lateinit var patientId:String
    private lateinit var profileImage: ImageView
    private lateinit var storageReference: StorageReference
    private  var patientName:String=""
    private lateinit var navigationView: NavigationView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var hamburgerButton: ImageButton




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.patienthomepage_activity)
        storageReference = FirebaseStorage.getInstance().reference
        val sharedPrefs = getSharedPreferences("UserData", MODE_PRIVATE)

        patientName = sharedPrefs.getString("first", "").toString()
        patientId = sharedPrefs.getString("patientId", "").toString()
        displayUploadedProfileImage()
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
        doctorRecyclerview = findViewById(R.id.doctorData)
        doctorRecyclerview.layoutManager = LinearLayoutManager(this)
        doctorRecyclerview.setHasFixedSize(true)
        displayMsg = findViewById(R.id.displayMsg)
        displayMsg.text = "Welcome, $patientName"

        doctorArrayList = ArrayList()
        getDoctorData()
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

    private fun getDoctorData() {

        dbRef = FirebaseDatabase.getInstance().getReference("Doctors")

        dbRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {

                    for (doctorSnapshot in snapshot.children) {
                        val doctor = doctorSnapshot.getValue(Doctors::class.java)
                        doctorArrayList.add(doctor!!)
                    }

                    if (doctorArrayList.isEmpty()) {
                        // Show an alert message here
                        showAlert("Sorry! Currently No doctors available.")
                    } else {
                        val adapter = DoctorAdapter(this@PatientHomePageActivity, doctorArrayList, patientId, patientName)
                        doctorRecyclerview.adapter = adapter
                    }

                } else {
                    // Show an alert message here
                    showAlert("No data available.")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle onCancelled event
                // You might want to show an alert message here as well
                showAlert("Database error: ${error.message}")
            }
        })
    }

    private fun showAlert(message: String) {
        val alertDialog = AlertDialog.Builder(this)
            .setTitle("Doctors")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        alertDialog.show()
    }

    private fun displayUploadedProfileImage() {
        val storageRef = storageReference.child("images/$patientId/profile.jpg")
        storageRef.downloadUrl.addOnSuccessListener { uri ->

            Glide.with(this).load(uri).into(profileImage)

//            // Or set the image URI directly
//            profileImage.setImageURI(uri)
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
                R.id.nav_vip -> {
                    startActivity(Intent(this, PatientVIPActivity::class.java))
                }
                R.id.nav_prescription -> {
                    startActivity(Intent(this, MedicineActivity::class.java))
                }
                R.id.nav_bmi -> {
                    startActivity(Intent(this, PatientBMIActivity::class.java))
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, PatientProfileActivity::class.java))
                }
                R.id.nav_logout -> {
                    val sharedPrefs = getSharedPreferences("UserData", MODE_PRIVATE)
                    val editor = sharedPrefs.edit()
                    editor.clear()
                    editor.apply()

                    // Redirect to your login screen
                    val intent = Intent(this@PatientHomePageActivity, MainActivity::class.java)
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








