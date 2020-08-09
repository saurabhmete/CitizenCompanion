package com.example.citizencompanion

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject

open class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //to populate types to the dropdown
        var spinner:Spinner = findViewById(R.id.type)
        val xusername = findViewById<EditText>(R.id.username)
        val username = xusername.text.toString()
        val xpassword = findViewById<EditText>(R.id.password)
        val password = xpassword.text.toString()
        val login = findViewById<Button>(R.id.login)



        login.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                val logindetails =  JSONObject()
                logindetails.put("type",type)
                logindetails.put("username",username)
                logindetails.put("password",password)


                loginws(logindetails)




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
                    var type = "citizen"
                }else if(positon.equals(1)){
                    var type = "police"
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                var type = null
            }
        }




    }

    private fun loginws(logindetails: JSONObject) {


        val url = "ip/login"

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, logindetails,
            Response.Listener { response ->
                textView.text = "Response Login: %s".format(response.toString())
               // ws success
                if(type.equals("citizen")){
                    FirActivity()
                }else{
                    getFirpolicewise()
                }


            },
            Response.ErrorListener { error ->
                Toast.makeText(applicationContext, "Some error occured", Toast.LENGTH_LONG).show()

            }
        )


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

                PoliceMainActivity()


            },
            Response.ErrorListener { error ->
                Toast.makeText(applicationContext, "Some error occured", Toast.LENGTH_LONG).show()

            }
        )
    }


}