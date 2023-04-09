package com.elewa.photoweather.core.di

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
}

