package com.example.hm_third_count.data.api

import com.example.hm_third_count.data.model.Country

interface CountriesApi {
    suspend fun getAllCountries(): List<Country>
    suspend fun searchCountriesByName(name: String): List<Country>
    suspend fun getCountryByCode(code: String): List<Country>
    suspend fun getCountriesByRegion(region: String): List<Country>
}
