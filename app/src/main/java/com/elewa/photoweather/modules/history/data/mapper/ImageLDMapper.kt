package com.elewa.photoweather.modules.history.data.mapper

import com.elewa.photoweather.core.database.dto.FileDTO
import com.elewa.photoweather.modules.history.domain.entity.ImageEntity

fun FileDTO.toImageEntity() = ImageEntity(
    id = this.id,
    imagePath = this.localPath
)

