package com.programmingwithahmed.googlemapsexample

import com.google.gson.annotations.SerializedName


data class RouteResponse (

    val code: String,
    @SerializedName("waypoints")
    val wayPoints: List<WayPoint>,
    val routes: List<Route>

)


data class WayPoint (
    val hint: String,
    val distance: Double,
    val location: List<Double>,
    val name: String
)

data class Route (

    val legs: List<Leg>,

    @SerializedName("weight_name")
    val weightName: String,

    val weight: Double,
    val distance: Double,
    val duration: Double
)

data class Leg (

    val steps: List<Step>,
    val weight: Double,
    val distance: Double,
    val summary: String,
    val duration: Double

)

data class Step (

    val intersections: List<Intersection>,

    @SerializedName("driving_side")
    val drivingSide: String,

    val geometry: String,
    val duration: Double,
    val distance: Double,
    val name: String,
    val weight: Double,
    val mode: String,
    val maneuver: Maneuver,
    val ref: String? = null
)

data class Intersection (

    val out: Long? = null,
    val entry: List<Boolean>,
    val location: List<Double>,
    val bearings: List<Long>,

    @SerializedName("in")
    val intersectionIn: Long? = null
)

data class Maneuver (
    @SerializedName("bearing_after")
    val bearingAfter: Long,

    val location: List<Double>,
    val type: String,

    @SerializedName("bearing_before")
    val bearingBefore: Long,

    val modifier: String? = null
)


