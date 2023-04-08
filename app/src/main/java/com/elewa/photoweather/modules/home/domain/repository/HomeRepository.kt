package com.elewa.photoweather.modules.home.domain.repository

import com.elewa.photoweather.modules.home.data.model.ResponseWeatherModel
import com.elewa.photoweather.modules.home.domain.entity.WeatherEntity

interface HomeRepository {

    suspend fun insertImage(filePath: String): Result<Long>

    suspend fun deleteImage(fileId: Int):Result<Boolean>

    suspend fun getWeather(lat: String,lon: String,city: String): Result<ResponseWeatherModel>
}