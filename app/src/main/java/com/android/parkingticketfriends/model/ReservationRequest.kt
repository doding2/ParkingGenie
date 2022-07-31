package com.android.parkingticketfriends.model

data class ReservationRequest(
    var parking_lot_id: Int,
    var start_time: String,
    var end_time: String
)