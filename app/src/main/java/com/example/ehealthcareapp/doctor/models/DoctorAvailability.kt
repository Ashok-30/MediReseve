package com.example.ehealthcareapp.doctor.models

data class DoctorAvailability(
    val availabilityId: String = "",
    val doctorId: String = "",
    val startDate:String="",
    val endDate:String="",
    val monday: MutableList<Int> = MutableList(12) { 0 },
    val tuesday: MutableList<Int> = MutableList(12) { 0 },
    val wednesday: MutableList<Int> = MutableList(12) { 0 },
    val thursday: MutableList<Int> = MutableList(12) { 0 },
    val friday: MutableList<Int> = MutableList(12) { 0 },
    val saturday: MutableList<Int> = MutableList(12) { 0 },
    val sunday: MutableList<Int> = MutableList(12  ) { 0 },
)
