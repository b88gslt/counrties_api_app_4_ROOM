package com.example.hm_third_count.data.repository

import com.example.hm_third_count.data.api.CountriesApi
import com.example.hm_third_count.data.local.FavoriteDao
import com.example.hm_third_count.data.local.FavoriteEntity
import com.example.hm_third_count.data.model.Country
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CountriesRepository @Inject constructor(
    private val api: CountriesApi,
    private val favoriteDao: FavoriteDao,
    private val json: Json
) {
    val favorites: Flow<Set<String>> = favoriteDao.observeAll()
        .map { list -> list.map { it.countryCode }.toSet() }

    /** Countries reconstructed from Room snapshots (favorites with saved JSON). */
    val favoriteCountriesFromRoom: Flow<List<Country>> = favoriteDao.observeAll().map { list ->
        list.mapNotNull { entity ->
            entity.countrySnapshotJson?.let { raw ->
                runCatching { json.decodeFromString<Country>(raw) }.getOrNull()
            }
        }
    }

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

    suspend fun addToFavorites(country: Country) {
        val snapshot = json.encodeToString(Country.serializer(), country)
        favoriteDao.insert(FavoriteEntity(country.code, snapshot))
    }

    suspend fun removeFromFavorites(countryCode: String) {
        favoriteDao.delete(FavoriteEntity(countryCode))
    }

    suspend fun isFavorite(countryCode: String): Boolean =
        favoriteDao.isFavorite(countryCode)
}
