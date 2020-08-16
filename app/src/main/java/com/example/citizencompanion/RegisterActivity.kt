package com.example.citizencompanion

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.util.*

open class RegisterActivity : AppCompatActivity() {

     var  latitude = "null"
     var longitude = "null"
     var pincode = "null"
    var type="null"
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        lateinit var auth: FirebaseAuth
        auth = Firebase.auth
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        //to populate types to the dropdown
        var spinner:Spinner = findViewById(R.id.typeregister)
        //dropdown end
        var register = findViewById<Button>(R.id.submitbutton)



        register.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {

                var xemailid = findViewById<EditText>(R.id.emailid)
                var xpassword = findViewById<EditText>(R.id.password)
                var emailid = xemailid.text.toString().trim()
                var password = xpassword.text.toString()
                authcreate(emailid,password)
            }

            private fun authcreate(emailid: String, password: String) {

                // [START create_user_with_email]
                auth.createUserWithEmailAndPassword(emailid, password)
                    .addOnCompleteListener(this@RegisterActivity) { task ->
                        val any = if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "createUserWithEmail:success")
                            val user = auth.currentUser


                            var radiogender = findViewById<RadioGroup>(R.id.radiogender)
                            var selectedgender = radiogender.checkedRadioButtonId
                            var xradiogender = findViewById<RadioButton>(selectedgender)
                            var gender = xradiogender.text.toString()
                            var xname = findViewById<EditText>(R.id.name)
                            var xphone = findViewById<EditText>(R.id.phone)
                            var name = xname.text.toString()
                            var phone = xphone.text.toString()


                            lateinit var database: DatabaseReference
                            database = Firebase.database.reference
                            val uid = user?.uid.toString()

                            database.child("citizens").child(uid).child("Name").setValue(name)
                            database.child("citizens").child(uid).child("phone").setValue(phone)
                            database.child("citizens").child(uid).child("gender").setValue(gender)
                            database.child("citizens").child(uid).child("latitude").setValue(latitude)
                            database.child("citizens").child(uid).child("longitude").setValue(longitude)
                            database.child("citizens").child(uid).child("pincode").setValue(pincode)
                            database.child("citizens").child(uid).child("type").setValue(type)
                            val intent = Intent(this@RegisterActivity, FirActivity::class.java)
                             intent.putExtra("uid", uid)
                            // start your next activity
                            startActivity(intent)


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "createUserWithEmail:failure", task.exception)
                            Toast.makeText(
                                baseContext, "Authentication failed. Please check details again.",
                                Toast.LENGTH_SHORT
                            ).show()

                        }


                    }
                // [END create_user_with_email]



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


        //reverse geocoding logic starts here


        val addresses: List<Address>
        val lati = latitude.toDouble()
        val long = longitude.toDouble()
        val  geocoder = Geocoder(this, Locale.ENGLISH)

        addresses = geocoder.getFromLocation(lati,long,1)

         pincode = addresses[0].postalCode
        // reverse geocoding ends here

        // type spinner logic starts here

        ArrayAdapter.createFromResource(
            this@RegisterActivity,
            R.array.typeregister,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }

        class SpinnerActivity : RegisterActivity(), AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
                // An item was selected. You can retrieve the selected item using
                var positon=  parent.getItemAtPosition(pos)
                if (positon.equals(0)){
                   type = "police"
                }else if(positon.equals(1)){
                   type = "citizen"
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                type = "null"
            }
        }

       // spinner logic ends here

    }




}