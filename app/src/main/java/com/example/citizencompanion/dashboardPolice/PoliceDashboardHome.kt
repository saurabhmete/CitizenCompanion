package com.example.citizencompanion.dashboardPolice

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.citizencompanion.FIRListAdapter
import com.example.citizencompanion.R
import com.example.citizencompanion.objects.FIRListItem
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fir_view_dialog_box.*
import kotlinx.android.synthetic.main.fir_view_dialog_box.view.*
import kotlinx.android.synthetic.main.fragment_police_dashboard_home.*

/**
 * A simple [Fragment] subclass.
 * Use the [PoliceDashboardHome.newInstance] factory method to
 * create an instance of this fragment.
 */
class PoliceDashboardHome : Fragment(), FIRListAdapter.OnItemClickListener {

    val firebaseDatabase = Firebase.database.reference
    val firestore = Firebase.firestore

    private val firListItem = ArrayList<FIRListItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_police_dashboard_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getFIRListForPolice()
    }

    private fun getRegisteredFIRs(_pinCode: Any?) {
        firebaseDatabase.child("policeStation")
            .child(_pinCode.toString())
            .child("FIRs")
            .addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value != null) {
                        val registeredFIRMap = snapshot.value as Map<*, *>
                        registeredFIRNumberPolice.text = registeredFIRMap.size.toString()
                        populateFIRList(registeredFIRMap)
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun getFIRListForPolice() {
        val currentUser = Firebase.auth.currentUser
        Log.i("getFIRListForPolice", "currentUser $currentUser")
        if(currentUser != null){
            firestore.collection("police")
                .document(currentUser.uid)
                .get()
                .addOnSuccessListener { task ->
                    getRegisteredFIRs(task.data?.get("pincode"))
                }
        }
    }

    private fun populateFIRList(registeredFIRMap: Map<*, *>){
        for((key, value) in registeredFIRMap){
            val item = FIRListItem(key as String)
            firListItem.plusAssign(item)
        }
        FIRListViewPolice.adapter = FIRListAdapter(firListItem, this)

        //This is responsible for positioning the items in the recycler view
        FIRListViewPolice.layoutManager = LinearLayoutManager(this.context)
        FIRListViewPolice.setHasFixedSize(true)
    }

    override fun onItemClick(position: Int) {
        val firList = firListItem[position]
        firestore.collection("FIR")
            .document(firList.firId)
            .get()
            .addOnSuccessListener { task ->
                if(task.data != null){
                    val firDialogView = LayoutInflater.from(activity).inflate(R.layout.fir_view_dialog_box, null)

                    firDialogView.incidentTypeValue.text = task.data?.get("incidenttype").toString()
                    firDialogView.incidentPlaceValue.text = task.data?.get("incidentplacename").toString()
                    firDialogView.incidentDateValue.text = task.data?.get("incidentdate").toString()
                    firDialogView.firDateValue.text = task.data?.get("date").toString()
                    firDialogView.firNameValue.text = task.data?.get("name").toString()
                    firDialogView.phoneNumberValue.text = task.data?.get("phone").toString()

                    val firDialogBuilder = AlertDialog.Builder(activity)
                        .setView(firDialogView)
                        .setTitle("View FIR")
                    //Show Dialog
                    val firAlertDialog = firDialogBuilder.show()
                    firDialogView.closeFIRDialogButton.setOnClickListener{
                        firAlertDialog.dismiss()
                    }
                }
            }
    }
}