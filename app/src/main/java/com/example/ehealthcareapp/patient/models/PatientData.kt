package com.example.ehealthcareapp.patient.models

data class PatientData(
    val patientId: String?=null,

    var first: String?=null,
    var last: String?=null,
    val gender:String?=null,
    var phone: String?=null,
    var niNumber:String?=null,
    var email: String?=null,
    var password: String?=null,
    var type :String?=null


)
