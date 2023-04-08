package com.elewa.photoweather.modules.home.domain.interceptor

import com.elewa.photoweather.base.BaseUseCase
import com.elewa.photoweather.core.exceptions.DomainExceptions
import com.elewa.photoweather.modules.home.domain.repository.HomeRepository

import javax.inject.Inject

class SaveImageUseCase @Inject constructor(private val imageCashRepository: HomeRepository) :
    BaseUseCase<String, Result<Long>> {
    override suspend fun execute(params: String?): Result<Long> {
        requireNotNull(params)
        return imageCashRepository.insertImage(params).fold({
            Result.success(it)
        }, {
            Result.failure(DomainExceptions.UnknownException)
        })
    }
}