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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
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
import java.io.File

open class RegisterActivity : AppCompatActivity() {
    //PERMISSION_ID is an integer value. It can be of any value
    private val PERMISSION_ID = 42

    lateinit var location: Location
    private var pincode = "null"
    private var guid = "null"
    private var aadharpath = "null"
    private lateinit var userType: String

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest

    private val fireStore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        //for getting the user location
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        getLastLocation()

        val registerCitizenFragment = RegisterCitizenFragment()
        val registerPoliceFragment = RegisterPoliceFragment()

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

    private fun createUser(newUser: RegisterUser) {
        getPincode()
        // [START create_user_with_email]
        auth.createUserWithEmailAndPassword(newUser.emailId, newUser.password)
            .addOnCompleteListener(this@RegisterActivity) { task ->
                val any = if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "createUserWithEmail:success")

                    val uid = auth.currentUser?.uid
                    newUser.uid = uid!!
                    val response = UserService.registerUser(newUser, location)

                    val intent = Intent(this@RegisterActivity, FirActivity::class.java)
                    intent.putExtra("uid", uid)
                    CommonUtils.firdata["uid"] = uid
                    guid = uid

                    // start your next activity
                    startActivity(intent)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("TAG", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext, "Registration failed. Please check details again.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        // [END create_user_with_email]
    }

    /**=====================================Location====================================================*/
    private fun getPincode() {
        //reverse geocoding logic starts here
        if (this::location.isInitialized) {
            Log.d("DEBUG", "location is ${location.latitude} ${location.longitude}")
            var addresses: List<Address> = CommonUtils.geoDecode(this, location)
            pincode = addresses[0].postalCode
        }
        // reverse geocoding ends here
    }

    //Check if all permissions are available
    private fun checkPermission(): Boolean {
        if (
            ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.ACCESS_MEDIA_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    //Get Permissions from user
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_MEDIA_LOCATION
            ),
            PERMISSION_ID
        )
    }

    //Check if location is enabled
    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    //built in function to check permissions
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_ID) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Debug", "Permissions Present")
            }
        }
    }

    private fun getLastLocation() {
        //check if permission is present and location is enabled
        if (checkPermission() && isLocationEnabled()) {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { response ->
                val _location: Location? = response
                if (_location == null) {
                    //for a null last location new location is to be get
                    getNewLocation()
                } else {
                    this.location = _location
                }
            }
        } else {
            requestPermissions()
            getNewLocation()
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            val lastLocation = p0.lastLocation
            location = lastLocation
        }
    }

    private fun getNewLocation() {
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 2
        if (checkPermission()) {
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest, locationCallback, Looper.myLooper()
            )
        }
    }
    /**=====================================Location====================================================*/
}