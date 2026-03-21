package com.example.hm_third_count.data.api

import com.example.hm_third_count.data.model.Country
import com.example.hm_third_count.data.model.toCountry

internal class ApiCountriesApiAdapter(private val apiCountriesApi: ApiCountriesApi) : CountriesApi {
    
    override suspend fun getAllCountries(): List<Country> {
        return apiCountriesApi.getAllCountries().map { it.toCountry() }
    }
    
    override suspend fun searchCountriesByName(name: String): List<Country> {
        return try {
            apiCountriesApi.searchCountriesByName(name).map { it.toCountry() }
        } catch (e: Exception) {
            val allCountries = getAllCountries()
            allCountries.filter { 
                it.name.common.contains(name, ignoreCase = true) ||
                it.name.official.contains(name, ignoreCase = true)
            }
        }
    }
    
    override suspend fun getCountryByCode(code: String): List<Country> {
        return try {
            apiCountriesApi.getCountryByCode(code).map { it.toCountry() }
        } catch (e: Exception) {
            val allCountries = getAllCountries()
            allCountries.filter { it.code.equals(code, ignoreCase = true) }
        }
    }
    
    override suspend fun getCountriesByRegion(region: String): List<Country> {
        return try {
            apiCountriesApi.getCountriesByRegion(region).map { it.toCountry() }
        } catch (e: Exception) {
            val allCountries = getAllCountries()
            allCountries.filter { it.region.equals(region, ignoreCase = true) }
        }
    }
}