package com.elewa.photoweather.modules.history.presentation.uimodel

import com.elewa.photoweather.modules.home.presentation.uimodel.ImageUiModel


/**
 * A generic class that holds a value with its loading status.
 * @param <T>
</T> */
sealed class HistoryUiState {
    object Empty : HistoryUiState()
    data class Loading(val loading: Boolean) : HistoryUiState()
    data class Loaded(val imgList: List<ImageUiModel> = emptyList()) : HistoryUiState()

}