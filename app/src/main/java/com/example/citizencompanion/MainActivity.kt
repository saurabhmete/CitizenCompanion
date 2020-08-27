package com.example.citizencompanion

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject


open class MainActivity : AppCompatActivity() {
    var type = "null"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Firebase Auth
        val auth: FirebaseAuth = Firebase.auth

        //to populate types to the dropdown
        val spinner: Spinner = findViewById(R.id.type)
        //dropdown end

        val login = findViewById<Button>(R.id.login)
        val register = findViewById<Button>(R.id.register)


        login.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                val xusername = findViewById<EditText>(R.id.username)
                val email = xusername.text.toString()
                val xpassword = findViewById<EditText>(R.id.password)
                val password = xpassword.text.toString()

                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this@MainActivity) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithEmail:success")
                            val user = auth.currentUser
                            val intent = Intent(this@MainActivity, FirActivity::class.java)
                            val uid = user?.uid.toString()
                            intent.putExtra("uid", uid)

                            // start your next activity
                            startActivity(intent)


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithEmail:failure", task.exception)
                            Toast.makeText(
                                baseContext, "Authentication failed.",
                                Toast.LENGTH_SHORT
                            ).show()

                            // ...
                        }

                        // ...
                    }


            }

        })


        register.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                val intent = Intent(this@MainActivity, RegisterActivity::class.java)
               /* intent.putExtra("key", value)*/
                // start your next activity
                startActivity(intent)




            }

        })


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
                    type = "citizen"
                }else if(positon.equals(1)){
                   type = "police"
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
               type = "null"
            }
        }




    }



    private fun getFirpolicewise() {
        val policewise =  JSONObject()
        policewise.put("type",type)
        policewise.put("username",username)

        val url = "ip/policewisefir"

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, policewise,
            Response.Listener { response ->
                textView.text = "Response policewisefir: %s".format(response.toString())
                // ws success

//                PoliceMainActivity()


            },
            Response.ErrorListener { error ->
                Toast.makeText(applicationContext, "Some error occured", Toast.LENGTH_LONG).show()

            }
        )
    }
}