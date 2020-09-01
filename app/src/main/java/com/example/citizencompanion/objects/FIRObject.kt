package com.example.citizencompanion.objects

import com.google.firebase.firestore.GeoPoint
import java.util.*

class FIRObject(
    val uid: String,
    val firname: String,
    val firgender: String,
    val firdate: String,
    val firaddress: String,
    val firphone: String,
    val firaadhar: String,
    val incidenttype: String,
    val incidentplacetype: String,
    val incidentplacename:String,
    val incidentdate:String,
    val seensuspect:String,
    val suspectname:String,
    val suspectage:String,
    val suspectaddress:String,
    val suspectidentify:String,
    val Location: String
)