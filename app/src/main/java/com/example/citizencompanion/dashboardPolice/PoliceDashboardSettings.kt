package com.example.citizencompanion.dashboardPolice

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.citizencompanion.MainActivity
import com.example.citizencompanion.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_dashboard_settings.*
import kotlinx.android.synthetic.main.fragment_police_dashboard_settings.*

/**
 * A simple [Fragment] subclass.
 * Use the [PoliceDashboardSettings.newInstance] factory method to
 * create an instance of this fragment.
 */
class PoliceDashboardSettings : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_police_dashboard_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        signOutFromAppPolice.setOnClickListener {
            Firebase.auth.signOut()
            // start your next activity
            requireActivity().run {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }
}