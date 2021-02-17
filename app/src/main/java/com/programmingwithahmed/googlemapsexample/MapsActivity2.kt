package com.programmingwithahmed.googlemapsexample

import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.gson.Gson
import java.net.HttpURLConnection
import java.net.URL


class MapsActivity2 : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var lastLocation: Location

    var markerPoints = arrayListOf<LatLng>()

    private lateinit var routeResponse: RouteResponse

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps_2)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true

        mMap.setOnMapClickListener { latLng ->

            if (markerPoints.size > 1) {
                markerPoints.clear()
                mMap.clear()
            }

            // Adding new item to the ArrayList
            markerPoints.add(latLng)

            // Creating MarkerOptions
            val options = MarkerOptions()

            // Setting the position of the marker
            options.position(latLng)
            if (markerPoints.size == 1) {
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            } else if (markerPoints.size == 2) {
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            }

            // Add new marker to the Google Map Android API V2
            mMap.addMarker(options)

            // Checks, whether start and end locations are captured
            if (markerPoints.size >= 2) {
                val origin = markerPoints[0]
                val dest = markerPoints[1]

                // Getting URL to the Google Directions API
                val url: String = getDirectionsUrl(origin, dest)

                val downloadTask = DownloadTask()
                downloadTask.execute(url)
            }

        }

        setUpMap()
    }


    private fun setUpMap() {

        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }


        mMap.isMyLocationEnabled = true
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL


        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->

            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
            }

        }

    }


    private fun drawRoute(){

        if (routeResponse.code.trim() == "Ok"){

           val steps = routeResponse.routes[0].legs[0].steps

            val points = arrayListOf<LatLng>()
            for (i in 0 .. (steps.size - 1)){

                val point = LatLng(steps[i].maneuver.location[0], steps[i].maneuver.location[1])
                points.add(point)
            }

            val polylineOptions = PolylineOptions().apply {

                addAll(points)
                width(12f)
                color(Color.RED)
                geodesic(true)

            }
            mMap.addPolyline(polylineOptions)


        }else {
            Toast.makeText(this, "Error in getting route", Toast.LENGTH_SHORT).show()
        }


    }


    private fun getDirectionsUrl(origin: LatLng, dest: LatLng): String {

        return "http://router.project-osrm.org" +

                "/route" + // service

                "/v1" +  // version

                "/driving" +  // profile

                "/${origin.latitude},${origin.longitude};${dest.latitude},${dest.longitude}" + // coordinates

                "?" +

                "overview=false" +
                "&" +
                "steps=true"
    }

    inner class DownloadTask : AsyncTask<String, String, String>() {


        override fun doInBackground(vararg url: String): String {

            var data = ""
            try {
                data = downloadUrl(url[0])
            } catch (e: Exception) {
                Log.d("Background Task", e.toString())
            }
            return data
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)

            val gson = Gson();
            routeResponse = gson.fromJson(result, RouteResponse::class.java)

            drawRoute()
        }


        private fun downloadUrl(strUrl: String): String {

            var data = ""

            try {

                val url = URL(strUrl)

                val urlConnection = url.openConnection() as HttpURLConnection
                urlConnection.connect()

                //read API response
                val result = urlConnection.inputStream.bufferedReader().readText()

                data = result
                urlConnection.disconnect()

            } catch (e: java.lang.Exception) {
                Log.d("Exception", e.toString())
            }

            return data
        }
    }


}