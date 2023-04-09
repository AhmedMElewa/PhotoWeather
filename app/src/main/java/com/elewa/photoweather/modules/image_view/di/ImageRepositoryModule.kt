package com.elewa.photoweather.modules.image_view.di

import com.elewa.photoweather.modules.home.data.repository.HomeRepositoryImpl
import com.elewa.photoweather.modules.home.domain.repository.HomeRepository
import com.elewa.photoweather.modules.image_view.data.repository.ImageRepositoryImpl
import com.elewa.photoweather.modules.image_view.domain.repository.ImageRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class ImageRepositoryModule {
    @Binds
    abstract fun bindImageCashRepository(repository: ImageRepositoryImpl): ImageRepository
}