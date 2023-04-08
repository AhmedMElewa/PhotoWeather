package com.elewa.photoweather.core.mapper

import com.elewa.photoweather.core.database.dto.FileDTO
import com.elewa.photoweather.core.domain.entity.ImageEntity
import com.elewa.photoweather.modules.home.presentation.uimodel.ImageUiModel

fun FileDTO.toImageEntity() = ImageEntity(
    id = this.id,
    imagePath = this.localPath
)

fun ImageEntity.toImageUiModel() = ImageUiModel(
    imgId = this.id,
    imgPath = this.imagePath
)