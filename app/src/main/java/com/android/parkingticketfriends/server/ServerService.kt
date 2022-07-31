package com.android.parkingticketfriends.server

import com.android.parkingticketfriends.model.ParkingMarker
import com.android.parkingticketfriends.model.Reservation
import com.android.parkingticketfriends.model.ReservationRequest
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ServerService {

    // 주변 주차장 가져오기
    @GET("/parking-lots")
    suspend fun getParkingLots(): Response<MutableList<ParkingMarker>>

    // 예약 리스트 가져오기
    @GET("/reservations/")
    suspend fun getReservations(): Response<Reservation>

    // 예약하기
    @POST("/reservations/")
    suspend fun doReservation(@Body request: ReservationRequest): Response<Any>

    // 예약 중단하기
    @PUT("/reservation/{id}/stop")
    suspend fun stopReservation(@Path(value="id") reservationId: Int): Response<Any>


}