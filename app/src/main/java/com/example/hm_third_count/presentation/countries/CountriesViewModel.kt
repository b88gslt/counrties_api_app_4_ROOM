package com.example.hm_third_count.presentation.countries

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hm_third_count.data.model.Country
import com.example.hm_third_count.data.repository.CountriesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CountriesViewModel @Inject constructor(
    private val repository: CountriesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CountriesUiState())
    val uiState: StateFlow<CountriesUiState> = _uiState.asStateFlow()

    private var allCountries: List<Country> = emptyList()
    private var favoriteSnapshotCountries: List<Country> = emptyList()
    private var searchJob: Job? = null

    init {
        loadCountries()
        observeFavorites()
    }

    fun onEvent(event: CountriesEvent) {
        when (event) {
            is CountriesEvent.SearchQueryChanged -> {
                _uiState.value = _uiState.value.copy(
                    searchQuery = event.query,
                    selectedRegion = "",
                    showFavoritesOnly = false
                )
                searchCountries(event.query)
            }
            is CountriesEvent.RegionSelected -> {
                val current = _uiState.value
                if (current.selectedRegion == event.region && !current.showFavoritesOnly) {
                    _uiState.value = current.copy(
                        selectedRegion = "",
                        searchQuery = "",
                        showFavoritesOnly = false
                    )
                    filterByRegion("")
                } else {
                    _uiState.value = current.copy(
                        selectedRegion = event.region,
                        searchQuery = "",
                        showFavoritesOnly = false
                    )
                    filterByRegion(event.region)
                }
            }
            CountriesEvent.ShowFavorites -> {
                _uiState.value = _uiState.value.copy(
                    showFavoritesOnly = true,
                    selectedRegion = "",
                    searchQuery = ""
                )
                applyFavoritesFilter()
            }
            is CountriesEvent.ToggleFavorite -> toggleFavorite(event.country)
            CountriesEvent.Retry -> loadCountries()
        }
    }

    private fun loadCountries() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, searchQuery = "", selectedRegion = "", showFavoritesOnly = false) }
            repository.getAllCountries()
                .onSuccess { countries ->
                    allCountries = countries
                    _uiState.update { it.copy(isLoading = false, countries = countries) }
                }
                .onFailure { _uiState.update { s -> s.copy(isLoading = false, error = it.message ?: "Unknown error") } }
        }
    }

    private fun searchCountries(query: String) {
        searchJob?.cancel()
        if (query.isBlank()) {
            _uiState.update { it.copy(countries = allCountries) }
            return
        }
        searchJob = viewModelScope.launch {
            delay(300)
            _uiState.update { it.copy(isLoading = true, error = null) }
            repository.searchCountries(query)
                .onSuccess { _uiState.update { s -> s.copy(isLoading = false, countries = it) } }
                .onFailure { _uiState.update { s -> s.copy(isLoading = false, error = it.message ?: "Search failed") } }
        }
    }

    private fun filterByRegion(region: String) {
        if (region.isEmpty()) {
            _uiState.update { it.copy(countries = allCountries) }
            return
        }
        if (allCountries.isNotEmpty()) {
            _uiState.update { it.copy(countries = allCountries.filter { c -> c.region.equals(region, ignoreCase = true) }) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            repository.getCountriesByRegion(region)
                .onSuccess { _uiState.update { s -> s.copy(isLoading = false, countries = it) } }
                .onFailure { _uiState.update { s -> s.copy(isLoading = false, error = it.message ?: "Filter failed") } }
        }
    }

    private fun favoriteDisplayList(favCodes: Set<String>): List<Country> =
        if (allCountries.isNotEmpty()) {
            allCountries.filter { it.code in favCodes }
        } else {
            favoriteSnapshotCountries.filter { it.code in favCodes }
        }

    private fun applyFavoritesFilter() {
        val favs = _uiState.value.favorites
        _uiState.update { it.copy(countries = favoriteDisplayList(favs)) }
    }

    private fun toggleFavorite(country: Country) {
        viewModelScope.launch {
            if (repository.isFavorite(country.code)) repository.removeFromFavorites(country.code)
            else repository.addToFavorites(country)
        }
    }

    private fun observeFavorites() {
        combine(repository.favorites, repository.favoriteCountriesFromRoom) { codes, snapshots ->
            favoriteSnapshotCountries = snapshots
            codes
        }
            .onEach { codes ->
                _uiState.update { current ->
                    if (current.showFavoritesOnly) {
                        current.copy(
                            favorites = codes,
                            countries = favoriteDisplayList(codes)
                        )
                    } else {
                        current.copy(favorites = codes)
                    }
                }
            }
            .launchIn(viewModelScope)
    }
}
