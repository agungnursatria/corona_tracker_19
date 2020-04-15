package tech.awesome.coronatrack.di

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import tech.awesome.data.local.CoronaDB
import tech.awesome.domain.local.DBRepository
import tech.awesome.domain.local.IDBRepository
import tech.awesome.domain.pref.AppPref
import tech.awesome.domain.pref.IAppPref

val persistenceModule = module {
    single {
        CoronaDB.getDB(androidContext())
    }

    single {
        IAppPref() as AppPref
    }

    single {
        IDBRepository(get()) as DBRepository
    }

}

