package com.elewa.photoweather.modules.home.data.repository

import com.elewa.photoweather.R
import com.elewa.photoweather.core.exceptions.DomainExceptions
import com.elewa.photoweather.modules.home.data.ds.local.SaveImageLocalDataSource
import com.elewa.photoweather.core.exceptions.APIException
import com.elewa.photoweather.modules.home.data.ds.remote.WeatherRemoteDataSource
import com.elewa.photoweather.modules.home.data.model.ResponseWeatherModel
import com.elewa.photoweather.modules.home.domain.repository.HomeRepository
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val imageLocalDs: SaveImageLocalDataSource,
    private val remoteDataSource: WeatherRemoteDataSource
) : HomeRepository {

    override suspend fun insertImage(filePath: String): Result<Long> {
        val result = imageLocalDs.saveNewImage(filePath)
        return if (result > 0) {
            Result.success(result)
        } else {
            Result.failure(DomainExceptions.UnknownException)
        }

    }
    override suspend fun getWeather(
        lat: String,
        lon: String,
        city: String
    ): Result<ResponseWeatherModel> {
        return try {
            val response =
                remoteDataSource.getWeather(
                    city = city,
                    latitude = lat,
                    longitude = lon,

                    )
            val body: ResponseWeatherModel? = response.body()
            if (body != null) {
                Result.success(body)
            } else {
                Result.failure(APIException(errorMessage = R.string.generic_unknown_error))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}