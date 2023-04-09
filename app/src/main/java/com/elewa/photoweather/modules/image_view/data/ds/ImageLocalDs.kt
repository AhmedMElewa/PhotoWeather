package com.elewa.photoweather.modules.image_view.data.ds

import com.elewa.photoweather.core.database.PhotoWeatherDatabase
import com.elewa.photoweather.core.database.dto.FileDTO
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ImageLocalDs @Inject constructor(
    private val database: PhotoWeatherDatabase
) {

    fun deleteImage(imageId: Int): Boolean {
        return database.fileDao().deleteFile(
            imageId
        ) > 0

    }
}