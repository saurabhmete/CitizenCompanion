package com.example.citizencompanion.dashboardPolice

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.citizencompanion.R
import com.example.citizencompanion.SOSListAdapter
import com.example.citizencompanion.Utils.CommonUtils
import com.example.citizencompanion.objects.SOSListItem
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_police_dashboard_menu.*

class PoliceDashboardMenu : Fragment(), SOSListAdapter.OnItemClickListener {

    private val sosAlertList = ArrayList<SOSListItem>()
    private val firebaseDatabase = Firebase.database.reference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_police_dashboard_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getSOSList()
    }

    private fun getSOSList() {
        val pincode = CommonUtils.firdata["sosPincode"]
        if (pincode != null) {
            firebaseDatabase.child("SOS")
                .child(pincode)
                .get().addOnSuccessListener { dataSnapshot ->
                    for (data in dataSnapshot.children) {
                        val uid = data.key.toString()
                        val locationMap: HashMap<String, String> =
                            data.value as HashMap<String, String>
                        val item = SOSListItem(
                            uid,
                            locationMap["longitude"].toString(),
                            locationMap["latitude"].toString()
                        )
                        sosAlertList.plusAssign(item)
                    }
                    SOSListViewPolice.adapter = SOSListAdapter(sosAlertList, this)

                    //This is responsible for positioning the items in the recycler view
                    SOSListViewPolice.layoutManager = LinearLayoutManager(this.context)
                    SOSListViewPolice.setHasFixedSize(true)
                }
        }
    }

    override fun onItemClick(position: Int) {
        val sosList = sosAlertList[position]

        openNavigation(sosList.latitude, sosList.longitude)
    }

    private fun openNavigation(latitude: String, longitude: String) {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("google.navigation:q=$latitude,$longitude&mode=d")
        )
        intent.setPackage("com.google.android.apps.maps")
        if (intent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(intent)
        }

    }
}