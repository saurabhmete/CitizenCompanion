package com.example.citizencompanion

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.birjuvachhani.locus.Locus
import com.example.citizencompanion.Utils.CommonUtils
import com.example.citizencompanion.objects.RegisterUser
import com.example.citizencompanion.services.UserService
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_register.*
import org.jetbrains.anko.doAsync
import java.io.File

open class RegisterActivity : AppCompatActivity() {

    lateinit var gLocation: Location
    private var pinCode = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        loadingBar.visibility = View.VISIBLE

        //for getting the user location
        Locus.getCurrentLocation(this) { result ->
            result.location?.let {
                Log.d("DEBUG", "locus ${result.location!!.latitude}")
                this.gLocation = result.location!!
                getPincode()
                loadingBar.visibility = View.GONE
                startFragment()
            }
            result.error?.let {
                Log.d("ERROR", "locus ${result.error}")
            }
        }
    }

    private fun startFragment (){
        val fragmentBundle = Bundle()
        fragmentBundle.putString("latitude", gLocation.latitude.toString())
        fragmentBundle.putString("longitude", gLocation.longitude.toString())
        fragmentBundle.putString("pinCode", pinCode)

        val registerCitizenFragment = RegisterCitizenFragment()
        val registerPoliceFragment = RegisterPoliceFragment()
        registerCitizenFragment.arguments = fragmentBundle
        registerPoliceFragment.arguments = fragmentBundle

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.registerForm, registerCitizenFragment)
            commit()
        }

        citizenPoliceToggle.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                supportFragmentManager.beginTransaction().apply {
                    replace(R.id.registerForm, registerPoliceFragment)
                    commit()
                }
            } else {
                supportFragmentManager.beginTransaction().apply {
                    replace(R.id.registerForm, registerCitizenFragment)
                    commit()
                }
            }
        }
    }

    //upload aadhar starts here
    private fun uploadAadhar(aadharpath: String) {
        val storage = Firebase.storage
        val storageRef = storage.reference

        val file = Uri.fromFile(File(aadharpath))
        val riversRef = storageRef.child("aadhar/${file.lastPathSegment}")
        val uploadTask = riversRef.putFile(file)

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener {
            Toast.makeText(
                baseContext, "Aadhar Upload Failed",
                Toast.LENGTH_SHORT
            ).show()
            // Handle unsuccessful uploads
        }.addOnSuccessListener {
            Toast.makeText(
                baseContext, "Aadhar Success",
                Toast.LENGTH_LONG
            ).show()
        }
    }
    //file choosing ends here

    private fun getPincode() {
        //reverse geocoding logic starts here
        if (this::gLocation.isInitialized) {
            Log.d("DEBUG", "getPincode() | location is ${gLocation.latitude} ${gLocation.longitude}")
            val addresses: List<Address> = CommonUtils.geoDecode(this, gLocation)
            pinCode = addresses[0].postalCode
            Log.d("DEBUG", "getPincode() | pincode is ${pinCode}")
        }
        // reverse geocoding ends here
    }
}