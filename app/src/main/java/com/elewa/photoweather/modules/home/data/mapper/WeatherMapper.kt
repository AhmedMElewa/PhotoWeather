package com.elewa.photoweather.modules.home.data.mapper

import com.elewa.photoweather.modules.home.data.model.ResponseWeatherModel
import com.elewa.photoweather.modules.home.domain.entity.WeatherEntity
import com.elewa.photoweather.modules.home.domain.entity.toCelsius

fun ResponseWeatherModel.toWeatherEntity() = WeatherEntity(
    temp = "Temp: ${main.temp.toCelsius()}",
    description = weather[0].description,
    place = name,
)