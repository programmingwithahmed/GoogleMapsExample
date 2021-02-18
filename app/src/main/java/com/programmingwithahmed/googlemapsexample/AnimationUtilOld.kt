package com.programmingwithahmed.googlemapsexample

import android.animation.ObjectAnimator
import android.animation.TypeEvaluator
import android.util.Property
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker


class AnimationUtilOld {


    companion object {

        fun animateMarkerTo(marker: Marker, finalPosition: LatLng) {

            animateMarkerToICS(marker, finalPosition)

        }

        private fun animateMarkerToICS(marker: Marker, finalPosition: LatLng) {

            val latLngInterpolator: LatLngInterpolator = LatLngInterpolator.Linear()

            val typeEvaluator: TypeEvaluator<LatLng> =
                TypeEvaluator { fraction, startValue, endValue ->
                    latLngInterpolator.interpolate(
                        fraction,
                        startValue,
                        endValue
                    )
                }

            val property: Property<Marker, LatLng> = Property.of(Marker::class.java, LatLng::class.java, "position")
            val animator = ObjectAnimator.ofObject(marker, property, typeEvaluator, finalPosition)
            animator.duration = 300
            animator.start()
        }
    }





    internal interface LatLngInterpolator {

        fun interpolate(fraction: Float, a: LatLng, b: LatLng): LatLng

        class Linear : LatLngInterpolator {

            override fun interpolate(fraction: Float, a: LatLng, b: LatLng): LatLng {

                val lat = (b.latitude - a.latitude) * fraction + a.latitude
                var lngDelta = b.longitude - a.longitude

                // Take the shortest path across the 180th meridian.
                if (Math.abs(lngDelta) > 180) {
                    lngDelta -= Math.signum(lngDelta) * 360
                }

                val lng = lngDelta * fraction + a.longitude
                return LatLng(lat, lng)
            }

        }

    }

}