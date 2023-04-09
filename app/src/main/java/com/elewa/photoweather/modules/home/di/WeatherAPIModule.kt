package com.elewa.photoweather.modules.home.di

import com.elewa.photoweather.modules.home.data.ds.remote.WeatherRemoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import retrofit2.Retrofit

@Module
@InstallIn(ViewModelComponent::class)
object WeatherAPIModule {
    @Provides
    fun provideWeatherRemoteDataSource(retrofit: Retrofit): WeatherRemoteDataSource =
        retrofit.create(WeatherRemoteDataSource::class.java)
}