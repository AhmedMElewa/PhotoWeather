package com.elewa.photoweather.modules.history.data.repository

import com.elewa.photoweather.core.database.dto.FileDTO
import com.elewa.photoweather.modules.history.domain.repository.HistoryRepository
import com.elewa.photoweather.core.data.source.local.ImageLocalDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class HistoryRepositoryImpl @Inject constructor(
    private val imageLocalDs: ImageLocalDataSource
) : HistoryRepository {

    override suspend fun getAllImages(): Flow<List<FileDTO>> {
        return imageLocalDs.getImages()
    }
}