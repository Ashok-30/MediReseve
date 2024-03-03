package com.example.ehealthcareapp.patient.models

data class Rating(
    val bookingId: String? = null,
    val patientId: String? = null,
    val doctorId: String? = null,
    val doctorName: String? = null,
    val patientName: String? = null,
    val bookingDate: String? = null,
    val patientAge: String? = null,
    val bookingTime: String? = null,
    val ratingDescription: String? = null,
    val rating: Float = 0.0f // Default value for rating
) {
    constructor() : this("", "", "", "", "", "", "", "", "", 0.0f)
}
