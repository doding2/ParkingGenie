package com.android.parkingticketfriends.server

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {
    companion object {
        private const val HOST_URL = "http://ec2-3-38-37-33.ap-northeast-2.compute.amazonaws.com"
        private lateinit var retrofit: Retrofit

        fun getService(): ServerService {
            if (!::retrofit.isInitialized) {
                retrofit = Retrofit.Builder()
                    .baseUrl(HOST_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }

            return retrofit.create(ServerService::class.java)
        }
    }
}