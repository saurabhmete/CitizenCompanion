package com.example.citizencompanion

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.util.*

open class MainActivity : AppCompatActivity() {
    lateinit var userTypeString: String
    lateinit var uid: String

    private val fireStore: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Firebase Auth
        val auth: FirebaseAuth = Firebase.auth
        // Get current User
        val currentUser = Firebase.auth.currentUser

        //to populate types to the dropdown
        val userType: Spinner = findViewById(R.id.type)

        ArrayAdapter.createFromResource(
            this,
            R.array.type,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            userType.adapter = adapter
        }
        userType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                userTypeString = adapterView?.getItemAtPosition(position).toString()
                    .toLowerCase(Locale.getDefault())
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        val login = findViewById<Button>(R.id.login)
        val register = findViewById<Button>(R.id.register)

        login.setOnClickListener {
            val xusername = findViewById<EditText>(R.id.username)
            val email = xusername.text.toString()
            val xpassword = findViewById<EditText>(R.id.password)
            val password = xpassword.text.toString()

//            auth.signInWithEmailAndPassword(email, password)
//                .addOnCompleteListener(this@MainActivity) { task ->
//                    if (task.isSuccessful) {
//                        // Sign in success, update UI with the signed-in user's information
//                        Log.d("TAG", "signInWithEmail:success")
//                        val user = auth.currentUser
//                        uid = user?.uid.toString()
//                        redirectIntent(uid, userTypeString)
//                    } else {
//                        // If sign in fails, display a message to the user.
//                        Log.w("TAG", "signInWithEmail:failure", task.exception)
//                        Toast.makeText(
//                            baseContext, "Authentication failed.",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                }
            val intent = Intent(this@MainActivity, DashboardActivity::class.java)
            // start your next activity
            startActivity(intent)
            finish()
        }

        register.setOnClickListener {
            val intent = Intent(this@MainActivity, RegisterActivity::class.java)
            // start your next activity
            startActivity(intent)
            finish()
        }

        if (currentUser != null) {
            val intent = Intent(this@MainActivity, DashboardActivity::class.java)
            // start your next activity
            startActivity(intent)
            finish()
        }
    }

    private fun redirectIntent(uid: String, userType: String) {
        lateinit var intent: Intent
        fireStore.collection(userType).document(uid).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful && task.result != null) {
                    if (userType == "citizen") {
                        intent = Intent(this@MainActivity, FirActivity::class.java)
                        intent.putExtra("uid", uid)
                    } else if (userType == "police") {
                        intent = Intent(this@MainActivity, FirActivity::class.java)
                        intent.putExtra("uid", uid)
                    }
                } else {
                    intent = Intent(this@MainActivity, MainActivity::class.java)
                }
                intent.putExtra("uid", uid)
                // start your next activity
                startActivity(intent)
                finish()
            }
    }
}