package com.elewa.photoweather.modules.history.data.repository

import com.elewa.photoweather.core.database.dto.FileDTO
import com.elewa.photoweather.modules.history.data.ds.GetImagesLocalDataSource
import com.elewa.photoweather.modules.history.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class HistoryRepositoryImpl @Inject constructor(
    private val imageLocalDs: GetImagesLocalDataSource
) : HistoryRepository {

    override suspend fun getAllImages(): Flow<List<FileDTO>> {
        return imageLocalDs.getImages()
    }
}