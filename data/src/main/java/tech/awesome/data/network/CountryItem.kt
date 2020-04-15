package tech.awesome.data.network

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CountryItem (
    val name: String,
    val iso2: String?,
    val iso3: String?
)