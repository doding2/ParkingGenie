package com.android.parkingticketfriends.model

import java.io.Serializable

data class ParkingMarker(
    var id: Int,
    var latitude: String,
    var longitude: String,
    var name: String,
    var parking_lots: List<ParkingLots>
): Serializable