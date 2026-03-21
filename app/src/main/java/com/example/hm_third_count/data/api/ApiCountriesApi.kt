package com.example.hm_third_count.data.api

import com.example.hm_third_count.data.model.ApiCountriesCountry
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiCountriesApi {
    
    @GET("countries")
    suspend fun getAllCountries(): List<ApiCountriesCountry>
    
    @GET("countries/name/{name}")
    suspend fun searchCountriesByName(@Path("name") name: String): List<ApiCountriesCountry>
    
    @GET("countries/alpha/{code}")
    suspend fun getCountryByCode(@Path("code") code: String): List<ApiCountriesCountry>
    
    @GET("countries/region/{region}")
    suspend fun getCountriesByRegion(@Path("region") region: String): List<ApiCountriesCountry>
    
    companion object {
        const val BASE_URL = "https://apicountries.com/"
    }
}