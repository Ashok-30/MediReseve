package com.example.ehealthcareapp.patient.models

data class Prescription(
    val MedicineName: String = "",
    val Morning: Boolean = false,
    val Afternoon: Boolean = false,
    val Night: Boolean = false,
    val bookingId: String = "",
    val doctorName: String = "",
    val patientId: String = "",
    val bookingDate: String = "",
    val bookingTime: String = "",
    val PatientName: String = ""
)
