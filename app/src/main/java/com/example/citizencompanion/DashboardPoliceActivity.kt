package com.example.citizencompanion

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavDeepLinkBuilder
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.birjuvachhani.locus.Locus
import com.example.citizencompanion.Utils.CommonUtils
import com.example.citizencompanion.Utils.LoadingClassCustomLoader
import com.example.citizencompanion.dashboardPolice.PoliceDashboardHome
import com.example.citizencompanion.dashboardPolice.PoliceDashboardMenu
import com.example.citizencompanion.dashboardPolice.PoliceDashboardSettings
import com.example.citizencompanion.viewfir.ViewFir
import com.google.android.gms.common.internal.service.Common
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_dashboard_police.*

class DashboardPoliceActivity : AppCompatActivity() {

    val realTimeDataBase = Firebase.database.reference

    lateinit var gLocation: Location
    var pinCode = ""

    private val CHANNEL_ID = "channel_id_example_01"
    private val notificationId = 101

    private var sendFIRNotification = false
    private var sendSOSNotification = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard_police)

        val loading = LoadingClassCustomLoader(this)
        loading.startLoading("Getting Your Location")

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomPoliceNavigationView)
        val navController = findNavController(R.id.policeFragment)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.dashboardPoliceHome,
                R.id.dashboardPoliceMenu,
                R.id.dashboardPoliceSettings
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        bottomNavigationView.setupWithNavController(navController)

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for(data in dataSnapshot.children){
                    CommonUtils.firdata["firID"] = data.key.toString()
                }
                if(sendFIRNotification) {
                    sendNotification()
                }
                sendFIRNotification = true
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException())
            }
        }

        val sosPostListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(sendSOSNotification){
                    sendSOSNotification()
                }
                sendSOSNotification = true
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException())
            }
        }
        //for getting the user location
        Locus.getCurrentLocation(this) { result ->
            result.location?.let {
                Log.d("DEBUG", "locus ${result.location!!.latitude}")
                this.gLocation = result.location!!
                getPincode(loading)
                CommonUtils.firdata["sosPincode"] = pinCode

                realTimeDataBase.child("FIRs")
                    .child(pinCode)
                    .addValueEventListener(postListener)

                realTimeDataBase.child("SOS")
                    .child(pinCode)
                    .addValueEventListener(sosPostListener)
            }
            result.error?.let {
                Log.d("ERROR", "locus ${result.error}")
            }
        }

        createNotificationChannel()
    }

    /**=====================================Pincode Starts=======================================*/
    private fun getPincode(loading: LoadingClassCustomLoader) {
        //reverse geocoding logic starts here
        if (this::gLocation.isInitialized) {
            Log.d("DEBUG", "location is ${gLocation.latitude} ${gLocation.longitude}")
            val addresses: List<Address> = CommonUtils.geoDecode(this, gLocation)
            pinCode = addresses[0].postalCode
            loading.isDismiss()
        }
    }
    /**=====================================Pincode Ends=======================================*/

    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = "Notification Title"
            val descriptionText = "Notification Description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification(){
        val intent = Intent(this, ViewFir::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_navigation_small)
            .setContentTitle("FIR Alert")
            .setContentText("A new FIR has been filed")
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(this)){
            notify(notificationId, builder.build())
        }
    }

    private fun sendSOSNotification(){
        val pendingIntent = NavDeepLinkBuilder(this)
            .setComponentName(DashboardPoliceActivity::class.java)
            .setGraph(R.navigation.dashboard_police_nav)
            .setDestination(R.id.dashboardPoliceMenu)
            .createPendingIntent()

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_navigation_small)
            .setContentTitle("SOS Alert")
            .setContentText("A new SOS alert has been raised")
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(this)){
            notify(notificationId, builder.build())
        }
    }
}