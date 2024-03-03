package com.example.ehealthcareapp.patient

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ehealthcareapp.MainActivity
import com.example.ehealthcareapp.R
import com.example.ehealthcareapp.patient.adapter.PrescriptionAdapter
import com.example.ehealthcareapp.patient.models.Prescription
import com.google.android.material.navigation.NavigationView

import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class PatientPrescriptionActivity : AppCompatActivity() {

    private lateinit var prescriptionRecyclerView: RecyclerView
    private lateinit var prescriptionAdapter: PrescriptionAdapter
    private lateinit var prescriptionList: ArrayList<Prescription>

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var hamburgerButton: ImageButton
    private lateinit var navigationView: NavigationView
    private lateinit var profileImage: ImageView
    private lateinit var storageReference: StorageReference
    private lateinit var displayMsg: TextView
    private lateinit var patientId: String
    private  var patientName:String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.patient_prescription_activity)

        drawerLayout = findViewById(R.id.drawer_layout)
        hamburgerButton = findViewById(R.id.hamburgerButton)
        navigationView = findViewById(R.id.navigation_view)
        val sharedPrefs = getSharedPreferences("UserData", MODE_PRIVATE)
        storageReference = FirebaseStorage.getInstance().reference
        patientId = sharedPrefs.getString("patientId", "").toString()
        val first = sharedPrefs.getString("first", "").toString()
        val email = sharedPrefs.getString("email", "").toString()
        val headerView = navigationView.getHeaderView(0)
        profileImage = headerView.findViewById(R.id.profileImage)
        displayUploadedProfileImage()
        setupDrawer()
        setupNavigationView()
        val userNameTextView = headerView.findViewById<TextView>(R.id.userName)
        val emailTextView = headerView.findViewById<TextView>(R.id.emailTextView)

        userNameTextView.text = first
        emailTextView.text = email
        patientName = sharedPrefs.getString("first", "").toString()
        displayMsg = findViewById(R.id.displayMsg)
        displayMsg.text = "$patientName"

        prescriptionRecyclerView = findViewById(R.id.prescriptionRecyclerView)
        prescriptionRecyclerView.layoutManager = LinearLayoutManager(this)
        prescriptionList = ArrayList()
        prescriptionAdapter = PrescriptionAdapter(prescriptionList)
        prescriptionRecyclerView.adapter = prescriptionAdapter

        val bookingId = intent.getStringExtra("bookingId") ?: ""
        Log.d("bookingId",bookingId)
        fetchPrescriptions(bookingId)
    }

    private fun fetchPrescriptions(bookingId: String) {
        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Prescriptions")
        val query: Query = databaseReference.orderByChild("bookingId").equalTo(bookingId)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                prescriptionList.clear()

                for (snapshot in dataSnapshot.children) {
                    val prescription: Prescription? = snapshot.getValue(Prescription::class.java)
                    prescription?.let { prescriptionList.add(it) }
                }

                // Log the size of the prescriptionList here to check if data is added
                Log.d("PrescriptionAdapter", "Prescription List Size: ${prescriptionList.size}")

                // Notify the adapter about the data change
                prescriptionAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle the error
                Log.e("PrescriptionAdapter", "Error fetching prescriptions: ${databaseError.message}")
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
                    val intent = Intent(this@PatientPrescriptionActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }

            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }


}
