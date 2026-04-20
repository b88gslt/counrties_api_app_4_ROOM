package com.example.hm_third_count.data.api

import com.example.hm_third_count.data.model.Country
import com.example.hm_third_count.data.model.toCountry

internal class ApiCountriesApiAdapter(private val apiCountriesApi: ApiCountriesApi) : CountriesApi {

    override suspend fun getAllCountries(): List<Country> =
        apiCountriesApi.getAllCountries().map { it.toCountry() }

    override suspend fun searchCountriesByName(name: String): List<Country> =
        apiCountriesApi.searchCountriesByName(name).map { it.toCountry() }

    override suspend fun getCountryByCode(code: String): List<Country> =
        apiCountriesApi.getCountryByCode(code).map { it.toCountry() }

    override suspend fun getCountriesByRegion(region: String): List<Country> =
        apiCountriesApi.getCountriesByRegion(region).map { it.toCountry() }
}
