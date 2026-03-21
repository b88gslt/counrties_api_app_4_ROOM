package com.example.hm_third_count.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.hm_third_count.presentation.countries.CountriesScreen
import com.example.hm_third_count.presentation.countries.CountriesViewModel
import com.example.hm_third_count.presentation.detail.CountryDetailScreen
import com.example.hm_third_count.presentation.detail.CountryDetailViewModel

@Composable
fun CountriesNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "countries") {
        composable("countries") {
            val viewModel: CountriesViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsState()
            CountriesScreen(
                uiState = uiState,
                onEvent = viewModel::onEvent,
                onCountryClick = { navController.navigate("detail/$it") }
            )
        }

        composable("detail/{countryCode}") {
            val viewModel: CountryDetailViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsState()
            CountryDetailScreen(
                uiState = uiState,
                onEvent = viewModel::onEvent,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
