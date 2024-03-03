package com.example.ehealthcareapp.patient.models

data class Booking(
    val bookingId:String="",
    val availabilityId: String = "",
    val doctorId: String = "",
    var doctorName: String = "",
    val patientAge: String = "",
    val patientDescription: String = "",
    var patientName: String = "",
    val patientId: String = "",
    val selectedDate:String="",
    val selectedDay:String="",
    val slot:String="",
    val pstatus: String ="",
    val dstatus: String ="",
    val type: String = ""
)

