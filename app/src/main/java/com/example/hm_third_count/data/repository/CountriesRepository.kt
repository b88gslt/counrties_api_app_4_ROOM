package com.example.hm_third_count.data.repository

import com.example.hm_third_count.data.api.CountriesApi
import com.example.hm_third_count.data.local.FavoriteDao
import com.example.hm_third_count.data.local.FavoriteEntity
import com.example.hm_third_count.data.model.Country
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CountriesRepository @Inject constructor(
    private val api: CountriesApi,
    private val favoriteDao: FavoriteDao
) {
    /** Emits the set of favorite country codes from Room — survives restarts. */
    val favorites: Flow<Set<String>> = favoriteDao.observeAll()
        .map { list -> list.map { it.countryCode }.toSet() }

    suspend fun getAllCountries(): Result<List<Country>> = runCatching {
        api.getAllCountries()
    }

    suspend fun searchCountries(query: String): Result<List<Country>> = runCatching {
        if (query.isBlank()) api.getAllCountries()
        else api.searchCountriesByName(query)
    }

    suspend fun getCountryByCode(code: String): Result<Country?> = runCatching {
        api.getCountryByCode(code).firstOrNull()
    }

    suspend fun getCountriesByRegion(region: String): Result<List<Country>> = runCatching {
        api.getCountriesByRegion(region)
    }

    suspend fun addToFavorites(countryCode: String) {
        favoriteDao.insert(FavoriteEntity(countryCode))
    }

    suspend fun removeFromFavorites(countryCode: String) {
        favoriteDao.delete(FavoriteEntity(countryCode))
    }

    suspend fun isFavorite(countryCode: String): Boolean =
        favoriteDao.isFavorite(countryCode)
}
