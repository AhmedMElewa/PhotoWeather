package com.elewa.photoweather.modules.home.presentation.uimodel

import androidx.annotation.StringRes

sealed class HomeSideEffect {

    data class Error(@StringRes val message: Int) : HomeSideEffect()
    data class DeleteImage(@StringRes val message: Int) : HomeSideEffect()
}