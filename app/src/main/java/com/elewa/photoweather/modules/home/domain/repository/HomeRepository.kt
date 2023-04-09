package com.elewa.photoweather.modules.home.domain.repository

import com.elewa.photoweather.modules.home.data.model.ResponseWeatherModel
interface HomeRepository {

    suspend fun insertImage(filePath: String): Result<Long>

    suspend fun getWeather(lat: String,lon: String,city: String): Result<ResponseWeatherModel>
}