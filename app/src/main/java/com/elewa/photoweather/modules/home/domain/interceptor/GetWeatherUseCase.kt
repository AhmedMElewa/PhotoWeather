package com.elewa.photoweather.modules.home.domain.interceptor

import com.elewa.photoweather.base.BaseUseCase
import com.elewa.photoweather.core.mapper.toImageEntity
import com.elewa.photoweather.modules.history.domain.repository.HistoryRepository
import com.elewa.photoweather.core.domain.entity.ImageEntity
import com.elewa.photoweather.modules.home.data.mapper.toWeatherEntity
import com.elewa.photoweather.modules.home.domain.entity.WeatherEntity
import com.elewa.photoweather.modules.home.domain.entity.WeatherParams
import com.elewa.photoweather.modules.home.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

import javax.inject.Inject

class GetWeatherUseCase @Inject constructor(private val repository: HomeRepository) :
    BaseUseCase<WeatherParams, Result<WeatherEntity>> {

    override suspend fun execute(params: WeatherParams?): Result<WeatherEntity> {
        requireNotNull(params)
        return repository.getWeather(params.lat, params.lon, params.city).fold({
            Result.success(it.toWeatherEntity())
        }, {
            Result.failure(it)
        })
    }

}