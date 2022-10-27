package com.globalpatments.android.sdk.merchant3ds.networking.di

import com.globalpatments.android.sdk.merchant3ds.BuildConfig
import com.globalpatments.android.sdk.merchant3ds.networking.Merchant3DSApi
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object NetworkingModule {

    @Provides
    fun providesJson(): Json {
        return Json {
            ignoreUnknownKeys = true
            prettyPrint = true
            isLenient = true
        }
    }

    @Provides
    fun provideRetrofit(json: Json): Retrofit {
        val contentType = "application/json; charset=utf-8".toMediaType()
        return Retrofit
            .Builder()
            .baseUrl(BuildConfig.SERVER_URL)
            .client(
                OkHttpClient
                    .Builder()
                    .addInterceptor(
                        HttpLoggingInterceptor().apply {
                            level = HttpLoggingInterceptor.Level.BODY
                        }
                    )
                    .build()
            )
            .addConverterFactory(json.asConverterFactory(contentType = contentType))
            .build()
    }

    @Provides
    fun provideApi(retrofit: Retrofit): Merchant3DSApi {
        return retrofit.create(Merchant3DSApi::class.java)
    }
}