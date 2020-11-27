package com.example.citizencompanion

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.birjuvachhani.locus.Locus
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

open class MainActivity : AppCompatActivity() {
    lateinit var userTypeString: String
    lateinit var uid: String

    private val fireStore: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Locus.getCurrentLocation(this) {
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
                val email = username.text.toString()
                val password = password.text.toString()

                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this@MainActivity) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithEmail:success")
                            val user = auth.currentUser
                            uid = user?.uid.toString()
                            redirectIntent(uid, userTypeString)
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithEmail:failure", task.exception)
                            Toast.makeText(
                                baseContext, "Authentication failed.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }

            register.setOnClickListener {
                // start your next activity
                startActivity(Intent(this@MainActivity, RegisterActivity::class.java))
            }

            if (currentUser != null) {
                fireStore.collection("citizen")
                    .document(currentUser.uid)
                    .get()
                    .addOnSuccessListener { documentSnapshot ->
                        if (documentSnapshot == null){
                            // start your next activity
                            startActivity(Intent(this@MainActivity, DashboardPoliceActivity::class.java))
                            finish()
                        } else {
                            // start your next activity
                            startActivity(Intent(this@MainActivity, DashboardActivity::class.java))
                            finish()
                        }
                    }
            }
            startActivity(Intent(this@MainActivity, DashboardActivity::class.java))
            finish()
        }
    }

    private fun redirectIntent(uid: String, userType: String) {
        lateinit var intent: Intent
        fireStore.collection(userType).document(uid).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful && task.result != null) {
                    if (userType == "citizen") {
                        intent = Intent(this@MainActivity, DashboardActivity::class.java)
                        intent.putExtra("uid", uid)
                    } else if (userType == "police") {
                        intent = Intent(this@MainActivity, DashboardPoliceActivity::class.java)
                        intent.putExtra("uid", uid)
                    }
                } else {
                    Toast.makeText(this, "Wrong Credentials", Toast.LENGTH_SHORT)
                    intent = Intent(this@MainActivity, MainActivity::class.java)
                }
                // start your next activity
                startActivity(intent)
                finish()
            }
    }
}