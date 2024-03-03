package com.example.ehealthcareapp.patient


import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView

import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.example.ehealthcareapp.MainActivity
import com.example.ehealthcareapp.R
import com.google.android.material.navigation.NavigationView
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class PatientVIPActivity : AppCompatActivity() {
    private lateinit var patientId: String
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var hamburgerButton: ImageButton
    private lateinit var navigationView: NavigationView
    private lateinit var payment: Button
    private lateinit var displayMsg: TextView
    private  var patientName:String=""
    private lateinit var profileImage: ImageView
    private lateinit var storageReference: StorageReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.patient_vip_activity)
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
        val userNameTextView = headerView.findViewById<TextView>(R.id.userName)
        val emailTextView = headerView.findViewById<TextView>(R.id.emailTextView)

        userNameTextView.text = first
        emailTextView.text = email
        payment = findViewById(R.id.payment)
        patientName = sharedPrefs.getString("first", "").toString()
        displayMsg = findViewById(R.id.displayMsg)
        displayMsg.text = "$patientName"
        val cardDetailsRef = FirebaseDatabase.getInstance().getReference("CardDetails")
        cardDetailsRef.orderByChild("patientId").equalTo(patientId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        payment.isEnabled=false
                        val dialog = AlertDialog.Builder(this@PatientVIPActivity)
                            .setTitle("Already a VIP")
                            .setMessage("You're already a VIP. Enjoy the benefits")
                            .setPositiveButton("Okay") { _, _ ->

                            }
                            .create()

                        dialog.show()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            })
        payment.setOnClickListener {
            val intent = Intent(this@PatientVIPActivity, PatientPaymentActivity::class.java)
            startActivity(intent)
        }

        setupDrawer()
        setupNavigationView()
        val vipBenefitsText = "<ul>" +
                "<li><b>Priority Scheduling:</b> Jump the queue with priority time slots for appointments.</li>" +
                "<li><b>24/7 Access:</b> Access to round-the-clock consultation or support.</li>" +
                "<li><b>Dedicated Support:</b> Direct and personalized support from a dedicated team.</li>" +
                "<li><b>Exclusive Discounts:</b> Special offers on appointments, prescriptions, or healthcare services.</li>" +
                "<li><b>Health Trackers:</b> Access to additional health tracking features or tools.</li>" +
                "<li><b>Extended Consultation Time:</b> Longer consultations for comprehensive discussions.</li>" +
                "<li><b>Specialist Referrals:</b> Assistance in getting referrals to specialists swiftly.</li>" +
                "<li><b>Health Insights & Tips:</b> Regular health insights, tips, and newsletters.</li>" +
                "<li><b>Concierge Services:</b> Exclusive concierge or appointment booking services.</li>" +
                "<li><b>Priority Lab Results:</b> Expedited access to lab results or diagnostic reports.</li>" +
                "</ul>"

        val vipBenefitsTextView: TextView = findViewById(R.id.vipBenefitsTextView)
        vipBenefitsTextView.text = Html.fromHtml(vipBenefitsText)



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
                R.id.nav_bmi -> {
                    startActivity(Intent(this, PatientBMIActivity::class.java))
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
                    val intent = Intent(this@PatientVIPActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }

            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }
}




