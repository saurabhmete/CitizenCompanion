package com.example.citizencompanion

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.citizencompanion.Utils.CommonUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_dashboard_menu.*

class DashboardMenu : Fragment() {

    private val firebaseDatabase = Firebase.database.reference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fileFIR.setOnClickListener {
            requireActivity().run {
                startActivity(Intent(this, FirActivity::class.java))
            }
        }

        sosAlert.setOnClickListener {
            val uid = FirebaseAuth.getInstance().uid
            val pinCode = CommonUtils.firdata["pinCodeSOS"]
            val latitude = CommonUtils.firdata["latitudeSOS"]
            val longitude = CommonUtils.firdata["longitudeSOS"]
            if (uid != null && pinCode != null && latitude != null && longitude != null) {
                firebaseDatabase.child("SOS")
                    .child(pinCode)
                    .child(uid)
                    .child("latitude")
                    .setValue(latitude)

                firebaseDatabase.child("SOS")
                    .child(pinCode)
                    .child(uid)
                    .child("longitude")
                    .setValue(longitude)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(),"SOS Alert Send Successfully", Toast.LENGTH_SHORT).show()
                    }
            }
        }

    }
}