package tech.awesome.domain.network

import tech.awesome.utils.Either
import tech.awesome.data.network.Confirmed
import tech.awesome.data.network.Countries
import tech.awesome.data.network.Daily
import tech.awesome.data.network.DailySummary
import tech.awesome.data.network.Overview

interface Repository {
    suspend fun overview(): Either<Throwable, Overview>

    suspend fun overviewCountry(country: String): Either<Throwable, Overview>

    suspend fun confirmed(): Either<Throwable, List<Confirmed>>

    suspend fun confirmedCountry(country: String): Either<Throwable, List<Confirmed>>

    suspend fun dailySummary(): Either<Throwable, List<DailySummary>>

    suspend fun daily(date: String): Either<Throwable, List<Daily>>

    suspend fun countries(): Either<Throwable, Countries>
}
