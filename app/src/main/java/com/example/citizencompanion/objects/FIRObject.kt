package com.example.citizencompanion.objects

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint

class FIRObject(
    val uid: String?,
    val name: String,
    val gender: String,
    val date: Timestamp,
    val address: String,
    val phone: String,
    val aadhar: String,
    val incidenttype: String,
    val incidentplacetype: String,
    val incidentplacename: String,
    val incidentdate: String,
    val seensuspect: String,
    val suspectname: String,
    val suspectage: String,
    val suspectaddress: String,
    val suspectidentify: String,
    val location: GeoPoint
)