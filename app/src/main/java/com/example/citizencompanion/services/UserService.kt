package com.example.citizencompanion.services

import android.content.ContentValues.TAG
import android.location.Location
import android.util.Log
import com.example.citizencompanion.objects.RegisterUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint

object UserService {
    private val fireStore: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun registerUser(newUser: RegisterUser, location: Location) {
        val docData = hashMapOf(
            "email" to newUser.emailId,
            "Name" to newUser.name,
            "Phone" to newUser.phone,
            "Gender" to newUser.gender,
            "Location" to GeoPoint(location.latitude, location.longitude),
            "FIR" to arrayListOf<String>()
        )
//        fireStore.collection("citizen").document(newUser.uid).set(docData)
//            .addOnSuccessListener {
//                Log.d(TAG, "DocumentSnapshot successfully written!")
//            }
//            .addOnFailureListener { e ->
//                Log.d(TAG, "DocumentSnapshot failed!", e)
//            }
    }
}
