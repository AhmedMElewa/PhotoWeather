package com.elewa.photoweather.modules.image_view.domain.repository

interface ImageRepository {
    suspend fun deleteImage(fileId: Int):Result<Boolean>
}