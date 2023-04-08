package com.elewa.photoweather.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.elewa.photoweather.core.database.dao.FileDAO
import com.elewa.photoweather.core.database.dto.FileDTO


@Database(
    entities = [FileDTO::class],
    version = 1,
    exportSchema = false
)
abstract class PhotoWeatherDatabase : RoomDatabase() {
    abstract fun fileDao(): FileDAO

}

const val File_TABLE = "Files"



