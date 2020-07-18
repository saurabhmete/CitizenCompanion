package com.example.citizencompanion

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject

class RegisterActivity : AppCompatActivity() {

    final var  latitude = "null"
    final var longitude = "null"

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val spinner: Spinner = findViewById(R.id.typeregister)
        val xname = findViewById<EditText>(R.id.name)
        val xphone = findViewById<EditText>(R.id.phone)

        val radiogender = findViewById<RadioGroup>(R.id.radiogender)
        val selectedgender = radiogender.checkedRadioButtonId
        val xradiogender = findViewById<RadioButton>(selectedgender)

        val register = findViewById<Button>(R.id.register)

        val gender = xradiogender.text.toString()
        val name = xname.text.toString()
        val phone = xphone.text.toString()


        register.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {

                val registerdetails =  JSONObject()
                registerdetails.put("type",type)
                registerdetails.put("name",name)
                registerdetails.put("phone",phone)
                registerdetails.put("gender",gender)
                registerdetails.put("latitude",latitude)
                registerdetails.put("longitude",longitude)



                registerws(registerdetails)




            }

            private fun registerws(registerdetails: JSONObject) {
                val url = "ip/register"

                val jsonObjectRequest = JsonObjectRequest(
                    Request.Method.POST, url, registerdetails,
                    Response.Listener { response ->
                        textView.text = "Response Register: %s".format(response.toString())
                        // ws success


                    },
                    Response.ErrorListener { error ->
                        Toast.makeText(applicationContext, "Some error occured", Toast.LENGTH_LONG).show()

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
                        latitude = location.latitude.toString()
                        longitude = location.longitude.toString()
                    }


                }
//location ends here

        // type spinner logic starts here

        ArrayAdapter.createFromResource(
            this,
            R.array.type,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }

        class SpinnerActivity : MainActivity(), AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
                // An item was selected. You can retrieve the selected item using
                var positon=  parent.getItemAtPosition(pos)
                if (positon.equals(0)){
                    var type = "police"
                }else if(positon.equals(1)){
                    var type = "citizen"
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                var type = null
            }
        }

        // type spinner logic starts here

    }




}