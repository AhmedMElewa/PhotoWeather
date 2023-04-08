package com.elewa.photoweather.modules.home.di

import com.elewa.photoweather.modules.home.data.repository.HomeRepositoryImpl
import com.elewa.photoweather.modules.home.domain.repository.HomeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class ImageCashModule {
    @Binds
    abstract fun bindImageCashRepository(repository: HomeRepositoryImpl): HomeRepository
}