package com.elewa.photoweather.modules.history.domain.repository

import com.elewa.photoweather.core.database.dto.FileDTO
import kotlinx.coroutines.flow.Flow

interface HistoryRepository {

    suspend fun getAllImages(): Flow<List<FileDTO>>

}