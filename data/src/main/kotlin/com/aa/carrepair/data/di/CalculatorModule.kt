package com.aa.carrepair.data.di

import com.aa.carrepair.data.repository.CalculatorRepositoryImpl
import com.aa.carrepair.domain.repository.CalculatorRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CalculatorModule {
    @Binds
    @Singleton
    abstract fun bindCalculatorRepository(impl: CalculatorRepositoryImpl): CalculatorRepository
}
