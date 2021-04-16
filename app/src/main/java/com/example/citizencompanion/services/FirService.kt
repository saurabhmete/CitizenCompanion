package com.example.citizencompanion.services

import android.content.ContentValues
import android.util.Log
import com.example.citizencompanion.objects.FIRObject
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

object FirService {
    private val fireStore: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun registerFir(firOb: FIRObject) {

        fireStore.collection("FIR")
            .add(firOb)
            .addOnSuccessListener { doc ->
                Log.d("DEBUG", "Successful Insertion with doc -> $doc")
            }
            .addOnFailureListener { e ->
                Log.d("ERROR", "Error Occured when filing FIR", e)
            }
    }

    fun addFirToUser(uid: String, firId: String) {
        fireStore.collection("citizen").document(uid).update(
            "FIR", FieldValue.arrayUnion(firId)
        ).addOnSuccessListener {
            Log.d(ContentValues.TAG, "DocumentSnapshot successfully written!")
        }.addOnFailureListener { e ->
            Log.d(ContentValues.TAG, "DocumentSnapshot failed!", e)
        }
    }
}