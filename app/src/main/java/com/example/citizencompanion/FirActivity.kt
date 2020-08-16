package com.example.citizencompanion

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

import java.util.*


class FirActivity : AppCompatActivity() {

    var  latitudefir = "null"
    var longitudefir = "null"
    var pincodefir = " null"

    var type = "null"
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    override fun onCreate(savedInstanceState: Bundle?) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fir)

        var type_fir: Spinner = findViewById(R.id.type_fir)
        var submitfir = findViewById<Button>(R.id.submitfir_btn)


        submitfir.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {



                var xfullname: TextInputEditText = findViewById(R.id.fullname_fir)
                var fullname = xfullname.text.toString()
                var xplace: TextInputEditText = findViewById(R.id.place_fir)
                var place = xplace.text.toString()
                var xdate: EditText = findViewById(R.id.date_fir)
                var date = xdate.text.toString()
                var xtime: EditText = findViewById(R.id.time_fir)
                var time = xtime.text.toString()
                var xdescription = findViewById<EditText>(R.id.description_fir)
                var description = xdescription.text.toString()
                var xwitness = findViewById<EditText>(R.id.witness_fir)
                var witness = xwitness.text.toString()



                //getting uid
                /*var bundle :Bundle ?=intent.extras
                var message = bundle!!.getString("uid")*/
                var uidfir: String = intent.getStringExtra("uid")
                lateinit var database: DatabaseReference
                database = Firebase.database.reference
                database.child("firs").child(uidfir).child("type_fir").setValue(type)
                database.child("firs").child(uidfir).child("fullname").setValue(fullname)
                database.child("firs").child(uidfir).child("place").setValue(place)
                database.child("firs").child(uidfir).child("date").setValue(date)
                database.child("firs").child(uidfir).child("time").setValue(time)
                database.child("firs").child(uidfir).child("description").setValue(description)
                database.child("firs").child(uidfir).child("witness").setValue(witness)
                database.child("firs").child(uidfir).child("latitudefir").setValue(latitudefir)
                database.child("firs").child(uidfir).child("longitudefir").setValue(longitudefir)
                database.child("firs").child(uidfir).child("pincodefir").setValue(pincodefir)
                Toast.makeText(baseContext, "Filed Successfully",
                    Toast.LENGTH_SHORT).show()

            }



        })

            // location starts here

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                // Got last known location. In some rare situations this can be null.

                if (location != null) {
                    latitudefir = location.latitude.toString()
                    longitudefir = location.longitude.toString()
                    getpincodefir(latitudefir,longitudefir)
                }


            }
//location ends here

        // type spinner logic starts here

        ArrayAdapter.createFromResource(
            this,
            R.array.fir_type,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            type_fir.adapter = adapter
            type_fir.prompt = "Type of incident"
        }

        class SpinnerActivity : MainActivity(), AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
                // An item was selected. You can retrieve the selected item using
                var positon=  parent.getItemAtPosition(pos)
                if (positon.equals(0)){
                    type = "murder"
                }else if(positon.equals(1)){
                     type = "rape"
                }else if(positon.equals(2)){
                     type = "theft"
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
               type = "null"
            }


            // type spinner logic starts here
        }

    }
    //reverse geocoding logic starts here
    private fun getpincodefir(latitudefir: String, longitudefir: String) {

        val addresses: List<Address>
        val lati = latitudefir.toDouble()
        val long = longitudefir.toDouble()
       val  geocoder = Geocoder(this, Locale.ENGLISH)

        addresses = geocoder.getFromLocation(lati,long,1)

        pincodefir = addresses[0].postalCode

    }

  //  reverse geocoding logic ends here

}