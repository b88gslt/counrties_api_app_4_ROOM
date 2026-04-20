package com.example.hm_third_count.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hm_third_count.data.repository.CountriesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CountryDetailViewModel @Inject constructor(
    private val repository: CountriesRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val countryCode: String = checkNotNull(savedStateHandle["countryCode"])

    private val _uiState = MutableStateFlow(CountryDetailUiState(isLoading = true))
    val uiState: StateFlow<CountryDetailUiState> = _uiState.asStateFlow()

    init {
        loadCountryDetail()
        observeFavorites()
    }

    fun onEvent(event: CountryDetailEvent) {
        when (event) {
            CountryDetailEvent.Retry -> loadCountryDetail()
            CountryDetailEvent.ToggleFavorite -> toggleFavorite()
        }
    }

    private fun loadCountryDetail() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            repository.getCountryByCode(countryCode)
                .onSuccess { country ->
                    if (country == null) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                country = null,
                                error = "Country not found"
                            )
                        }
                    } else {
                        _uiState.update { s -> s.copy(isLoading = false, country = country, error = null) }
                    }
                }
                .onFailure { _uiState.update { s -> s.copy(isLoading = false, error = it.message ?: "Failed to load country details") } }
        }
    }

    private fun toggleFavorite() {
        viewModelScope.launch {
            val country = _uiState.value.country ?: return@launch
            if (repository.isFavorite(countryCode)) repository.removeFromFavorites(countryCode)
            else repository.addToFavorites(country)
        }
    }

    private fun observeFavorites() {
        repository.favorites
            .onEach { _uiState.update { s -> s.copy(isFavorite = countryCode in it) } }
            .launchIn(viewModelScope)
    }
}
