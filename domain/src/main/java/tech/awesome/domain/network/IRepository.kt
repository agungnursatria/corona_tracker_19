package tech.awesome.domain.network

import tech.awesome.network.Api
import tech.awesome.utils.Either
import tech.awesome.utils.runService
import tech.awesome.data.network.Confirmed
import tech.awesome.data.network.Countries
import tech.awesome.data.network.Daily
import tech.awesome.data.network.DailySummary
import tech.awesome.data.network.Overview


class IRepository(private val api: Api) :
    Repository {
    override suspend fun overview(): Either<Throwable, Overview> = runService(api.overview())

    override suspend fun overviewCountry(country: String): Either<Throwable, Overview> =
        runService(api.overviewCountry(country))

    override suspend fun confirmed(): Either<Throwable, List<Confirmed>> =
        runService(api.confirmed())

    override suspend fun confirmedCountry(country: String): Either<Throwable, List<Confirmed>> =
        runService(api.confirmedCountry(country))

    override suspend fun dailySummary(): Either<Throwable, List<DailySummary>> = runService(api.dailySummary())

    override suspend fun daily(date: String): Either<Throwable, List<Daily>> = runService(api.daily(date))

    override suspend fun countries(): Either<Throwable, Countries> = runService(api.countries())
}