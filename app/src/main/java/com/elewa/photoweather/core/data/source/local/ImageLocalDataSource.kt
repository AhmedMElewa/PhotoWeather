package com.elewa.photoweather.core.data.source.local

import com.elewa.photoweather.core.database.PhotoWeatherDatabase
import com.elewa.photoweather.core.database.dto.FileDTO
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ImageLocalDataSource @Inject constructor(
    private val database: PhotoWeatherDatabase
) {

    fun saveNewImage(imageLocalPath: String): Long {
        return database.fileDao().insertFile(
            FileDTO(
                localPath = "$imageLocalPath",
            )
        )
    }

    fun getImages(): Flow<List<FileDTO>> {
        return database.fileDao().selectAllFiles()
    }

    fun deleteImage(imageId: Int): Boolean {
        return database.fileDao().deleteFile(
                imageId
            ) > 0

    }
}