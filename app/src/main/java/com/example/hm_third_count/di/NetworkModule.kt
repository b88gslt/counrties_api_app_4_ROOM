package com.example.hm_third_count.di

import com.example.hm_third_count.data.api.ApiCountriesApi
import com.example.hm_third_count.data.api.ApiCountriesApiAdapter
import com.example.hm_third_count.data.api.CountriesApi
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
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()

    @Provides
    @Singleton
    fun provideApiCountriesApi(client: OkHttpClient, json: Json): ApiCountriesApi =
        Retrofit.Builder()
            .baseUrl(ApiCountriesApi.BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(ApiCountriesApi::class.java)

    @Provides
    @Singleton
    fun provideCountriesApi(apiCountriesApi: ApiCountriesApi): CountriesApi =
        ApiCountriesApiAdapter(apiCountriesApi)
}
