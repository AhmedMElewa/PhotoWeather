package com.elewa.photoweather.modules.image_view.data.repository

import com.elewa.photoweather.core.exceptions.DomainExceptions
import com.elewa.photoweather.modules.image_view.data.ds.ImageLocalDs
import com.elewa.photoweather.modules.image_view.domain.repository.ImageRepository
import javax.inject.Inject

class ImageRepositoryImpl @Inject constructor(
    private val imageLocalDs: ImageLocalDs,
) : ImageRepository {

    override suspend fun deleteImage(fileId: Int): Result<Boolean> {
        return try {
            if (imageLocalDs.deleteImage(fileId)) {
                Result.success(true)
            } else {
                Result.failure(DomainExceptions.UnknownException)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}