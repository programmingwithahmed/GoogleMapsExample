package com.programmingwithahmed.googlemapsexample

import android.content.ClipData.Item
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


class MapsActivity3 : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    private var users = arrayListOf<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps_3)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true

        setUpMap()
    }


    private fun setUpMap() {

        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL


        val json = Utility.readTextFile(this, "ImagesWithLocations.txt")
        val gson = Gson()

        val listType: Type = object : TypeToken<List<User>>() {}.type
        users = gson.fromJson(json, listType)

        for (i in 0..(users.size - 1)) {

            val point = LatLng(users[i].address.geo.lat, users[i].address.geo.lng)
            placeMarkerOnMap(users[i], point)

            if (i == users.size - 1)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(point, 12f))
        }
    }


    private fun placeMarkerOnMap(user: User, location: LatLng) {

        val markerOptions = MarkerOptions().position(location)

        markerOptions.icon(
            BitmapDescriptorFactory.fromBitmap(
                Utility.createScaledBitmap(
                    this,
                    R.drawable.ic_avtar_placeholder,
                    50,
                    50
                )
            )
        )

        markerOptions.title(user.name)
        val pinnedMarker: Marker = mMap.addMarker(markerOptions)
        loadMarkerIcon(pinnedMarker, user.image)
    }

    private fun loadMarkerIcon(marker: Marker, imageUrl: String) {


        Glide.with(this)
            .asBitmap()
            .load(imageUrl.replace("https://", "http://"))

            .apply(RequestOptions.bitmapTransform(CircleCrop()))
            .transform(GlideCircleWithBorder(5, Color.parseColor("#000000")))

            .into(object : CustomTarget<Bitmap?>() {

                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap?>?
                ) {

                    if (resource != null) {

                        val resizedBitmap = Bitmap.createScaledBitmap(resource, 80, 80, false)
                        val icon = BitmapDescriptorFactory.fromBitmap(resizedBitmap)
                        marker.setIcon(icon)

                    }

                }

                override fun onLoadCleared(placeholder: Drawable?) {

                }

            })

    }


}