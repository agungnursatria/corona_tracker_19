package tech.awesome.coronatrack.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import tech.awesome.domain.local.DBRepository
import tech.awesome.domain.network.Repository
import tech.awesome.coronatrack.ui.base.BindingViewModel
import tech.awesome.data.local.entity.Country
import tech.awesome.data.network.DailySummary
import tech.awesome.data.network.Overview
import tech.awesome.utils.*
import tech.awesome.utils.extension.getError

class MainViewModel(private val repository: Repository, private val dbRepository: DBRepository) :
    BindingViewModel() {
    private val _overviewState = MutableLiveData<State<String, Overview>>()
    val overviewState: LiveData<State<String, Overview>> get() = _overviewState

    private val _dailyState = MutableLiveData<State<String, List<DailySummary>>>()
    val dailySummaryState: LiveData<State<String, List<DailySummary>>> get() = _dailyState

    private val _overviewCountryState = MutableLiveData<State<String, Overview>>()
    val overviewCountryState: LiveData<State<String, Overview>> get() = _overviewCountryState

    private val _countryDBState = MutableLiveData<State<String, Country>>()
    val countryDBState: LiveData<State<String, Country>> get() = _countryDBState


    fun getOverview() {
        viewModelScope.launch {
            _overviewState.postValue(State.Loading)
            when (val result = repository.overview()) {
                is Either.Success -> _overviewState.postValue(State.Success(result.value))
                is Either.Failure -> _overviewState.postValue(State.Error(result.error.getError()))
            }
        }
    }

    fun getOverviewCountry(countryName: String) {
        if (countryName.isBlank()) return
        viewModelScope.launch {
            _overviewCountryState.postValue(State.Loading)
            when (val result = repository.overviewCountry(countryName)) {
                is Either.Success -> _overviewCountryState.postValue(State.Success(result.value))
                is Either.Failure -> _overviewCountryState.postValue(State.Error(result.error.getError()))
            }
        }
    }

    fun getDaily() {
        viewModelScope.launch {
            _dailyState.postValue(State.Loading)
            when (val result = repository.dailySummary()) {
                is Either.Success -> _dailyState.postValue(State.Success(result.value))
                is Either.Failure -> _dailyState.postValue(State.Error(result.error.getError()))
            }
        }
    }

    fun getCountryDB(countryName: String) {
        viewModelScope.launch {
            _countryDBState.postValue(State.Loading)
            when (val result = dbRepository.getCountry(countryName)) {
                is Either.Success -> {
                    if (result.value.isEmpty()) _countryDBState.postValue(State.Error("Failed to retrieve country in db"))
                    else _countryDBState.postValue(State.Success(result.value[0]))
                }
                is Either.Failure -> _countryDBState.postValue(State.Error(result.error.getError()))
            }
        }
    }
}