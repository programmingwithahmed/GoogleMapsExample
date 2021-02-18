package com.programmingwithahmed.googlemapsexample


import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import de.hdodenhof.circleimageview.CircleImageView
import java.lang.reflect.Type


class MapsActivity4 : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    private var users = arrayListOf<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps_4)

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
            placeMarkerOnMap(i, users[i], point)

            if (i == users.size - 1)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(point, 12f))
        }

        mMap.setInfoWindowAdapter(object : InfoWindowAdapter {

            override fun getInfoWindow(marker: Marker): View? {

                val rootView: View = layoutInflater.inflate(R.layout.popup_user_marker_info, null)

                val currentUser = users[marker.snippet.toInt()]

                val ivAvatar = rootView.findViewById(R.id.iv_avatar) as CircleImageView
                Glide.with(this@MapsActivity4)
                    .load(currentUser.image.replace("https://", "http://"))
                    .apply(
                        RequestOptions.circleCropTransform()
                            .placeholder(R.drawable.ic_avtar_placeholder)
                    )
                    .listener(object : RequestListener<Drawable> {

                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            return true
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {

                            ivAvatar.setImageDrawable(resource)
                            if (dataSource?.name.equals("DATA_DISK_CACHE")) {
                                marker.showInfoWindow()
                            }
                            return true
                        }

                    }).into(ivAvatar)


                val tvName = rootView.findViewById(R.id.tv_name) as TextView
                tvName.text = currentUser.name

                val tvEmail = rootView.findViewById(R.id.tv_email) as TextView
                tvEmail.text = "Email : " + currentUser.email

                val tvPhone = rootView.findViewById(R.id.tv_phone) as TextView
                tvPhone.text = "Phone : " + currentUser.phone


                return rootView
            }

            override fun getInfoContents(marker: Marker): View? {
                return null
            }
        })

        mMap.setOnInfoWindowClickListener {

            val currentUser = users[it.snippet.toInt()]
            Toast.makeText(
                this@MapsActivity4,
                "Hello ${currentUser.name}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    private fun placeMarkerOnMap(index: Int, user: User, location: LatLng) {

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
        markerOptions.snippet("$index")
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