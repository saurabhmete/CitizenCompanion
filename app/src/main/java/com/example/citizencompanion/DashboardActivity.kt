package com.example.citizencompanion

import android.location.Address
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.birjuvachhani.locus.Locus
import com.example.citizencompanion.Utils.CommonUtils
import com.example.citizencompanion.Utils.LoadingClassCustomLoader
import com.google.android.material.bottomnavigation.BottomNavigationView

class DashboardActivity : AppCompatActivity() {

    lateinit var gLocation: Location
    var pinCode = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val loading = LoadingClassCustomLoader(this)
        loading.startLoading("Getting Your Location")

        Locus.getCurrentLocation(this) { locationResult ->
            locationResult.location?.let {
                Log.d("DEBUG", "locus ${locationResult.location!!.latitude}")
                this.gLocation = locationResult.location!!
                getPincode()
                CommonUtils.firdata["pinCodeSOS"] = pinCode
                CommonUtils.firdata["latitudeSOS"] = gLocation.latitude.toString()
                CommonUtils.firdata["longitudeSOS"] = gLocation.longitude.toString()
                loading.isDismiss()
            }
            locationResult.error?.let {
                Log.e("ERROR", "locus ${locationResult.error}")
            }
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val navController = findNavController(R.id.fragment)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.dashboardHome,
                R.id.dashboardMenu,
                R.id.dashboardSettings
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        bottomNavigationView.setupWithNavController(navController)
    }

    /**=====================================Pincode Starts=======================================*/
    private fun getPincode() {
        //reverse geocoding logic starts here
        if (this::gLocation.isInitialized) {
            Log.d("DEBUG", "location is ${gLocation.latitude} ${gLocation.longitude}")
            val addresses: List<Address> = CommonUtils.geoDecode(this, gLocation)
            pinCode = addresses[0].postalCode
        }
        // reverse geocoding ends here
    }
    /**=====================================Pincode Ends=======================================*/
}