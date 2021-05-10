package com.example.citizencompanion

import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.birjuvachhani.locus.Locus
import com.example.citizencompanion.Utils.LoadingClassCustomLoader
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.custom_loading_dialog.*
import java.util.*

open class MainActivity : AppCompatActivity() {
    lateinit var userTypeString: String
    lateinit var uid: String

    private val fireStore: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val loading = LoadingClassCustomLoader(this)
        loading.startLoading("Getting Your Location")

        Locus.getCurrentLocation(this) {
            setContentView(R.layout.activity_main)

            loading.isDismiss()

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
                loading.startLoading("Logging In")
                val email = username.text.toString()
                val password = password.text.toString()

                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this@MainActivity) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithEmail:success")
                            val user = auth.currentUser
                            uid = user?.uid.toString()
                            redirectIntent(uid, userTypeString, loading)
                        } else {
                            // If sign in fails, display a message to the user.
                            loading.isDismiss()
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
                loading.startLoading("Retrieving login details")
                fireStore.collection("citizen")
                    .document(currentUser.uid)
                    .get()
                    .addOnSuccessListener { documentSnapshot ->
                        if (documentSnapshot.data != null){
                            loading.isDismiss()
                            loading.startLoading("Found Citizen login")
                            Handler().postDelayed(Runnable {
                                loading.isDismiss()
                                // start your next activity
                                startActivity(Intent(this@MainActivity, DashboardActivity::class.java))
                                finish()
                            }, 1500)
                        } else {
                            loading.isDismiss()
                            loading.startLoading("Found Police login")
                            Handler().postDelayed(Runnable {
                            loading.isDismiss()
                            // start your next activity
                            startActivity(Intent(this@MainActivity, DashboardPoliceActivity::class.java))
                            finish()
                            }, 1500)
                        }
                    }
            }
        }
    }

    private fun redirectIntent(uid: String, userType: String, loading: LoadingClassCustomLoader) {
        lateinit var intent: Intent
        fireStore.collection(userType).document(uid).get()
            .addOnCompleteListener { task ->
                loading.isDismiss()
                if (task.isSuccessful && task.result.data != null) {
                    if (userType == "citizen") {
                        loading.startLoading("Citizen Login")
                        intent = Intent(this@MainActivity, DashboardActivity::class.java)
                        intent.putExtra("uid", uid)
                    } else if (userType == "police") {
                        loading.startLoading("Police Login")
                        intent = Intent(this@MainActivity, DashboardPoliceActivity::class.java)
                        intent.putExtra("uid", uid)
                    }
                } else {
                    Toast.makeText(this, "Wrong Credentials. Please Check Again", Toast.LENGTH_SHORT).show()
                    intent = Intent(this@MainActivity, MainActivity::class.java)
                    Firebase.auth.signOut()
                }

                Handler().postDelayed(Runnable {
                    loading.isDismiss()
                    // start your next activity
                    startActivity(intent)
                    finish()
                }, 1500)
            }
    }
}