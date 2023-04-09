package com.elewa.photoweather.modules.image_view.presentation.uimodel

import androidx.annotation.StringRes

sealed class ImageSideEffect {

    data class Error(@StringRes val message: Int) : ImageSideEffect()
    data class DeleteImage(@StringRes val message: Int) : ImageSideEffect()

}