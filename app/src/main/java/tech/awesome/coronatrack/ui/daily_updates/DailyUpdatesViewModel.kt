package tech.awesome.coronatrack.ui.daily_updates

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import tech.awesome.domain.local.DBRepository
import tech.awesome.domain.network.Repository
import tech.awesome.coronatrack.ui.base.BindingViewModel
import tech.awesome.data.network.Countries
import tech.awesome.data.network.Daily
import tech.awesome.data.network.DailySummary
import tech.awesome.utils.*
import tech.awesome.utils.extension.getError

class DailyUpdatesViewModel(
    private val repository: Repository,
    private val dbRepository: DBRepository
) : BindingViewModel() {

    private val _dailySummaryState = MutableLiveData<State<String, List<DailySummary>>>()
    val dailySummaryState: LiveData<State<String, List<DailySummary>>> get() = _dailySummaryState

    private val _dailyState = MutableLiveData<State<String, List<Daily>>>()
    val dailyState: LiveData<State<String, List<Daily>>> get() = _dailyState

    private val _countryItemState = MutableLiveData<State<String, Countries>>()
    val countryState: LiveData<State<String, Countries>> get() = _countryItemState

    fun getDaily(date: String) {
        viewModelScope.launch {
            _dailyState.postValue(State.Loading)
            when (val result = repository.daily(date)) {
                is Either.Success -> _dailyState.postValue(State.Success(result.value))
                is Either.Failure -> _dailyState.postValue(State.Error(result.error.getError()))
            }
        }
    }

    fun getDailySummary() {
        viewModelScope.launch {
            _dailySummaryState.postValue(State.Loading)
            when (val result = repository.dailySummary()) {
                is Either.Success -> _dailySummaryState.postValue(State.Success(result.value))
                is Either.Failure -> _dailySummaryState.postValue(State.Error(result.error.getError()))
            }
        }
    }

    fun getCountries() {
        viewModelScope.launch {
            _countryItemState.postValue(State.Loading)
            when (val result = repository.countries()) {
                is Either.Success -> _countryItemState.postValue(State.Success(result.value))
                is Either.Failure -> _countryItemState.postValue(State.Error(result.error.getError()))
            }
        }
    }
}