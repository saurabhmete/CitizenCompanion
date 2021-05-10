package com.example.citizencompanion.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.citizencompanion.*
import com.example.citizencompanion.Utils.CommonUtils
import com.example.citizencompanion.objects.FIRListItem
import com.example.citizencompanion.viewfir.ViewFir
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fir_view_dialog_box.view.*
import kotlinx.android.synthetic.main.fragment_dashboard_home.*

class   DashboardHome : Fragment(), FIRListAdapter.OnItemClickListener {

    val firebaseDatabase = Firebase.database.reference
    val firestore = Firebase.firestore

    private val firListItem = ArrayList<FIRListItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getFIRListForUser()
    }

    private fun getRegisteredFIRs(_pinCode: Any?) {
        firebaseDatabase.child("FIRs")
            .child(_pinCode.toString())
            .addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value != null) {
                        val registeredFIRMap = snapshot.value as Map<*, *>
                        registeredFIRNumber.text = registeredFIRMap.size.toString()
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun getFIRListForUser() {
        val currentUser = Firebase.auth.currentUser
        if(currentUser != null){
            firestore.collection("citizen")
                .document(currentUser.uid)
                .get()
                .addOnSuccessListener { task ->
                    getRegisteredFIRs(task.data?.get("pincode"))
                    if (task.data?.get("FIRs") != null) {
                        val firs = task.data?.get("FIRs") as ArrayList<*>
                        for (fir in firs) {
                            Log.d("DEBUG", "fir loop $fir")
                            val item = FIRListItem(fir as String)
                            firListItem.plusAssign(item)
                        }

                        FIRListView.adapter = FIRListAdapter(firListItem, this)

                        //This is responsible for positioning the items in the recycler view
                        FIRListView.layoutManager = LinearLayoutManager(this.context)
                        FIRListView.setHasFixedSize(true)
                    }
                }
        }
    }

    override fun onItemClick(position: Int) {
        val firList = firListItem[position]
        firestore.collection("FIR")
            .document(firList.firId)
            .get()
            .addOnSuccessListener { task ->
                if(task.data != null){
                    gotoActivity(firList.firId)
                }
            }
    }

    private fun gotoActivity(firId: String) {
        val intent = Intent(activity, ViewFir::class.java)
        CommonUtils.firdata["firID"]= firId
        startActivity(intent)
    }
}