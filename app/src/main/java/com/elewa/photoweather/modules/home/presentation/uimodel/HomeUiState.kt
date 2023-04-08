package com.elewa.photoweather.modules.home.presentation.uimodel


/**
 * A generic class that holds a value with its loading status.
 * @param <T>
</T> */
sealed class HomeUiState {
    object Empty : HomeUiState()
    data class Loading(val loading: Boolean) : HomeUiState()
    data class Loaded(val imgState: ImageUiModel) : HomeUiState()
    data class WeatherLoaded(val weatherState: WeatherUiModel) : HomeUiState()

}