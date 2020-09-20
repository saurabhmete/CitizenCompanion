package com.example.citizencompanion

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

// this has the same effect as when onCreateView
class RegisterPoliceFragment : Fragment(R.layout.fragment_register_police) {
    val firebaseDatabase: DatabaseReference = Firebase.database.reference

    var policeStationArray = arrayOf("")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        firebaseDatabase.child("some").child("some1").setValue("awesome")
        firebaseDatabase
            .child("policeStation")
            .child("400709")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("INFO", "dataSnapshot ${snapshot.value}")
                }

                override fun onCancelled(error: DatabaseError) {}
            })

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val policeStationDropDown = view.findViewById(R.id.policeNameDropDown) as Spinner

    }
}