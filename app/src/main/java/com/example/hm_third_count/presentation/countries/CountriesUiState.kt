package com.example.hm_third_count.presentation.countries

import com.example.hm_third_count.data.model.Country

data class CountriesUiState(
    val isLoading: Boolean = false,
    val countries: List<Country> = emptyList(),
    val error: String? = null,
    val searchQuery: String = "",
    val selectedRegion: String = "",
    val favorites: Set<String> = emptySet(),
    val showFavoritesOnly: Boolean = false
) {
    // Empty only when not in favorites mode (favorites-only empty has its own UI)
    val isEmpty: Boolean
        get() = !isLoading && countries.isEmpty() && error == null && !showFavoritesOnly
}

sealed class CountriesEvent {
    data class SearchQueryChanged(val query: String) : CountriesEvent()
    data class RegionSelected(val region: String) : CountriesEvent()
    data class ToggleFavorite(val countryCode: String) : CountriesEvent()
    object ShowFavorites : CountriesEvent()
    object Retry : CountriesEvent()
    object LoadCountries : CountriesEvent()
}