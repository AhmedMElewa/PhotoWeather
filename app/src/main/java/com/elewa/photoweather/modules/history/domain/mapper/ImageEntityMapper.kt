package com.elewa.photoweather.modules.history.domain.mapper

import com.elewa.photoweather.modules.history.domain.entity.ImageEntity
import com.elewa.photoweather.modules.home.presentation.uimodel.ImageUiModel

fun ImageEntity.toImageUiModel() = ImageUiModel(
    imgId = this.id,
    imgPath = this.imagePath
)