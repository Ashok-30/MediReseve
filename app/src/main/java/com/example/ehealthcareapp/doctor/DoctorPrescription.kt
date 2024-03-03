package com.example.ehealthcareapp.doctor

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.CheckBox
import com.example.ehealthcareapp.R
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.ehealthcareapp.MainActivity
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class DoctorPrescription : AppCompatActivity() {
    private lateinit var database: DatabaseReference

    private lateinit var medicineContainer: LinearLayout
    private lateinit var navigationView: NavigationView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var hamburgerButton: ImageButton
    private  var doctorName:String=""
    private  var patientName:String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.doctor_prescription)
        database = FirebaseDatabase.getInstance().reference
        val sharedPrefs = getSharedPreferences("DoctorData", MODE_PRIVATE)

        doctorName = sharedPrefs.getString("first", "").toString()
        patientName = intent.getStringExtra("patientName") ?: ""
        findViewById<TextView>(R.id.patientName).text = patientName
        drawerLayout = findViewById(R.id.drawer_layout)
        hamburgerButton = findViewById(R.id.hamburgerButton)
        navigationView = findViewById(R.id.navigation_view)

        val first = sharedPrefs.getString("first", "").toString()
        val email = sharedPrefs.getString("email", "").toString()
        val headerView = navigationView.getHeaderView(0)
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

        val buttonAddMedicine: Button = findViewById(R.id.buttonAddMedicine)
        medicineContainer = findViewById(R.id.medicineContainer)

        buttonAddMedicine.setOnClickListener {
            addMedicineSet()
        }
        val buttonSubmit: Button = findViewById(R.id.buttonSubmit)
        buttonSubmit.setOnClickListener {
            savePrescriptionData()
        }
    }

    private fun addMedicineSet() {
        val inflater = LayoutInflater.from(this)
        val medicineSetLayout = inflater.inflate(R.layout.medicine_set_layout, null) as LinearLayout

        val editTextMedication = medicineSetLayout.findViewById<EditText>(R.id.editTextMedication)
        val checkBoxMorning = medicineSetLayout.findViewById<CheckBox>(R.id.checkBoxMorning)
        val checkBoxAfternoon = medicineSetLayout.findViewById<CheckBox>(R.id.checkBoxAfternoon)
        val checkBoxNight = medicineSetLayout.findViewById<CheckBox>(R.id.checkBoxNight)

        // Optionally modify IDs, tags, or other properties of the dynamically added views

        // Add this new medicine set to the main container
        medicineContainer.addView(medicineSetLayout)
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
    private fun savePrescriptionData() {
        val patientName = findViewById<TextView>(R.id.patientName).text.toString()
        val bookingId = intent.getStringExtra("bookingId") ?: ""
        val patientId = intent.getStringExtra("patientId") ?: ""

        val bookingDate = intent.getStringExtra("bookingDate") ?: ""
        val bookingTime = intent.getStringExtra("bookingTime") ?: ""
        val prescriptionId = database.child("Prescriptions").push().key

        if (prescriptionId != null) {
            val prescriptionData = HashMap<String, Any>()
            prescriptionData["patientName"] = patientName

            for (i in 0 until medicineContainer.childCount) {
                val medicineLayout = medicineContainer.getChildAt(i)
                val editTextMedication = medicineLayout.findViewById<EditText>(R.id.editTextMedication)
                val checkBoxMorning = medicineLayout.findViewById<CheckBox>(R.id.checkBoxMorning)
                val checkBoxAfternoon = medicineLayout.findViewById<CheckBox>(R.id.checkBoxAfternoon)
                val checkBoxNight = medicineLayout.findViewById<CheckBox>(R.id.checkBoxNight)

                val medicineName = editTextMedication.text.toString()
                val morning = checkBoxMorning.isChecked
                val afternoon = checkBoxAfternoon.isChecked
                val night = checkBoxNight.isChecked

                val medicineData = HashMap<String, Any>()
                medicineData["MedicineName"] = medicineName
                medicineData["Morning"] = morning
                medicineData["Afternoon"] = afternoon
                medicineData["Night"] = night
                medicineData["bookingId"] =bookingId
                medicineData["doctorName"] =doctorName
                medicineData["patientId"] =patientId
                medicineData["bookingDate"] =bookingDate
                medicineData["bookingTime"] =bookingTime
                medicineData["PatientName"] =patientName

                database.child("Prescriptions").push()
                    .setValue(medicineData)
            }
            database.child("Booking").child(bookingId).child("dstatus").setValue("1")
                .addOnSuccessListener {
                    val intent = Intent(this@DoctorPrescription, DoctorViewPrescription::class.java)
                    startActivity(intent)
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to save prescription", Toast.LENGTH_SHORT).show()
                }
        }
    }




    private fun setupNavigationView() {
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {

                    startActivity(Intent(this, DoctorHomePageActivity::class.java))
                }
                R.id.nav_appointment -> {
                    startActivity(Intent(this, DoctorViewAppointment::class.java))
                }
                R.id.nav_prescription -> {
                    startActivity(Intent(this, DoctorPrescription::class.java))
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
                    val intent = Intent(this@DoctorPrescription, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }

            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }
}
