package com.elewa.photoweather.modules.home.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elewa.photoweather.R
import com.elewa.photoweather.core.exceptions.APIException
import com.elewa.photoweather.modules.home.domain.entity.WeatherParams
import com.elewa.photoweather.modules.home.domain.interceptor.GetWeatherUseCase
import com.elewa.photoweather.modules.home.domain.interceptor.SaveImageUseCase
import com.elewa.photoweather.modules.home.domain.mapper.toWeatherUiModel
import com.elewa.photoweather.modules.home.presentation.uimodel.HomeSideEffect
import com.elewa.photoweather.modules.home.presentation.uimodel.HomeUiState
import com.elewa.photoweather.modules.home.presentation.uimodel.ImageUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val saveImageUseCase: SaveImageUseCase,
    private val getWeatherUseCase: GetWeatherUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Empty)
    val uiState: StateFlow<HomeUiState> = _uiState

    val uiEffects = MutableSharedFlow<HomeSideEffect>(0)

    fun saveImage(imgPath: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = HomeUiState.Loading(true)
            saveImageUseCase.execute(imgPath).fold(
                {
                    if (it > 0) {
                        updateEffect(HomeSideEffect.ImageSaved(ImageUiModel(it, imgPath)))
                    } else {
                        updateEffect(HomeSideEffect.Error(R.string.image_save_error))
                        _uiState.value = HomeUiState.Loading(false)
                    }


                }, {
                    _uiState.value = HomeUiState.Loading(false)
                    updateEffect(HomeSideEffect.Error(R.string.image_save_error))
                }
            )
        }
    }

    private fun updateEffect(effect: HomeSideEffect) {
        viewModelScope.launch {
            uiEffects.emit(effect)
        }
    }

    fun getWeather(lat: String, lon: String, city: String) {
        _uiState.value = HomeUiState.Loading(true)
        viewModelScope.launch(Dispatchers.IO) {
            getWeatherUseCase.execute(WeatherParams(lat, lon,city)).fold({
                _uiState.value = HomeUiState.Loading(false)
                updateEffect(HomeSideEffect.WeatherLoaded(it.toWeatherUiModel()))
            }, {
                _uiState.value = HomeUiState.Loading(false)
                it.handleError()
            })
        }

    }

    private fun Throwable.handleError() {
        when (this@handleError) {
            is APIException ->
                if (this@handleError.errorMessage != null) {
                    updateEffect(HomeSideEffect.Error(this@handleError.errorMessage))
                } else {
                    updateEffect(HomeSideEffect.Error(R.string.generic_unknown_error))
                }
            else -> updateEffect(HomeSideEffect.Error(R.string.generic_unknown_error))
        }
    }


}