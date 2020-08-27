package com.example.citizencompanion.Utils

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import java.util.*

object CommonUtils {

    fun geoDecode(context: Context, location: Location): List<Address> {
        val geocode = Geocoder(context, Locale.ENGLISH)
        return geocode.getFromLocation(
            location.latitude.toDouble(),
            location.longitude.toDouble(),
            1
        )
    }
}