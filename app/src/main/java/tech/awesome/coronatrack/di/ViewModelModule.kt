package tech.awesome.coronatrack.di

import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import tech.awesome.coronatrack.ui.add_country.AddCountryViewModel
import tech.awesome.coronatrack.ui.daily_updates.DailyUpdatesViewModel
import tech.awesome.coronatrack.ui.main.MainViewModel
import tech.awesome.coronatrack.ui.maps.MapsViewModel

val viewModelModule = module {
    viewModel { MainViewModel(get(), get()) }
    viewModel { DailyUpdatesViewModel(get(), get()) }
    viewModel { AddCountryViewModel(get(), get()) }
    viewModel { MapsViewModel(get(), get()) }
}