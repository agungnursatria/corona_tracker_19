package tech.awesome.domain.local

import tech.awesome.data.local.CoronaDB
import tech.awesome.data.local.entity.Country
import tech.awesome.utils.Either
import tech.awesome.utils.value

class IDBRepository(private val db: CoronaDB) : DBRepository {
    override suspend fun getCountry(): Either<Throwable, List<Country>> {
        return try {
            val result = db.countryDao().get()
            value(result)
        } catch (e: Throwable) {
            error(e)
        }
    }

    override suspend fun getCountry(name: String): Either<Throwable, List<Country>> {
        return try {
            val result = db.countryDao().get(name)
            value(result)
        } catch (e: Throwable) {
            error(e)
        }
    }

    override suspend fun insertCountry(country: Country): Either<Throwable, Boolean> {
        return try {
            db.countryDao().insert(country)
            value(true)
        } catch (e: Throwable) {
            error(e)
        }
    }

    override suspend fun updateCountry(country: Country): Either<Throwable, Boolean> {
        return try {
            db.countryDao().update(country)
            value(true)
        } catch (e: Throwable) {
            error(e)
        }
    }

    override suspend fun deleteCountry(name: String): Either<Throwable, Boolean> {
        return try {
            db.countryDao().delete(name)
            value(true)
        } catch (e: Throwable) {
            error(e)
        }
    }
}