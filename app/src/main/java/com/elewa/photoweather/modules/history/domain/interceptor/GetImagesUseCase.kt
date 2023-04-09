package com.elewa.photoweather.modules.history.domain.interceptor

import com.elewa.photoweather.base.BaseUseCase
import com.elewa.photoweather.modules.history.domain.repository.HistoryRepository
import com.elewa.photoweather.modules.history.domain.entity.ImageEntity
import com.elewa.photoweather.modules.history.data.mapper.toImageEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

import javax.inject.Inject

class GetImagesUseCase @Inject constructor(private val historyRepository: HistoryRepository) :
    BaseUseCase<Unit, Flow<List<ImageEntity>>> {
    override suspend fun execute(params: Unit?): Flow<List<ImageEntity>> {
        return flow {
            historyRepository.getAllImages().collect {
                emit(it.map { it.toImageEntity() })
            }
        }
    }
}