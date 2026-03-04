package com.aa.carrepair.core.di

import android.content.Context
import com.aa.carrepair.core.network.NetworkMonitor
import com.aa.carrepair.core.preferences.UserPreferencesManager
import com.aa.carrepair.core.privacy.PrivacyManager
import com.aa.carrepair.core.privacy.TelemetryRedactor
import com.aa.carrepair.core.security.EncryptionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoreModule {

    @Provides
    @Singleton
    fun provideNetworkMonitor(
        @ApplicationContext context: Context
    ): NetworkMonitor = NetworkMonitor(context)

    @Provides
    @Singleton
    fun provideEncryptionManager(
        @ApplicationContext context: Context
    ): EncryptionManager = EncryptionManager(context)

    @Provides
    @Singleton
    fun providePrivacyManager(
        @ApplicationContext context: Context
    ): PrivacyManager = PrivacyManager(context)

    @Provides
    @Singleton
    fun provideTelemetryRedactor(): TelemetryRedactor = TelemetryRedactor()

    @Provides
    @Singleton
    fun provideUserPreferencesManager(
        @ApplicationContext context: Context
    ): UserPreferencesManager = UserPreferencesManager(context)
}
