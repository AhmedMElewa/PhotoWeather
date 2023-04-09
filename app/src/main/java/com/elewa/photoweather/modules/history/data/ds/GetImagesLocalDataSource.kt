package com.elewa.photoweather.modules.history.data.ds

import com.elewa.photoweather.core.database.PhotoWeatherDatabase
import com.elewa.photoweather.core.database.dto.FileDTO
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetImagesLocalDataSource @Inject constructor(
    private val database: PhotoWeatherDatabase
) {
    fun getImages(): Flow<List<FileDTO>> {
        return database.fileDao().selectAllFiles()
    }

}