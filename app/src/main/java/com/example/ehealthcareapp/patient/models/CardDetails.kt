package com.example.ehealthcareapp.patient.models
data class CardDetails(
    val patientId: String,
    val cardNumber: String,
    val cardName: String,
    val expiryDate: String,
    val expiryYear: String,
    val cvv: String
)
