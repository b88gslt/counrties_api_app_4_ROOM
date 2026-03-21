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

    // In-memory cache — loaded once, filtered locally
    private var allCountries: List<Country> = emptyList()
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
                _uiState.value = _uiState.value.copy(
                    selectedRegion = event.region,
                    searchQuery = "",
                    showFavoritesOnly = false
                )
                filterByRegion(event.region)
            }
            CountriesEvent.ShowFavorites -> {
                _uiState.value = _uiState.value.copy(
                    showFavoritesOnly = true,
                    selectedRegion = "",
                    searchQuery = ""
                )
                applyFavoritesFilter()
            }
            is CountriesEvent.ToggleFavorite -> toggleFavorite(event.countryCode)
            CountriesEvent.Retry -> loadCountries()
            CountriesEvent.LoadCountries -> loadCountries()
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

    private fun applyFavoritesFilter() {
        val favs = _uiState.value.favorites
        _uiState.update { it.copy(countries = allCountries.filter { c -> c.code in favs }) }
    }

    private fun toggleFavorite(countryCode: String) {
        viewModelScope.launch {
            if (repository.isFavorite(countryCode)) repository.removeFromFavorites(countryCode)
            else repository.addToFavorites(countryCode)
        }
    }

    private fun observeFavorites() {
        repository.favorites
            .onEach { favs ->
                _uiState.update { current ->
                    if (current.showFavoritesOnly) {
                        current.copy(favorites = favs, countries = allCountries.filter { it.code in favs })
                    } else {
                        current.copy(favorites = favs)
                    }
                }
            }
            .launchIn(viewModelScope)
    }
}
