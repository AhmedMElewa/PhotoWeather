package com.elewa.photoweather.core.exceptions

import androidx.annotation.StringRes
import java.io.IOException


data class APIException(
    @StringRes val errorMessage: Int? = null
) : IOException()