package com.example.citizencompanion.objects

import com.google.firebase.firestore.GeoPoint
import java.util.*

class FIRObject(
    val uid: String,
    val FirType: String,
    val Place: String,
    val DateTime: Date,
    val Description: String,
    val Witness: String,
    val Location: GeoPoint
)