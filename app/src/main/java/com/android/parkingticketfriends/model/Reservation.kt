package com.android.parkingticketfriends.model

import java.io.Serializable

data class Reservation(
    var id: Int,
    var parking_lot_id: Int,
    var start_time: String,
    var end_time: String,
    var stop_time: String?,
)