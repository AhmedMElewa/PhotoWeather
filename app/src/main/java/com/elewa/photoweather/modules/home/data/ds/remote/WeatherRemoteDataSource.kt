package com.elewa.photoweather.modules.home.data.ds.remote


import com.elewa.photoweather.BuildConfig
import com.elewa.photoweather.modules.home.data.model.ResponseWeatherModel
import retrofit2.Response
import retrofit2.http.*

interface WeatherRemoteDataSource {

    @POST("data/2.5/weather")
    suspend fun getWeather(
        @Query("q")
        city: String,
        @Query("lat")
        latitude: String,
        @Query("lon")
        longitude: String,
        @Query("appid")
        clientID: String = BuildConfig.CLIENT_ID,
        @Query("units")
        units: String = "imperial"
    ): Response<ResponseWeatherModel>

}