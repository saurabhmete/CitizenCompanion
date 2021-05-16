package com.example.citizencompanion.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.citizencompanion.R
import com.example.citizencompanion.SOSListAdapter
import com.example.citizencompanion.Utils.CommonUtils
import com.example.citizencompanion.objects.HospitalContactListItem
import com.example.citizencompanion.objects.SOSListItem
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class Emergency_Contacts : Fragment(), SOSListAdapter.OnItemClickListener {

    private val sosAlertList = ArrayList<HospitalContactListItem>()
    private val firebaseDatabase = Firebase.database.reference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_emergency_contacts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getHospitalContactList()
    }

    private fun getHospitalContactList(){
        val pincode = CommonUtils.firdata["sosPincode"]
        if (pincode != null) {
            firebaseDatabase.child("Hospital")
                .child(pincode)
                .get().addOnSuccessListener { dataSnapshot ->
                    for (data in dataSnapshot.children) {
                        val locationMap: HashMap<String, String> =
                            data.value as HashMap<String, String>

                    }
                }
        }
    }

    override fun onItemClick(position: Int) {
    }

}