package com.example.citizencompanion

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.citizencompanion.dashboardPolice.PoliceDashboardHome
import com.example.citizencompanion.dashboardPolice.PoliceDashboardMenu
import com.example.citizencompanion.dashboardPolice.PoliceDashboardSettings
import kotlinx.android.synthetic.main.activity_dashboard_police.*

class DashboardPoliceActivity : AppCompatActivity() {

    private val policeDashboardHome = PoliceDashboardHome()
    private val policeDashboardMenu = PoliceDashboardMenu()
    private val policeDashboardSettings = PoliceDashboardSettings()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard_police)

        replaceFragment(policeDashboardHome)

        bottomPoliceNavigationView.setOnNavigationItemReselectedListener { menuItem->
            when(menuItem.itemId){
                R.id.dashboardPoliceHome -> replaceFragment(policeDashboardHome)
//                R.id.dashboardPoliceMenu -> replaceFragment(policeDashboardMenu)
                R.id.dashboardPoliceSettings -> replaceFragment(policeDashboardSettings)
            }
        }
    }

    private fun replaceFragment(fragment: Fragment){
        if(fragment != null){
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.policeDashboardFragment, fragment)
            transaction.commit()
        }
    }
}