package com.aa.carrepair.data.di

import android.content.Context
import com.aa.carrepair.data.local.AppDatabase
import com.aa.carrepair.data.local.dao.CalculatorDao
import com.aa.carrepair.data.local.dao.ChatDao
import com.aa.carrepair.data.local.dao.DtcDao
import com.aa.carrepair.data.local.dao.EstimateDao
import com.aa.carrepair.data.local.dao.FleetDao
import com.aa.carrepair.data.local.dao.VehicleDao
import com.aa.carrepair.data.remote.api.AgentApi
import com.aa.carrepair.data.remote.api.DtcApi
import com.aa.carrepair.data.remote.api.EstimatorApi
import com.aa.carrepair.data.remote.api.InspectionApi
import com.aa.carrepair.data.remote.api.VehicleApi
import com.aa.carrepair.data.remote.interceptor.AuthInterceptor
import com.aa.carrepair.data.remote.interceptor.LoggingInterceptor
import com.aa.carrepair.data.remote.interceptor.RetryInterceptor
import com.aa.carrepair.data.repository.ChatRepositoryImpl
import com.aa.carrepair.data.repository.DtcRepositoryImpl
import com.aa.carrepair.data.repository.EstimateRepositoryImpl
import com.aa.carrepair.data.repository.FleetRepositoryImpl
import com.aa.carrepair.data.repository.InspectionRepositoryImpl
import com.aa.carrepair.data.repository.VehicleRepositoryImpl
import com.aa.carrepair.domain.repository.ChatRepository
import com.aa.carrepair.domain.repository.DtcRepository
import com.aa.carrepair.domain.repository.EstimateRepository
import com.aa.carrepair.domain.repository.FleetRepository
import com.aa.carrepair.domain.repository.InspectionRepository
import com.aa.carrepair.domain.repository.VehicleRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.sqlcipher.database.SQLiteDatabase
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Named("api_key")
    fun provideApiKey(): String = com.aa.carrepair.data.BuildConfig.AA_API_KEY

    @Provides
    @Named("api_base_url")
    fun provideApiBaseUrl(): String = com.aa.carrepair.data.BuildConfig.AA_API_BASE_URL

    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    @Provides
    @Singleton
    fun provideAuthInterceptor(@Named("api_key") apiKey: String): AuthInterceptor =
        AuthInterceptor(apiKey)

    @Provides
    @Singleton
    fun provideRetryInterceptor(): RetryInterceptor = RetryInterceptor()

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        retryInterceptor: RetryInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(retryInterceptor)
        .addInterceptor(LoggingInterceptor.create(com.aa.carrepair.data.BuildConfig.DEBUG))
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        moshi: Moshi,
        @Named("api_base_url") baseUrl: String
    ): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    @Provides
    @Singleton
    fun provideAgentApi(retrofit: Retrofit): AgentApi = retrofit.create(AgentApi::class.java)

    @Provides
    @Singleton
    fun provideVehicleApi(retrofit: Retrofit): VehicleApi = retrofit.create(VehicleApi::class.java)

    @Provides
    @Singleton
    fun provideEstimatorApi(retrofit: Retrofit): EstimatorApi = retrofit.create(EstimatorApi::class.java)

    @Provides
    @Singleton
    fun provideDtcApi(retrofit: Retrofit): DtcApi = retrofit.create(DtcApi::class.java)

    @Provides
    @Singleton
    fun provideInspectionApi(retrofit: Retrofit): InspectionApi = retrofit.create(InspectionApi::class.java)

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        val passphrase = SQLiteDatabase.getBytes("aa_carrepair_secure_key".toCharArray())
        return AppDatabase.create(context, passphrase)
    }

    @Provides
    fun provideVehicleDao(db: AppDatabase): VehicleDao = db.vehicleDao()

    @Provides
    fun provideChatDao(db: AppDatabase): ChatDao = db.chatDao()

    @Provides
    fun provideEstimateDao(db: AppDatabase): EstimateDao = db.estimateDao()

    @Provides
    fun provideDtcDao(db: AppDatabase): DtcDao = db.dtcDao()

    @Provides
    fun provideFleetDao(db: AppDatabase): FleetDao = db.fleetDao()

    @Provides
    fun provideCalculatorDao(db: AppDatabase): CalculatorDao = db.calculatorDao()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindVehicleRepository(impl: VehicleRepositoryImpl): VehicleRepository

    @Binds
    @Singleton
    abstract fun bindChatRepository(impl: ChatRepositoryImpl): ChatRepository

    @Binds
    @Singleton
    abstract fun bindEstimateRepository(impl: EstimateRepositoryImpl): EstimateRepository

    @Binds
    @Singleton
    abstract fun bindDtcRepository(impl: DtcRepositoryImpl): DtcRepository

    @Binds
    @Singleton
    abstract fun bindFleetRepository(impl: FleetRepositoryImpl): FleetRepository

    @Binds
    @Singleton
    abstract fun bindInspectionRepository(impl: InspectionRepositoryImpl): InspectionRepository
}
