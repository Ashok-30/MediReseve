package com.example.ehealthcareapp.patient




import android.content.Intent

import com.example.ehealthcareapp.R
import android.os.Bundle

import android.util.Log



import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView

import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.example.ehealthcareapp.MainActivity


import com.google.android.material.navigation.NavigationView


import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference



class PatientHomeScreen : AppCompatActivity() {
    private lateinit var navigationView: NavigationView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var hamburgerButton: ImageButton

    private lateinit var profileImage: ImageView
    private lateinit var storageReference: StorageReference
    private lateinit var patientId: String






    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.patient_homescreen)


        // Initialize ImageViews
        val profileImageView: ImageView = findViewById(R.id.profileImageView)
        val availabilityImageView: ImageView = findViewById(R.id.availabilityImageView)
        val prescriptionsImageView: ImageView = findViewById(R.id.prescriptionsImageView)
        val appointmentsImageView: ImageView = findViewById(R.id.appointmentsImageView)

        // Set click listeners for each ImageView
        profileImageView.setOnClickListener {
            // Handle navigation to the profile page
            startActivity(Intent(this@PatientHomeScreen, PatientProfileActivity::class.java))
        }

        availabilityImageView.setOnClickListener {
            // Handle navigation to the availability page
            startActivity(Intent(this@PatientHomeScreen, PatientHomePageActivity::class.java))
        }

        prescriptionsImageView.setOnClickListener {
            // Handle navigation to the prescriptions page
            startActivity(Intent(this@PatientHomeScreen, MedicineActivity::class.java))
        }

        appointmentsImageView.setOnClickListener {
            // Handle navigation to the appointments page
            startActivity(Intent(this@PatientHomeScreen, PatientBookingActivity::class.java))
        }

        drawerLayout = findViewById(R.id.drawer_layout)
        hamburgerButton = findViewById(R.id.hamburgerButton)
        navigationView = findViewById(R.id.navigation_view)
        storageReference = FirebaseStorage.getInstance().reference
        val sharedPrefs = getSharedPreferences("UserData", MODE_PRIVATE)
        patientId = sharedPrefs.getString("patientId", "").toString()
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
                R.id.nav_profile -> {
                    startActivity(Intent(this, PatientProfileActivity::class.java))
                }
                R.id.nav_bmi -> {
                    startActivity(Intent(this, PatientBMIActivity::class.java))
                }
                R.id.nav_logout -> {
                    val sharedPrefs = getSharedPreferences("UserData", MODE_PRIVATE)
                    val editor = sharedPrefs.edit()
                    editor.clear()
                    editor.apply()

                    // Redirect to your login screen
                    val intent = Intent(this@PatientHomeScreen, MainActivity::class.java)
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
        val storageRef = storageReference.child("images/$patientId/profile.jpg")
        storageRef.downloadUrl.addOnSuccessListener { uri ->

            Glide.with(this).load(uri).into(profileImage)

        }.addOnFailureListener { e ->
            // Handle any errors while fetching the image
            Log.e("PatientProfileActivity", "Failed to fetch image: ${e.message}")
        }
    }


}