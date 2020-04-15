package tech.awesome.domain.local

import tech.awesome.data.local.entity.Country
import tech.awesome.utils.Either

interface DBRepository {
    suspend fun getCountry() : Either<Throwable, List<Country>>
    suspend fun getCountry(name: String) : Either<Throwable, List<Country>>
    suspend fun insertCountry(country: Country) : Either<Throwable, Boolean>
    suspend fun updateCountry(country: Country) : Either<Throwable, Boolean>
    suspend fun deleteCountry(name: String) : Either<Throwable, Boolean>
}