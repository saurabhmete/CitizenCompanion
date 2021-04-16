package com.example.citizencompanion

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.citizencompanion.objects.RegisterPolice
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_register_citizen.*
import kotlinx.android.synthetic.main.fragment_register_police.*
import kotlinx.android.synthetic.main.fragment_register_police.aadharNumber
import kotlinx.android.synthetic.main.fragment_register_police.chooseAadhar
import kotlinx.android.synthetic.main.fragment_register_police.confirmPassword
import kotlinx.android.synthetic.main.fragment_register_police.emailId
import kotlinx.android.synthetic.main.fragment_register_police.gender
import kotlinx.android.synthetic.main.fragment_register_police.name
import kotlinx.android.synthetic.main.fragment_register_police.password
import kotlinx.android.synthetic.main.fragment_register_police.phoneNumber
import kotlinx.android.synthetic.main.fragment_register_police.register
import org.jetbrains.anko.backgroundColor

// this has the same effect as when onCreateView
class   RegisterPoliceFragment : Fragment(R.layout.fragment_register_police) {
    private val firebaseDatabase: DatabaseReference = Firebase.database.reference
    private val fireStore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = Firebase.auth

    var policeStationArray = mutableListOf<String>()
    lateinit var policeStationDropDown: Spinner
    lateinit var filePath: Uri

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val latitude = arguments?.getString("latitude").toString().toDouble()
        val longitude = arguments?.getString("longitude").toString().toDouble()
        val pinCode = arguments?.getString("pinCode").toString().toInt()

        chooseAadhar.setOnClickListener {
            startAadharChooser()
        }

        var policeStation = ""
        // populate the dropdown
        policeStationDropDown = view.findViewById(R.id.policeNameDropDown) as Spinner

        var designation = ""
        val policeOrganization = resources.getStringArray(R.array.policeOrganization)
        val policeOrganizationAdapter = ArrayAdapter(
            requireActivity().applicationContext,
            android.R.layout.simple_spinner_item,
            policeOrganization
        )
        policeOrganizationSpinner.adapter = policeOrganizationAdapter
        policeOrganizationSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    designation = adapterView?.getItemAtPosition(position).toString()
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }

        if (pinCode != null) {
            firebaseDatabase
                .child("policeStation")
                .child(pinCode.toString())
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val policeStationMap = snapshot.value as Map<*, *>
                        for ((k, v) in policeStationMap) {
                            policeStationArray.add(k as String)
                        }

                        policeStationDropDown.adapter = ArrayAdapter<String>(
                            requireActivity().applicationContext,
                            android.R.layout.simple_list_item_1,
                            policeStationArray.toTypedArray()
                        )
                        policeStationDropDown.onItemSelectedListener =
                            object : AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(
                                    adapterView: AdapterView<*>?,
                                    view: View?,
                                    position: Int,
                                    id: Long
                                ) {
                                    Log.d(
                                        "DEBUG",
                                        "adapter ${
                                            adapterView?.getItemAtPosition(position).toString()
                                        }"
                                    )
                                    policeStation =
                                        adapterView?.getItemAtPosition(position).toString()
                                }

                                override fun onNothingSelected(p0: AdapterView<*>?) {}
                            }
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
        }

        var selectedGender = ""
        gender.setOnCheckedChangeListener { group, checkedId ->
            val radioGender: RadioButton = view.findViewById(checkedId)
            selectedGender = radioGender.text.toString()
        }

        register.setOnClickListener {
            val emailId = emailId.text.toString()
            val password = password.text.toString()
            val confirmPassword = confirmPassword.text.toString()
            val name = name.text.toString()
            val phoneNumber = phoneNumber.text.toString()
            val aadharNumber = aadharNumber.text.toString()

            if (password == confirmPassword) {
                auth.createUserWithEmailAndPassword(emailId, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val uid = auth.currentUser?.uid

                            FirebaseStorage.getInstance().reference
                                .child("aadhar/police/$uid.pdf")
                                .putFile(filePath)

                            val policeObject = RegisterPolice(
                                uid!!,
                                designation,
                                policeStation,
                                emailId,
                                name,
                                phoneNumber,
                                selectedGender,
                                aadharNumber,
                                pinCode,
                                GeoPoint(latitude, longitude)
                            )
                            fireStore.collection("police").document(uid).set(policeObject)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {

                                        firebaseDatabase.child("policeStation").child(pinCode.toString()).child(policeStation).child(designation).child(uid).setValue(true)
                                            .addOnCompleteListener {
                                                if(task.isSuccessful){
                                                    Toast.makeText(
                                                        context,
                                                        "User has been created",
                                                        Toast.LENGTH_SHORT
                                                    ).show()

                                                    // start your next activity
                                                    requireActivity().run {
                                                        startActivity(
                                                            Intent(
                                                                this,
                                                                DashboardPoliceActivity::class.java
                                                            )
                                                        )
                                                        finish()
                                                    }
                                                }
                                            }
                                    }
                                }
                        }
                    }
            }
        }
    }

    private fun startAadharChooser(){
        val choosingIntent = Intent()
        choosingIntent.type = "*/*"
        choosingIntent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(choosingIntent,"Choose Aadhar"), 111)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 111 && resultCode == Activity.RESULT_OK && data != null) {
            filePath = data.data!!
            Log.d("DEBUG", "filepath ${filePath.lastPathSegment}")

            if (this::filePath.isInitialized){
                chooseAadhar.text = "Aadhar Selected"
                chooseAadhar.backgroundColor = Color.parseColor("#34CB50")
            }
        }
    }
}