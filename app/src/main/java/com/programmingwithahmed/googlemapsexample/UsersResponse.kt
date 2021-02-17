package com.programmingwithahmed.googlemapsexample

import com.google.gson.annotations.SerializedName

data class User(

    val id: Int,
    val name: String,
    val username: String,
    val email: String,
    val image: String,
    val address: Address,
    val phone: String,
    val website: String,
    val company: Company

)


data class Company(

    val name: String,
    val catchPhrase: String,
    val bs: String
)


data class Address(

    val street: String,
    val suite: String,
    val city: String,
    @SerializedName("zipcode") val zipCode: String,
    val geo: Geo
)


data class Geo(

    val lat: Double,
    val lng: Double
)


