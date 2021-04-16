package com.example.citizencompanion

import android.app.Activity
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.citizencompanion.objects.RegisterUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_register_citizen.*
import org.jetbrains.anko.backgroundColor
import java.util.*

class RegisterCitizenFragment : Fragment(), DatePickerDialog.OnDateSetListener {

    private val fireStore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = Firebase.auth

    lateinit var filePath: Uri
    lateinit var location: Location
    var pincode: Int = 0

    var day = 0
    var month = 0
    var year = 0

    // this is called after onCreate()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register_citizen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var selectedGender = ""
        gender.setOnCheckedChangeListener { group, checkedId ->
            val radioGender: RadioButton = view.findViewById(checkedId)
            selectedGender = radioGender.text.toString()
        }

        lateinit var citizenObject: RegisterUser

        location = Location("GPS")
        location.latitude = arguments?.get("latitude").toString().toDouble()
        location.longitude = arguments?.get("longitude").toString().toDouble()
        pincode = arguments?.get("pinCode").toString().toInt()

        chooseAadhar.setOnClickListener {
            startAadharChooser()
        }

        datePick()

        register.setOnClickListener {
            val emailId = emailId.text.toString()
            val password = password.text.toString()
            val confirmPassword = confirmPassword.text.toString()
            val name = name.text.toString()
            val phoneNumber = phoneNumber.text.toString()
            val aadharNumber = aadharNumber.text.toString()
            val address = "${addressLine1.text} ${addressLine2.text}"
            val dateOfBirth = dateOfBirth.text.toString()

            if (password == confirmPassword) {
                auth.createUserWithEmailAndPassword(emailId, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.i("INFO", "User is created")

                            val currentUser = auth.currentUser
                            if (currentUser != null) {
                                val uid = currentUser.uid
                                FirebaseStorage.getInstance().reference
                                    .child("aadhar/citizen/$uid.pdf")
                                    .putFile(filePath)

                                citizenObject = RegisterUser(
                                    emailId,
                                    name,
                                    phoneNumber,
                                    selectedGender,
                                    aadharNumber,
                                    dateOfBirth,
                                    pincode,
                                    GeoPoint(
                                        arguments?.get("latitude").toString().toDouble(),
                                        arguments?.get("longitude").toString().toDouble()
                                    ),
                                    address,
                                    listOf()
                                )

                                fireStore.collection("citizen").document(uid).set(citizenObject)
                                    .addOnSuccessListener {
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
                                                        DashboardActivity::class.java
                                                    )
                                                )
                                                finish()
                                        }
                                    }
                                    .addOnFailureListener {exception ->
                                        Log.e("ERROR", "Exception occured when inserting in firestore ${exception.localizedMessage}")
                                    }
                            }
                        }
                    }
                    .addOnFailureListener {exception ->
                        Log.e("ERROR", "Error when creating user ${exception.localizedMessage}")
                    }
            }
        }
    }

    private fun getDateCalendar(){
        val cal = Calendar.getInstance()
        day = cal.get(Calendar.DAY_OF_MONTH)
        month = cal.get(Calendar.MONTH)
        year = cal.get(Calendar.YEAR)
    }
    private fun datePick(){
        dateOfBirth.isFocusable = false
        dateOfBirth.setOnClickListener {
            getDateCalendar()
            val con = this.context
            if(con != null) {
                DatePickerDialog(con, this, year, month, day).show()
            }
        }
    }
    override fun onDateSet(p0: DatePicker?, _year: Int, _month: Int, _dayOfMonth: Int) {
        val dob = getString(R.string.date_of_birth, _year, _month, _dayOfMonth)
        dateOfBirth.setText(dob)
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