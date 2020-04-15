package tech.awesome.network

import retrofit2.http.GET
import retrofit2.http.Path
import tech.awesome.data.network.Confirmed
import tech.awesome.data.network.Countries
import tech.awesome.data.network.Daily
import tech.awesome.data.network.DailySummary
import tech.awesome.data.network.Overview

interface Api {
    @GET("api")
    suspend fun overview(): Overview

    @GET("api/countries/{country}")
    suspend fun overviewCountry(@Path("country") country: String): Overview

    @GET("api/confirmed")
    suspend fun confirmed(): List<Confirmed>

    @GET("api/countries/{country}/confirmed")
    suspend fun confirmedCountry(@Path("country") country: String): List<Confirmed>

    @GET("api/daily")
    suspend fun dailySummary(): List<DailySummary>

    @GET("api/daily/{date}")
    suspend fun daily(@Path("date") date: String): List<Daily>

    @GET("api/countries")
    suspend fun countries(): Countries
}