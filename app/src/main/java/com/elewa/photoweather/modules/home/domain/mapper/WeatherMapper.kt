package com.elewa.photoweather.modules.home.domain.mapper

import com.elewa.photoweather.modules.home.domain.entity.WeatherEntity
import com.elewa.photoweather.modules.home.presentation.uimodel.WeatherUiModel

fun WeatherEntity.toWeatherUiModel() = WeatherUiModel(
    temp = temp,
    description = description,
    place = place,
)