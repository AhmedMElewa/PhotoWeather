package com.elewa.photoweather.modules.image_view.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elewa.photoweather.R
import com.elewa.photoweather.modules.image_view.domain.interceptor.DeleteImageUseCase
import com.elewa.photoweather.modules.image_view.presentation.uimodel.ImageSideEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImageViewModel @Inject constructor(
    private val deleteImageUseCase: DeleteImageUseCase,
) : ViewModel() {

    val uiEffects = MutableSharedFlow<ImageSideEffect>(0)

    fun deleteImage(imgId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteImageUseCase.execute(imgId).fold(
                {
                    updateEffect(ImageSideEffect.DeleteImage(R.string.image_deleted))
                }, {
                    updateEffect(ImageSideEffect.Error(R.string.image_save_error))
                }

            )
        }
    }

    private fun updateEffect(effect: ImageSideEffect) {
        viewModelScope.launch {
            uiEffects.emit(effect)
        }
    }

}