package com.programmingwithahmed.googlemapsexample


import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var mMap: GoogleMap

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var lastLocation: Location


    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.setOnMarkerClickListener(this)

        setUpMap()
    }

    override fun onMarkerClick(marker: Marker?): Boolean = false



    private fun setUpMap() {

        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }


        mMap.isMyLocationEnabled = true
        mMap.mapType = GoogleMap.MAP_TYPE_HYBRID


        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->

            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                placeMarkerOnMap(currentLatLng)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
            }

        }

    }


    private fun placeMarkerOnMap(location: LatLng) {

        val markerOptions = MarkerOptions().position(location)

        markerOptions.icon(
            BitmapDescriptorFactory.fromBitmap(
                Utility.createScaledBitmap(
                    this,
                    R.drawable.home,
                    50,
                    50
                )
            )
        )


        val titleStr = getAddress(location)
        markerOptions.title(titleStr)

        mMap.addMarker(markerOptions)
    }


    private fun getAddress(latLng: LatLng): String {

        val geocoder = Geocoder(this)

        try {

            val addresses: List<Address>? = geocoder.getFromLocation(
                latLng.latitude,
                latLng.longitude,
                1
            )

            if (null != addresses && addresses.isNotEmpty()) {

                val address: Address = addresses[0]
                return address.getAddressLine(0)
            }

        } catch (e: IOException) {
            Log.e("MapsActivity", e.localizedMessage!!)
        }

        return ""
    }


    private fun startDropMarkerAnimation(marker: Marker) {

        val target = marker.position
        val handler = Handl
        er()
        val start = SystemClock.uptimeMillis()
        val proj: Projection = getMap().getProjection()
        val targetPoint: Point = proj.toScreenLocation(target)
        val duration = (200 + targetPoint.y * 0.6) as Long
        val startPoint: Point = proj.toScreenLocation(marker.position)
        startPoint.y = 0
        val startLatLng = proj.fromScreenLocation(startPoint)
        val interpolator: Interpolator = LinearOutSlowInInterpolator()
        handler.post(object : Runnable {
            override fun run() {
                val elapsed = SystemClock.uptimeMillis() - start
                val t: Float = interpolator.getInterpolation(elapsed.toFloat() / duration)
                val lng = t * target.longitude + (1 - t) * startLatLng.longitude
                val lat = t * target.latitude + (1 - t) * startLatLng.latitude
                marker.setPosition(LatLng(lat, lng))
                if (t < 1.0) {
                    // Post again 16ms later == 60 frames per second
                    handler.postDelayed(this, 16)
                }
            }
        })

    }

}