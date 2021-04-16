package com.example.citizencompanion

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.birjuvachhani.locus.Locus
import com.example.citizencompanion.Utils.CommonUtils
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_register.*


class FirActivity : AppCompatActivity() {

    //PERMISSION_ID is an integer value. It can be of any value
    private val PERMISSION_ID = 42

    lateinit var gLocation: Location
    var pinCode = ""


    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fir)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        loadingBar.visibility = View.VISIBLE

        //for getting the user location
        Locus.getCurrentLocation(this) { result ->
            result.location?.let {
                Log.d("DEBUG", "locus ${result.location!!.latitude}")
                this.gLocation = result.location!!
                getPincode()
                loadingBar.visibility = View.GONE
            }
            result.error?.let {
                Log.d("ERROR", "locus ${result.error}")
            }
        }

        /* val firType: Spinner = findViewById(R.id.type_fir)
        val submitFir = findViewById<Button>(R.id.submitfir_btn)*/

        /*submitFir.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                getPincode()

                *//*val xfullname: TextInputEditText = findViewById(R.id.fullname_fir)
                val fullname = xfullname.text.toString()
                val xplace: TextInputEditText = findViewById(R.id.place_fir)
                val place = xplace.text.toString()
                val xdate: EditText = findViewById(R.id.date_fir)
                val date = xdate.text.toString()
                val xtime: EditText = findViewById(R.id.time_fir)
                val time = xtime.text.toString()
                val xdescription = findViewById<EditText>(R.id.description_fir)
                val description = xdescription.text.toString()
                val xwitness = findViewById<EditText>(R.id.witness_fir)
                val witness = xwitness.text.toString()
                val timestamp = Timestamp.now()*//*


                //getting uid

                val uid: String = intent.getStringExtra("uid")!!
                *//*var firOb = FIRObject(
                    uid,
                    firTypeString,
                    place,
                    timestamp.toDate(),
                    description,
                    witness,
                    GeoPoint(location.latitude, location.longitude)
                )*//*
             *//*   FirService.registerFir(firOb)*//*
                Toast.makeText(baseContext, "Filed Successfully", Toast.LENGTH_SHORT).show()
            }
        })*/

        // type spinner logic starts here
        /*ArrayAdapter.createFromResource(
            this,
            R.array.fir_type,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            firType.adapter = adapter
            firType.prompt = "Type of incident"
        }*/

        /*firType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                firTypeString = adapterView?.getItemAtPosition(position).toString().toLowerCase()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }*/
    }

    private fun fragmentBegin() {
        val fragment = FirFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.commit()
    }

    /**=====================================Location====================================================*/
    private fun getPincode() {
        //reverse geocoding logic starts here
        if (this::gLocation.isInitialized) {
            Log.d("DEBUG", "location is ${gLocation.latitude} ${gLocation.longitude}")
            val addresses: List<Address> = CommonUtils.geoDecode(this, gLocation)
            pinCode = addresses[0].postalCode
        }
        // reverse geocoding ends here

        CommonUtils.firdata["latitude"] = gLocation.latitude.toString()
        CommonUtils.firdata["longitude"] = gLocation.latitude.toString()
        CommonUtils.firdata["pinCode"] = pinCode

        //Fragment code
        fragmentBegin()
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
                Manifest.permission.ACCESS_COARSE_LOCATION
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
                    this.gLocation = _location
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
            gLocation = lastLocation
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
    /**=================================Location Ends=================================================*/
}
