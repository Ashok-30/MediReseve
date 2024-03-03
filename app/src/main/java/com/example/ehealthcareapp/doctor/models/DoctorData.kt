package com.example.ehealthcareapp.doctor.models

data class DoctorData(
    val doctorId: String?=null,

    var first: String?=null,
    var last: String?=null,
    val specialization:String?=null,
    var phone: String?=null,
    var doctornum:String?=null,
    var email: String?=null,
    var password: String?=null,
)