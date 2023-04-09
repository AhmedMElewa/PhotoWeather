package com.elewa.photoweather.modules.history.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elewa.photoweather.modules.history.domain.mapper.toImageUiModel
import com.elewa.photoweather.modules.history.domain.interceptor.GetImagesUseCase
import com.elewa.photoweather.modules.history.presentation.uimodel.HistoryUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getImagesUseCase: GetImagesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<HistoryUiState>(HistoryUiState.Empty)
    val uiState: StateFlow<HistoryUiState> = _uiState

    fun getImages() {
        _uiState.value = HistoryUiState.Loading(true)
        viewModelScope.launch(Dispatchers.IO) {
            getImagesUseCase.execute(Unit).collect { imgList ->
                _uiState.value = HistoryUiState.Loaded(imgList.map { it.toImageUiModel() })
            }
        }
    }
}