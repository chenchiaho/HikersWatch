package com.example.android.travelerswatch

import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.lang.Exception
import java.util.*

class MainActivity : AppCompatActivity() {

    var locationManager: LocationManager? = null
    var locationListener: LocationListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                updateLocationInfo(location)
            }

            @Suppress("DEPRECATION")
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                super.onStatusChanged(provider, status, extras)
            }

        }
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                2
            )
        } else {
            locationManager!!.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                0,
                0f,
                locationListener!!
            )
            val lastLocation = locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (lastLocation != null) {
                updateLocationInfo(lastLocation)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED) {
                locationManager!!.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    0,
                    0f,
                    locationListener!!
                )
            }
        }
    }

    fun updateLocationInfo(location: Location) {

        var lat = findViewById<TextView>(R.id.lat)
        var lon = findViewById<TextView>(R.id.lon)
        var alt = findViewById<TextView>(R.id.alt)
        var acc = findViewById<TextView>(R.id.accu)
        var address = findViewById<TextView>(R.id.address)


        lat.text = "Latitude:    ${String.format("%.4f", location.latitude)}"
        lon.text = "Longitude: ${String.format("%.4f", location.longitude)}"
        alt.text = "Altitude:     ${String.format("%.4f", location.altitude)}"
        acc.text = "Accuracy:   ${String.format("%.2f", location.accuracy)}"

        var addressString = "Could not find address"
        val geocoder = Geocoder(this, Locale.getDefault())

        try {
            val list = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            if (list != null && list.isNotEmpty()) {
                addressString = "Address:\n"
                if (list[0].thoroughfare != null)
                {
                    addressString += list[0].thoroughfare + "\n"
                }
                if (list[0].locality != null)
                {
                    addressString += list[0].locality + " "
                }
                if (list[0].postalCode != null)
                {
                    addressString += list[0].postalCode + " "
                }
                if (list[0].adminArea != null) {
                    addressString += list[0].adminArea
                }
            }
        } catch (e: Exception) { e.printStackTrace()}
        address.text = addressString
    }
}