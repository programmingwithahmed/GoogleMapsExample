package com.programmingwithahmed.googlemapsexample


import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.util.MapUtils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*


class MapsActivity5 : AppCompatActivity(), OnMapReadyCallback{

    private lateinit var mMap: GoogleMap

    private lateinit var fusedLocationClient: FusedLocationProviderClient


    private var grayPolyline: Polyline? = null
    private var blackPolyline: Polyline? = null


    private var movingCabMarker: Marker? = null
    private var previousLatLng: LatLng? = null
    private var currentLatLng: LatLng? = null

    lateinit var btnShowPath: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps_5)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        btnShowPath = findViewById(R.id.btn_show_path)
        btnShowPath.setOnClickListener {

            showPath(MapUtil.getListOfLocations())
            showMovingCab(MapUtil.getListOfLocations())
        }

    }


    override fun onMapReady(googleMap: GoogleMap) {

        mMap = googleMap

        showDefaultLocationOnMap(LatLng(28.435350000000003, 77.11368))

        mMap.uiSettings.isZoomControlsEnabled = true

    }


    private fun showDefaultLocationOnMap(latLng: LatLng) {
        moveCamera(latLng)
        animateCamera(latLng)
    }

    private fun moveCamera(latLng: LatLng) {
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
    }

    private fun animateCamera(latLng: LatLng) {
        val cameraPosition = CameraPosition.Builder().target(latLng).zoom(15.5f).build()
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }


    private fun showPath(latLngList: ArrayList<LatLng>) {

        val builder = LatLngBounds.Builder()
        for (latLng in latLngList) {
            builder.include(latLng)
        }

        // this is used to set the bound of the Map
        val bounds = builder.build()
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 2))

        val polylineOptions = PolylineOptions()
        polylineOptions.color(Color.GRAY)
        polylineOptions.width(5f)
        polylineOptions.addAll(latLngList)
        grayPolyline = mMap.addPolyline(polylineOptions)

        val blackPolylineOptions = PolylineOptions()
        blackPolylineOptions.color(Color.BLACK)
        blackPolylineOptions.width(5f)
        blackPolyline = mMap.addPolyline(blackPolylineOptions)



        val originMarker = addOriginDestinationMarkerAndGet(latLngList[0])
        originMarker.setAnchor(0.5f, 0.5f)
        val destinationMarker = addOriginDestinationMarkerAndGet(latLngList[latLngList.size - 1])
        destinationMarker.setAnchor(0.5f, 0.5f)




        val polylineAnimator = AnimationUtil.polylineAnimator()
        polylineAnimator.addUpdateListener { valueAnimator ->
            val percentValue = (valueAnimator.animatedValue as Int)
            val index = (grayPolyline?.points!!.size) * (percentValue / 100.0f).toInt()
            blackPolyline?.points = grayPolyline?.points!!.subList(0, index)
        }
        polylineAnimator.start()
    }


    private fun addOriginDestinationMarkerAndGet(latLng: LatLng): Marker {

        val bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(MapUtil.getOriginDestinationMarkerBitmap())
        return mMap.addMarker(MarkerOptions().position(latLng).flat(true).icon(bitmapDescriptor))
    }

    private fun addCarMarkerAndGet(latLng: LatLng): Marker {

        val bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(MapUtil.getCarBitmap(this))
        return mMap.addMarker(MarkerOptions().position(latLng).flat(true).icon(bitmapDescriptor))
    }

    private fun updateCarLocation(latLng: LatLng) {
        if (movingCabMarker == null) {
            movingCabMarker = addCarMarkerAndGet(latLng)
        }
        if (previousLatLng == null) {
            currentLatLng = latLng
            previousLatLng = currentLatLng
            movingCabMarker?.position = currentLatLng
            movingCabMarker?.setAnchor(0.5f, 0.5f)
            animateCamera(currentLatLng!!)
        } else {
            previousLatLng = currentLatLng
            currentLatLng = latLng
            val valueAnimator = AnimationUtil.carAnimator()
            valueAnimator.addUpdateListener { va ->

                if (currentLatLng != null && previousLatLng != null) {
                    val multiplier = va.animatedFraction
                    val nextLocation = LatLng(
                        multiplier * currentLatLng!!.latitude + (1 - multiplier) * previousLatLng!!.latitude,
                        multiplier * currentLatLng!!.longitude + (1 - multiplier) * previousLatLng!!.longitude
                    )
                    movingCabMarker?.position = nextLocation
                    val rotation = MapUtil.getRotation(previousLatLng!!, nextLocation)
                    if (!rotation.isNaN()) {
                        movingCabMarker?.rotation = rotation
                    }
                    movingCabMarker?.setAnchor(0.5f, 0.5f)
                    animateCamera(nextLocation)
                }
            }
            valueAnimator.start()
        }
    }


    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    private fun showMovingCab(cabLatLngList: ArrayList<LatLng>) {
        handler = Handler()
        var index = 0
        runnable = Runnable {
            run {
                if (index < 10) {
                    updateCarLocation(cabLatLngList[index])
                    handler.postDelayed(runnable, 3000)
                    ++index
                } else {
                    handler.removeCallbacks(runnable)
                    Toast.makeText(this@MapsActivity5, "Trip Ends", Toast.LENGTH_LONG).show()
                }
            }
        }
        handler.postDelayed(runnable, 5000)
    }

}