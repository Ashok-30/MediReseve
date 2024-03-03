package com.example.ehealthcareapp.patient

import android.content.Intent
import android.os.Bundle

import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import com.example.ehealthcareapp.R

import com.example.ehealthcareapp.patient.models.Rating

import com.google.firebase.database.FirebaseDatabase

class PatientRatingActivity : AppCompatActivity() {
    private lateinit var ratingDescription: EditText
    private var rating: Float = 0f
    private lateinit var rateButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.patient_rating_activity)
        val doctorName = intent.getStringExtra("doctorName")
        val patientName = intent.getStringExtra("patientName")

        ratingDescription = findViewById(R.id.doctorRating)

        // Find TextViews in your layout
        val data1 = findViewById<TextView>(R.id.dName)
        val data2 = findViewById<TextView>(R.id.pName)

        // Set the retrieved data to TextViews
        data1.text = "Doctor Name: $doctorName"
        data2.text = "Patient Name: $patientName"

        rateButton = findViewById(R.id.rateButton)
        rateButton.setOnClickListener {
            saveRatingData()
        }
    }

    private fun saveRatingData() {
        val bookingId = intent.getStringExtra("bookingId") ?: ""
        val patientId = intent.getStringExtra("patientId") ?: ""
        val doctorId = intent.getStringExtra("doctorId") ?: ""
        val doctorName = intent.getStringExtra("doctorName")
        val patientName = intent.getStringExtra("patientName")
        val bookingDate = intent.getStringExtra("bookingDate")
        val patientAge = intent.getStringExtra("patientAge")
        val bookingTime = intent.getStringExtra("bookingTime")
        val ratingDescriptionText = ratingDescription.text.toString()


        if (ratingDescriptionText.isEmpty()) {
            Toast.makeText(this, "Enter Your Review", Toast.LENGTH_SHORT).show()
            return
        }

        if (rating == 0f) {
            Toast.makeText(this, "Give your Stars", Toast.LENGTH_SHORT).show()
            return
        }

        val dbRef = FirebaseDatabase.getInstance().reference

        // Update 'status' field to "1" in the Booking table
        dbRef.child("Booking").child(bookingId).child("pstatus").setValue("1")
            .addOnSuccessListener {
                // Update successful, proceed to save rating data
                val ratingData = Rating(
                    bookingId,
                    patientId,
                    doctorId,
                    doctorName,
                    patientName,
                    bookingDate,
                    patientAge,
                    bookingTime,
                    ratingDescriptionText,
                    rating
                )

                // Push rating data to the Rating table
                val ratingId = dbRef.child("Rating").push().key
                ratingId?.let {
                    dbRef.child("Rating").child(it).setValue(ratingData)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Rating submitted!", Toast.LENGTH_SHORT).show()
                            val intent =
                                Intent(this@PatientRatingActivity, PatientHomeScreen::class.java)
                            startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed to submit rating!", Toast.LENGTH_SHORT)
                                .show()
                        }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to update status!", Toast.LENGTH_SHORT).show()
            }
    }






    fun onStarClicked(view: View) {
        val clickedStarId = view.id

        val star1 = findViewById<ImageView>(R.id.star1)
        val star2 = findViewById<ImageView>(R.id.star2)
        val star3 = findViewById<ImageView>(R.id.star3)
        val star4 = findViewById<ImageView>(R.id.star4)
        val star5 = findViewById<ImageView>(R.id.star5)

        star1.setImageResource(R.drawable.ic_star_empty)
        star2.setImageResource(R.drawable.ic_star_empty)
        star3.setImageResource(R.drawable.ic_star_empty)
        star4.setImageResource(R.drawable.ic_star_empty)
        star5.setImageResource(R.drawable.ic_star_empty)

        when (clickedStarId) {
            R.id.star1 -> {
                star1.setImageResource(R.drawable.ic_star_filled)
            }
            R.id.star2 -> {
                star1.setImageResource(R.drawable.ic_star_filled)
                star2.setImageResource(R.drawable.ic_star_filled)
            }
            R.id.star3 -> {
                star1.setImageResource(R.drawable.ic_star_filled)
                star2.setImageResource(R.drawable.ic_star_filled)
                star3.setImageResource(R.drawable.ic_star_filled)
            }
            R.id.star4 -> {
                star1.setImageResource(R.drawable.ic_star_filled)
                star2.setImageResource(R.drawable.ic_star_filled)
                star3.setImageResource(R.drawable.ic_star_filled)
                star4.setImageResource(R.drawable.ic_star_filled)
            }
            R.id.star5 -> {
                star1.setImageResource(R.drawable.ic_star_filled)
                star2.setImageResource(R.drawable.ic_star_filled)
                star3.setImageResource(R.drawable.ic_star_filled)
                star4.setImageResource(R.drawable.ic_star_filled)
                star5.setImageResource(R.drawable.ic_star_filled)
            }
        }

         rating = when (clickedStarId) {
             R.id.star1 -> 1f
             R.id.star2 -> 2f
             R.id.star3 -> 3f
             R.id.star4 -> 4f
             R.id.star5 -> 5f
             else -> 0f
        }

        // Perform actions based on the selected rating value
        // For example, display a Toast message with the selected rating
        Toast.makeText(this, "Selected Rating: $rating", Toast.LENGTH_SHORT).show()
    }
}
