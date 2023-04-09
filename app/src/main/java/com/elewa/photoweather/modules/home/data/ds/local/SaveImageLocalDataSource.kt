package com.elewa.photoweather.modules.home.data.ds.local

import com.elewa.photoweather.core.database.PhotoWeatherDatabase
import com.elewa.photoweather.core.database.dto.FileDTO
import javax.inject.Inject

class SaveImageLocalDataSource @Inject constructor(
    private val database: PhotoWeatherDatabase
) {

    fun saveNewImage(imageLocalPath: String): Long {
        return database.fileDao().insertFile(
            FileDTO(
                localPath = "$imageLocalPath",
            )
        )
    }

}