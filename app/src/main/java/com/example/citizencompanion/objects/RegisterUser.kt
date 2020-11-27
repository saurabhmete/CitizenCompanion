package com.example.citizencompanion.objects

import android.location.Location
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.GeoPoint

class RegisterUser(
    val emailId: String,
    val name: String,
    val phone: String,
    val gender: String,
    val aadhar: String,
    val dateOfBirth: String,
    val pincode: Int,
    val location: GeoPoint,
    val address: String,
    val FIRs: List<String>
)