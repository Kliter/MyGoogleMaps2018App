package com.codingwithmitch.googlemaps2018.models

import com.google.firebase.firestore.GeoPoint
import java.util.*

data class UserLocation(
        var geo_point: GeoPoint? = null,
        var timestamp: Date? = null,
        var user: User? = null
)