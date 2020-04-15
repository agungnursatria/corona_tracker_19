package tech.awesome.data.network

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Countries (
    val countries: List<CountryItem>?
)