package com.example.citizencompanion

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.citizencompanion.Utils.CommonUtils
import com.example.citizencompanion.objects.HospitalContactListItem
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_emergency_contacts.*


class EmergencyContacts : AppCompatActivity(), HospitalListAdapter.OnItemClickListener {

    private val hospitalArrayList = ArrayList<HospitalContactListItem>()
    private val firebaseDatabase = Firebase.database.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emergency_contacts)

        getHospitalContactList()
    }

    private fun getHospitalContactList(){
        val pincode = CommonUtils.firdata["pinCodeSOS"]
        if (pincode != null) {
            firebaseDatabase.child("Hospitals")
                .child(pincode)
                .get().addOnSuccessListener { dataSnapshot ->
                        val locationMap: HashMap<String, String> =
                            dataSnapshot.value as HashMap<String, String>

                    locationMap.forEach { (key, value) ->
                        val item = HospitalContactListItem(
                            key,value
                        )
                        hospitalArrayList.plusAssign(item)
                    }

                    hospitalList.adapter = HospitalListAdapter(hospitalArrayList, this)
                    //This is responsible for positioning the items in the recycler view
                    hospitalList.layoutManager = LinearLayoutManager(this)
                    hospitalList.setHasFixedSize(true)
                }
        }
    }

    override fun onItemClick(position: Int) {
        val hospitalDetails = hospitalArrayList[position]
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:${hospitalDetails.hostpitalContactNumber}")
        startActivity(intent)
    }
}