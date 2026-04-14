package com.example.hm_third_count.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiCountriesCountry(
    @SerialName("name")
    val name: String,
    @SerialName("alpha2Code")
    val alpha2Code: String,
    @SerialName("alpha3Code")
    val alpha3Code: String,
    @SerialName("capital")
    val capital: String? = null,
    @SerialName("region")
    val region: String,
    @SerialName("subregion")
    val subregion: String? = null,
    @SerialName("population")
    val population: Long,
    @SerialName("area")
    val area: Double? = null,
    @SerialName("flags")
    val flags: ApiCountriesFlags,
    @SerialName("languages")
    val languages: List<ApiCountriesLanguage>? = null,
    @SerialName("currencies")
    val currencies: List<ApiCountriesCurrency>? = null,
    @SerialName("timezones")
    val timezones: List<String>? = null,
    @SerialName("borders")
    val borders: List<String>? = null
)

@Serializable
data class ApiCountriesFlags(
    @SerialName("png")
    val png: String,
    @SerialName("svg")
    val svg: String
)

@Serializable
data class ApiCountriesLanguage(
    @SerialName("name")
    val name: String,
    @SerialName("nativeName")
    val nativeName: String? = null
)

@Serializable
data class ApiCountriesCurrency(
    @SerialName("code")
    val code: String,
    @SerialName("name")
    val name: String,
    @SerialName("symbol")
    val symbol: String? = null
)

fun ApiCountriesCountry.toCountry(): Country {
    return Country(
        name = CountryName(
            common = this.name,
            official = this.name
        ),
        code = this.alpha3Code,
        capital = this.capital?.let { listOf(it) },
        region = this.region,
        subregion = this.subregion,
        population = this.population,
        area = this.area,
        flags = CountryFlags(
            png = this.flags.png,
            svg = this.flags.svg
        ),
        languages = this.languages?.associate { 
            it.name.lowercase().take(3) to it.name 
        },
        currencies = this.currencies?.associate { 
            it.code to Currency(name = it.name, symbol = it.symbol) 
        },
        timezones = this.timezones,
        borders = this.borders
    )
}