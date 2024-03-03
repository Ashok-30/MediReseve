package com.example.ehealthcareapp.patient



import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.example.ehealthcareapp.MainActivity
import com.example.ehealthcareapp.R
import com.google.android.material.navigation.NavigationView
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class PatientBMIActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var hamburgerButton: ImageButton
    private lateinit var navigationView: NavigationView
    private lateinit var dtViewCardView: CardView
    private lateinit var profileImage: ImageView
    private lateinit var storageReference: StorageReference
    private lateinit var patientId: String
    private val preferencesKey = "isDialogShown"
    private val sharedPreferences by lazy {
        getSharedPreferences("showDialog", Context.MODE_PRIVATE)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.patient_bmi_activity)

        storageReference = FirebaseStorage.getInstance().reference
        val sharedPrefs = getSharedPreferences("UserData", MODE_PRIVATE)
        patientId = sharedPrefs.getString("patientId", "").toString()
        displayUploadedProfileImage()
        val weightT = findViewById<EditText>(R.id.gwText)
        val heightT = findViewById<EditText>(R.id.ghText)
        val calBtn = findViewById<Button>(R.id.calBtn)
        dtViewCardView = findViewById(R.id.dtView)
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

        dtViewCardView.visibility = View.INVISIBLE
        setupDrawer()
        setupNavigationView()
        val isDialogShown = sharedPreferences.getBoolean(preferencesKey, false)

        // Show dialog only if it hasn't been shown before
        if (!isDialogShown) {
            showDialog()

            // Update the flag to indicate that the dialog has been shown
            sharedPreferences.edit().putBoolean(preferencesKey, true).apply()
        }
        calBtn.setOnClickListener {
            val weight= weightT.text.toString()
            val height = heightT.text.toString()
            if (validInput(weight,height)) {
                val bmi = weight.toFloat() / ((height.toFloat() / 100) * (height.toFloat() / 100))
                val bmi2D = String.format("%.2f", bmi).toFloat()
                displayResult(bmi2D)
                dtViewCardView.visibility = View.VISIBLE
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
                    val intent = Intent(this@PatientBMIActivity, MainActivity::class.java)
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
    private fun validInput(weight:String?,height:String?):Boolean{
        return when{
            weight.isNullOrEmpty() ->{
                Toast.makeText(this,"Weight is empty",Toast.LENGTH_SHORT).show()
                return false
            }
            height.isNullOrEmpty() ->{
                Toast.makeText(this,"Height is empty",Toast.LENGTH_SHORT).show()
                return false
            }

            else ->{
                return true
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

    private fun displayResult(bmi:Float){
        val resultV= findViewById<TextView>(R.id.resultVidw)
        val resultDescV= findViewById<TextView>(R.id.resultDesView)
        val condViewV= findViewById<TextView>(R.id.resultVidwCondition)

        resultV.text=bmi.toString()
        condViewV.text= "(Normal range is 18.5 - 24.9)"

        var resultText= ""
        var color=0

        when{
            bmi<18.50 ->{
                resultText="Underweight"
                color= R.color.uw
            }
            bmi in 18.50..24.99 ->{
                resultText="Normal"
                color= R.color.nor
            }
            bmi in 25.00..29.99 ->{
                resultText="Overweight"
                color= R.color.over
            }

            bmi > 29.99 ->{
                resultText="Obese"
                color= R.color.oves
            }
        }
        resultDescV.setTextColor(ContextCompat.getColor(this,color))
        resultDescV.text= resultText;

    }
    private fun showDialog() {
        val dialog = AlertDialog.Builder(this)
            .setTitle("BMI Calculator")
            .setMessage("Calculate your BMI and Stay Healthy")
            .setPositiveButton("Okay") { _, _ -> }
            .create()

        dialog.show()
    }
}
