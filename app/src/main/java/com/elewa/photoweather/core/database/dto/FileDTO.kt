package com.elewa.photoweather.core.database.dto

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.elewa.photoweather.core.database.File_TABLE

@Entity(tableName = File_TABLE)
data class FileDTO(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val localPath: String,
)
