package com.example.ehealthcareapp.doctor

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
import com.example.ehealthcareapp.doctor.models.DoctorData

import com.google.android.material.navigation.NavigationView
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class DoctorProfileActivity : AppCompatActivity() {

    private lateinit var databaseReference: DatabaseReference
    private lateinit var navigationView: NavigationView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var hamburgerButton: ImageButton
    private lateinit var uploadButton: Button

    private lateinit var profilePhoto: ImageView
    private lateinit var profileImage: ImageView
    private lateinit var storageReference: StorageReference
    private lateinit var doctorId: String
    private  var doctorName:String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.doctor_profile)
        uploadButton = findViewById(R.id.upload)
        profilePhoto = findViewById(R.id.profilePhoto)
        storageReference = FirebaseStorage.getInstance().reference
        val sharedPrefs = getSharedPreferences("DoctorData", MODE_PRIVATE)
        doctorId = sharedPrefs.getString("doctorId", "").toString()

        // Set onClickListener for upload button
        uploadButton.setOnClickListener {
            openGalleryForImage()
        }
        displayUploadedProfileImage()

        doctorName = sharedPrefs.getString("first", "").toString()


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
        fetchDoctorData()
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

    private fun fetchDoctorData() {
        val sharedPrefs = getSharedPreferences("DoctorData", MODE_PRIVATE)
        val doctorId = sharedPrefs.getString("doctorId", "").toString()

        val doctorDataRef = databaseReference.child("Doctors").child(doctorId)

        doctorDataRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val doctorData = dataSnapshot.getValue(DoctorData::class.java)

                    // Access the TextViews and set data
                    findViewById<TextView>(R.id.first).text = doctorData?.first ?: ""
                    findViewById<TextView>(R.id.firstName).text = doctorData?.first ?: ""
                    findViewById<TextView>(R.id.surName).text = doctorData?.last ?: ""
                    findViewById<TextView>(R.id.doctorNum).text = doctorData?.doctornum ?: ""
                    findViewById<TextView>(R.id.specialization).text = doctorData?.specialization ?: ""
                    findViewById<TextView>(R.id.phone).text = doctorData?.phone ?: ""
                    findViewById<TextView>(R.id.email).text = doctorData?.email ?: ""

                } else {

                    Log.e("DoctorProfileActivity", "Doctor data not found")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle an error while fetching data
                Log.e("DoctorProfileActivity", "Error: ${databaseError.message}")
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
                    val intent = Intent(this@DoctorProfileActivity, MainActivity::class.java)
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
                val storageRef = storageReference.child("images/$doctorId/profile.jpg")
                storageRef.putFile(uri)
                    .addOnSuccessListener {
                        // Image uploaded successfully
                        // Display the uploaded image in ImageView
                        profilePhoto.setImageURI(uri)
                    }
                    .addOnFailureListener { e ->
                        // Handle unsuccessful uploads
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
        val storageRef = storageReference.child("images/$doctorId/profile.jpg")
        storageRef.downloadUrl.addOnSuccessListener { uri ->

            Glide.with(this).load(uri).into(profileImage)
            Glide.with(this).load(uri).into(profilePhoto)


        }.addOnFailureListener { e ->
            // Handle any errors while fetching the image
            Log.e("PatientProfileActivity", "Failed to fetch image: ${e.message}")
        }
    }
}
