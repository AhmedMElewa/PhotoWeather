package com.elewa.photoweather.core.database.di

import android.content.Context
import androidx.room.Room
import com.elewa.photoweather.core.database.PhotoWeatherDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
    ): PhotoWeatherDatabase {
        return Room
            .databaseBuilder(context, PhotoWeatherDatabase::class.java, "photoWeather-db")
            .fallbackToDestructiveMigration()
            .build()
    }

//    @Provides
//    @Singleton
//    internal fun provideRoomDB(applicationContext: Context): PhotoWeatherDatabase {
//        val tempInstance = INSTANCE
//        if (tempInstance != null) {
//            return tempInstance
//        }
//        synchronized(this) {
//            val instance = Room.databaseBuilder(
//                applicationContext,
//                PhotoWeatherDatabase::class.java, "photoWeather-db"
//            ).fallbackToDestructiveMigration()
//                .build()
//            INSTANCE = instance
//            return instance
//        }
//    }


//    @Module
//    @InstallIn(SingletonComponent::class)
//    companion object {
//        @Volatile
//        private var INSTANCE: PhotoWeatherDatabase? = null
//    }


}

