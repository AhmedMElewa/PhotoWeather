package com.elewa.photoweather.modules.image_view.domain.interceptor

import com.elewa.photoweather.base.BaseUseCase
import com.elewa.photoweather.core.exceptions.DomainExceptions
import com.elewa.photoweather.modules.home.domain.repository.HomeRepository
import com.elewa.photoweather.modules.image_view.domain.repository.ImageRepository

import javax.inject.Inject

class DeleteImageUseCase @Inject constructor(private val imageRepository: ImageRepository) :
    BaseUseCase<Int, Result<Boolean>> {
    override suspend fun execute(params: Int?): Result<Boolean> {
        requireNotNull(params)
        return imageRepository.deleteImage(params).fold({
            Result.success(it)
        }, {
            Result.failure(DomainExceptions.UnknownException)
        })
    }
}