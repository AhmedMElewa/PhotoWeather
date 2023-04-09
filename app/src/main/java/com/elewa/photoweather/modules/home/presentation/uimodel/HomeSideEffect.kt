package com.elewa.photoweather.modules.home.presentation.uimodel

import androidx.annotation.StringRes

sealed class HomeSideEffect {

    data class Error(@StringRes val message: Int) : HomeSideEffect()

    data class ImageSaved(val imgState: ImageUiModel) : HomeSideEffect()
    data class WeatherLoaded(val weatherState: WeatherUiModel) : HomeSideEffect()
}