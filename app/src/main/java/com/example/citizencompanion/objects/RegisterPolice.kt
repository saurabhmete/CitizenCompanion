package com.example.citizencompanion.objects

import com.google.firebase.firestore.GeoPoint

class RegisterPolice(
    var uid: String,
    val designation: String,
    val policeStation: String,
    val emailId: String,
    val name: String,
    val phoneNumber: String,
    val gender: String,
    val aadharNumber: String,
    val pincode: Int,
    val location: GeoPoint
)