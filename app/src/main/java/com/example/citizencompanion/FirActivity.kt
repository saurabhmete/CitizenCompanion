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
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.util.*


class FirActivity : AppCompatActivity() {

    final var  latitudefir = "null"
    final var longitudefir = "null"
    final var pincodefir = " null"
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    override fun onCreate(savedInstanceState: Bundle?) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fir)

        val type_fir: Spinner = findViewById(R.id.type_fir)
        val fullname: TextInputEditText = findViewById(R.id.fullname_fir)
        val place: TextInputEditText = findViewById(R.id.place_fir)
        val date: EditText = findViewById(R.id.date_fir)
        val time: EditText = findViewById(R.id.time_fir)
        val description = findViewById<EditText>(R.id.description_fir)
        val witness = findViewById<EditText>(R.id.witness_fir)
        val submitfir = findViewById<Button>(R.id.submitfir_btn)


        submitfir.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {

                val submitfir = JSONObject()
                submitfir.put("type_fir", type_fir)
                submitfir.put("fullname", fullname)
                submitfir.put("place", place)
                submitfir.put("date", date)
                submitfir.put("time", time)
                submitfir.put("description", description)
                submitfir.put("witness", witness)
                submitfir.put("latitudefir", latitudefir)
                submitfir.put("longitudefir", longitudefir)
                submitfir.put("pincodefir", pincodefir)



                submitfirws(submitfir)


            }

            private fun submitfirws(submitfir: JSONObject) {

                val url = "ip/submitfir"

                val jsonObjectRequest = JsonObjectRequest(
                    Request.Method.POST, url, submitfir,
                    Response.Listener { response ->
                        textView.text = "Response submitfir: %s".format(response.toString())
                        // ws success


                    },
                    Response.ErrorListener { error ->
                        Toast.makeText(applicationContext, "Some error occured", Toast.LENGTH_LONG)
                            .show()

                    }
                )

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
                    var type = "murder"
                }else if(positon.equals(1)){
                    var type = "rape"
                }else if(positon.equals(2)){
                    var type = "theft"
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                var type = null
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