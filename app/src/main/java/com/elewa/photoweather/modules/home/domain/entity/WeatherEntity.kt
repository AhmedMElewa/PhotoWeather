package com.elewa.photoweather.modules.home.domain.entity

data class WeatherEntity(
    val temp: String,
    val description: String,
    val place: String,
) {
}

fun Double.toCelsius() = String.format("%.2f", (this-272.15)).toDouble()