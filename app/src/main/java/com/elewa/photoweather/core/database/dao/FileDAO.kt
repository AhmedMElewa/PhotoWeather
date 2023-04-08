package com.elewa.photoweather.core.database.dao

import androidx.room.*
import com.elewa.photoweather.core.database.dto.FileDTO
import com.elewa.photoweather.core.database.File_TABLE
import kotlinx.coroutines.flow.Flow

@Dao
interface FileDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFile(file: FileDTO): Long

    @Query("SELECT * FROM $File_TABLE")
    fun selectAllFiles(): Flow<List<FileDTO>>

//    @Query("SELECT localPath FROM $File_TABLE")
//    fun getFilesLocalPath(): List<String>

    @Query("DELETE FROM $File_TABLE WHERE id=:imageId")
    fun deleteFile(imageId: Int): Int

}