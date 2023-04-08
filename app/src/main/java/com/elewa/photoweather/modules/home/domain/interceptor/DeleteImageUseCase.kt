package com.elewa.photoweather.modules.home.domain.interceptor

import com.elewa.photoweather.base.BaseUseCase
import com.elewa.photoweather.core.exceptions.DomainExceptions
import com.elewa.photoweather.modules.home.domain.repository.HomeRepository

import javax.inject.Inject

class DeleteImageUseCase @Inject constructor(private val imageCashRepository: HomeRepository) :
    BaseUseCase<Int, Result<Boolean>> {
    override suspend fun execute(params: Int?): Result<Boolean> {
        requireNotNull(params)
        return imageCashRepository.deleteImage(params).fold({
            Result.success(it)
        }, {
            Result.failure(DomainExceptions.UnknownException)
        })
    }
}