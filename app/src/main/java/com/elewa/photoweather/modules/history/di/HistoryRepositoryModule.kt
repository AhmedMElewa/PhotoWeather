package com.elewa.photoweather.modules.history.di

import com.elewa.photoweather.modules.history.data.repository.HistoryRepositoryImpl
import com.elewa.photoweather.modules.history.domain.repository.HistoryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class HistoryRepositoryModule {
    @Binds
    abstract fun bindImageCashRepository(repository: HistoryRepositoryImpl): HistoryRepository
}