package com.example.hm_third_count.presentation.countries

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.hm_third_count.data.model.Country

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountriesScreen(
    uiState: CountriesUiState,
    onEvent: (CountriesEvent) -> Unit,
    onCountryClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = { onEvent(CountriesEvent.SearchQueryChanged(it)) },
            label = { Text("Search countries") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            singleLine = true
        )
        
        RegionFilter(
            selectedRegion = uiState.selectedRegion,
            showFavoritesOnly = uiState.showFavoritesOnly,
            onRegionSelected = { onEvent(CountriesEvent.RegionSelected(it)) },
            onShowFavorites = { onEvent(CountriesEvent.ShowFavorites) },
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        
        // Content
        when {
            uiState.isLoading -> {
                LoadingContent()
            }
            uiState.error != null -> {
                ErrorContent(
                    error = uiState.error,
                    onRetry = { onEvent(CountriesEvent.Retry) }
                )
            }
            uiState.isEmpty -> {
                EmptyContent()
            }
            else -> {
                CountriesList(
                    countries = uiState.countries,
                    favorites = uiState.favorites,
                    onCountryClick = onCountryClick,
                    onFavoriteClick = { onEvent(CountriesEvent.ToggleFavorite(it)) },
                    showFavoritesOnly = uiState.showFavoritesOnly
                )
            }
        }
    }
}

@Composable
private fun RegionFilter(
    selectedRegion: String,
    showFavoritesOnly: Boolean,
    onRegionSelected: (String) -> Unit,
    onShowFavorites: () -> Unit,
    modifier: Modifier = Modifier
) {
    val regions = listOf("Africa", "Americas", "Asia", "Europe", "Oceania")
    val regionNames = listOf("Africa", "Americas", "Asia", "Europe", "Oceania")
    
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(bottom = 8.dp)
    ) {
        item {
            FilterChip(
                onClick = onShowFavorites,
                label = { Text("Favourite") },
                selected = showFavoritesOnly,
                leadingIcon = if (showFavoritesOnly) {
                    { Icon(Icons.Default.Favorite, contentDescription = null, modifier = Modifier.size(16.dp)) }
                } else null
            )
        }
        
        items(regions.size) { index ->
            FilterChip(
                onClick = { onRegionSelected(regions[index]) },
                label = { Text(regionNames[index]) },
                selected = selectedRegion == regions[index] && !showFavoritesOnly
            )
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator()
            Text("Loading countries...")
        }
    }
}

@Composable
private fun ErrorContent(
    error: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = error,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error
            )
            Button(onClick = onRetry) {
                Text("Retry")
            }
        }
    }
}

@Composable
private fun EmptyContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No countries found",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun CountriesList(
    countries: List<Country>,
    favorites: Set<String>,
    onCountryClick: (String) -> Unit,
    onFavoriteClick: (String) -> Unit,
    showFavoritesOnly: Boolean = false
) {
    if (showFavoritesOnly && countries.isEmpty()) {
        // Показываем специальное сообщение для пустого избранного
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "No favorite countries yet",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Add countries to favorites by tapping the heart icon",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(countries) { country ->
                CountryItem(
                    country = country,
                    isFavorite = favorites.contains(country.code),
                    onClick = { onCountryClick(country.code) },
                    onFavoriteClick = { onFavoriteClick(country.code) }
                )
            }
        }
    }
}

@Composable
private fun CountryItem(
    country: Country,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = country.flags.png,
                contentDescription = "Flag of ${country.name.common}",
                modifier = Modifier.size(60.dp, 40.dp),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = country.name.common,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = country.capital?.firstOrNull() ?: "No capital",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${country.region} • ${String.format("%,d", country.population)} people",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            IconButton(onClick = onFavoriteClick) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                    tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}