package com.android.parkingticketfriends.ui.common

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.android.parkingticketfriends.model.ParkingMarker
import com.android.parkingticketfriends.model.Reservation
import com.android.parkingticketfriends.server.RetrofitInstance
import com.google.android.gms.maps.model.Marker
import retrofit2.Response

class MapsViewModel(context: Context): ViewModel() {

    val server = RetrofitInstance.getService()

    // 마커 정보
    val parkingMarkerInfo: LiveData<MutableList<ParkingMarker>> = liveData {
        val response: Response<MutableList<ParkingMarker>> = server.getParkingLots()

        if (response.isSuccessful) {
            val result = response.body() ?: mutableListOf()
            emit(result)
        } else {
            emit(mutableListOf())
        }
    }

    // 마커
    val markers = MutableLiveData<MutableMap<Marker, ParkingMarker>>().apply {
        value = mutableMapOf()
    }


    // 예약 정보들
    var reservations = MutableLiveData<MutableList<Reservation>>().apply {
        value = mutableListOf()
    }

//        liveData {
//        val response = server.getReservations()
//
//        if (response.isSuccessful) {
//            val result = response.body()
//            if (result != null) {
//                emit(result)
//            }
//        }
//        emit(mutableListOf())
//    }

//    fun requestReservations() {
//        println("예약 목록 요청2")
//        reservations = liveData {
//            val response = server.getReservations()
//
//            println("Returned: $response")
//
//            if (response.isSuccessful) {
//                val result = response.body() ?: Any()
//                emit(result as MutableList<Reservation>)
//            } else {
//                emit(mutableListOf())
//            }
//        } as MutableLiveData<MutableList<Reservation>>
//    }

}