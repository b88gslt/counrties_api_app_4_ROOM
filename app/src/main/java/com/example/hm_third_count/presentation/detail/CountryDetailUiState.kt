package com.example.hm_third_count.presentation.detail

import com.example.hm_third_count.data.model.Country

data class CountryDetailUiState(
    val isLoading: Boolean = false,
    val country: Country? = null,
    val error: String? = null,
    val isFavorite: Boolean = false
)

sealed class CountryDetailEvent {
    object Retry : CountryDetailEvent()
    object ToggleFavorite : CountryDetailEvent()
}