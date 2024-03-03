package com.example.ehealthcareapp.patient

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.example.ehealthcareapp.MainActivity
import com.google.firebase.database.*
import com.example.ehealthcareapp.R
import com.example.ehealthcareapp.patient.models.PatientData
import com.google.android.material.navigation.NavigationView
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class PatientProfileActivity : AppCompatActivity() {

    private lateinit var databaseReference: DatabaseReference
    private lateinit var navigationView: NavigationView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var hamburgerButton: ImageButton
    private lateinit var uploadButton: Button
    private lateinit var profileImage: ImageView
    private lateinit var profilePhoto:ImageView
    private lateinit var storageReference: StorageReference
    private  var patientName:String=""
    private lateinit var patientId: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.patient_profile)
        uploadButton = findViewById(R.id.upload)
        profilePhoto = findViewById(R.id.profilePhoto)
        storageReference = FirebaseStorage.getInstance().reference
        val sharedPrefs = getSharedPreferences("UserData", MODE_PRIVATE)
        patientId = sharedPrefs.getString("patientId", "").toString()

        // Set onClickListener for upload button
        uploadButton.setOnClickListener {
            openGalleryForImage()
        }
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

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().reference

        // Call function to fetch patient data
        fetchPatientData()
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

    private fun fetchPatientData() {
        val sharedPrefs = getSharedPreferences("UserData", MODE_PRIVATE)
        val patientId = sharedPrefs.getString("patientId", "").toString()

        val patientDataRef = databaseReference.child("Patients").child(patientId)

        patientDataRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val patientData = dataSnapshot.getValue(PatientData::class.java)

                    // Access the TextViews and set data
                    findViewById<TextView>(R.id.first).text = patientData?.first ?: ""
                    findViewById<TextView>(R.id.firstName).text = patientData?.first ?: ""
                    findViewById<TextView>(R.id.surName).text = patientData?.last ?: ""
                    findViewById<TextView>(R.id.niNumber).text = patientData?.niNumber ?: ""
                    findViewById<TextView>(R.id.gender).text = patientData?.gender ?: ""
                    findViewById<TextView>(R.id.phone).text = patientData?.phone ?: ""
                    findViewById<TextView>(R.id.email).text = patientData?.email ?: ""
                    findViewById<TextView>(R.id.type).text = patientData?.type ?: ""
                } else {

                    Log.e("PatientProfileActivity", "Patient data not found")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle an error while fetching data
                Log.e("PatientProfileActivity", "Error: ${databaseError.message}")
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
                R.id.nav_logout -> {
                    val sharedPrefs = getSharedPreferences("UserData", MODE_PRIVATE)
                    val editor = sharedPrefs.edit()
                    editor.clear()
                    editor.apply()

                    // Redirect to your login screen
                    val intent = Intent(this@PatientProfileActivity, MainActivity::class.java)
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
    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val imageUri: Uri? = data?.data
            imageUri?.let { uri ->
                // Upload the selected image to Firebase Storage with patientId
                val storageRef = storageReference.child("images/$patientId/profile.jpg")
                storageRef.putFile(uri)
                    .addOnSuccessListener {

                        profilePhoto.setImageURI(uri)
                    }
                    .addOnFailureListener { e ->
                        // Log error message
                        Log.e("PatientProfileActivity", "Image upload failed: ${e.message}")
                    }
            }
        }
    }

    companion object {
        private const val REQUEST_CODE = 42
    }

    private fun displayUploadedProfileImage() {
        val storageRef = storageReference.child("images/$patientId/profile.jpg")
        storageRef.downloadUrl.addOnSuccessListener { uri ->

             Glide.with(this).load(uri).into(profileImage)
            Glide.with(this).load(uri).into(profilePhoto)

        }.addOnFailureListener { e ->
            // Handle any errors while fetching the image
            Log.e("PatientProfileActivity", "Failed to fetch image: ${e.message}")
        }
    }
}
