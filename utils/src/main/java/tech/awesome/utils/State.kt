package tech.awesome.utils

//import tech.awesome.data.db.Country
//import tech.awesome.data.network.Countries
//import tech.awesome.data.network.CountryItem
//import tech.awesome.data.network.Daily
//import tech.awesome.data.network.DailySummary
//import tech.awesome.data.network.Overview

sealed class State<out E, out V> {
    data class Error<out E>(val message: E) : State<E, Nothing>()
    data class Success<out V>(val value: V) : State<Nothing, V>()
    object Loading : State<Nothing, Nothing>()
}

//typealias OverviewState = State<String, Overview>
//typealias CountryItemState = State<String, CountryItem>
//typealias CountriesState = State<String, Countries>
//typealias CountryState = State<String, Country>
//typealias DailySummaryState = State<String, List<DailySummary>>
//typealias DailyState = State<String, List<Daily>>