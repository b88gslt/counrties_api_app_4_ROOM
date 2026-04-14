package com.example.hm_third_count.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Country(
    @SerialName("name")
    val name: CountryName,
    @SerialName("cca3")
    val code: String,
    @SerialName("capital")
    val capital: List<String>? = null,
    @SerialName("region")
    val region: String,
    @SerialName("subregion")
    val subregion: String? = null,
    @SerialName("population")
    val population: Long,
    @SerialName("area")
    val area: Double? = null,
    @SerialName("flags")
    val flags: CountryFlags,
    @SerialName("languages")
    val languages: Map<String, String>? = null,
    @SerialName("currencies")
    val currencies: Map<String, Currency>? = null,
    @SerialName("timezones")
    val timezones: List<String>? = null,
    @SerialName("borders")
    val borders: List<String>? = null
)

@Serializable
data class CountryName(
    @SerialName("common")
    val common: String,
    @SerialName("official")
    val official: String
)

@Serializable
data class CountryFlags(
    @SerialName("png")
    val png: String,
    @SerialName("svg")
    val svg: String,
    @SerialName("alt")
    val alt: String? = null
)

@Serializable
data class Currency(
    @SerialName("name")
    val name: String,
    @SerialName("symbol")
    val symbol: String? = null
)