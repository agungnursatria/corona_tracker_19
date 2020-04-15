package tech.awesome.coronatrack.di

import org.koin.dsl.module
import tech.awesome.domain.network.Repository
import tech.awesome.domain.network.IRepository

val repositoryModule = module {
    single { IRepository(get()) as Repository }
}