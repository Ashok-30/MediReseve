package com.example.ehealthcareapp.doctor

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog

import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ehealthcareapp.MainActivity
import com.example.ehealthcareapp.R
import com.example.ehealthcareapp.doctor.adapter.RatingAdapter

import com.example.ehealthcareapp.patient.models.Rating
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class DoctorViewRating : AppCompatActivity() {

    private lateinit var ratingRecyclerView: RecyclerView
    private lateinit var ratingList: ArrayList<Rating>
    private lateinit var adapter: RatingAdapter
    private lateinit var displayMsg: TextView
    private lateinit var navigationView: NavigationView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var hamburgerButton: ImageButton
    private  var doctorName:String=""
    private lateinit var profileImage: ImageView
    private lateinit var storageReference: StorageReference
    private lateinit var doctorId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.doctor_view_rating)
        val sharedPrefs = getSharedPreferences("DoctorData", MODE_PRIVATE)
        storageReference = FirebaseStorage.getInstance().reference
        doctorName = sharedPrefs.getString("first", "").toString()
        doctorId = sharedPrefs.getString("doctorId", "").toString()

        drawerLayout = findViewById(R.id.drawer_layout)
        hamburgerButton = findViewById(R.id.hamburgerButton)
        navigationView = findViewById(R.id.navigation_view)
        displayUploadedProfileImage()
        val first = sharedPrefs.getString("first", "").toString()
        val email = sharedPrefs.getString("email", "").toString()
        val headerView = navigationView.getHeaderView(0)
        profileImage = headerView.findViewById(R.id.profileImage)
        val userNameTextView = headerView.findViewById<TextView>(R.id.userName)
        val emailTextView = headerView.findViewById<TextView>(R.id.emailTextView)

        userNameTextView.text = first
        emailTextView.text = email
        displayMsg = findViewById(R.id.displayMsg)
        displayMsg.text = "Welcome, $doctorName"

        ratingRecyclerView = findViewById(R.id.bookingData)
        ratingRecyclerView.layoutManager = LinearLayoutManager(this)
        ratingRecyclerView.setHasFixedSize(true)
        ratingList = ArrayList()
        adapter = RatingAdapter(ratingList) // Initialize adapter
        ratingRecyclerView.adapter = adapter
        getRatingData()
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

    private fun getRatingData() {
        val sharedPrefs = getSharedPreferences("DoctorData", MODE_PRIVATE)
        val doctorId = sharedPrefs.getString("doctorId", "")

        val dbRef = FirebaseDatabase.getInstance().reference
        val query: Query = dbRef.child("Rating").orderByChild("doctorId").equalTo(doctorId)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                ratingList.clear()
                for (snapshot in dataSnapshot.children) {
                    val rating: Rating? = snapshot.getValue(Rating::class.java)
                    rating?.let { ratingList.add(it) }
                }
                if (ratingList.isEmpty()) {
                    val alertDialogBuilder = AlertDialog.Builder(this@DoctorViewRating)
                    alertDialogBuilder.apply {
                        setTitle("No Ratings Available")
                        setMessage("There are currently no ratings available for you.")
//                        setPositiveButton("OK") { dialog, _ ->
//                            dialog.dismiss()
//                            // Navigate to DoctorHomePageActivity
//                            val intent = Intent(this@DoctorViewRating, DoctorHomeScreenActivity::class.java)
//                            startActivity(intent)
//                        }
                    }
                    val alertDialog: AlertDialog = alertDialogBuilder.create()
                    alertDialog.show()
                } else {
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle cancellation
            }
        })
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
                    val intent = Intent(this@DoctorViewRating, MainActivity::class.java)
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
