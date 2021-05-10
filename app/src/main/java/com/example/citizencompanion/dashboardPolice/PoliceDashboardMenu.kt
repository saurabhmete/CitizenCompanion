package com.example.citizencompanion.dashboardPolice

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.citizencompanion.R
/**
 * A simple [Fragment] subclass.
 * Use the [PoliceDashboardMenu.newInstance] factory method to
 * create an instance of this fragment.
 */
class PoliceDashboardMenu : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_police_dashboard_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }
}