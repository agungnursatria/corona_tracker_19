package tech.awesome.coronatrack.ui.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import tech.awesome.coronatrack.ui.base.BindingViewModel
import tech.awesome.data.network.Confirmed
import tech.awesome.data.network.Countries
import tech.awesome.domain.local.DBRepository
import tech.awesome.domain.network.Repository
import tech.awesome.utils.Either
import tech.awesome.utils.State
import tech.awesome.utils.extension.getError

class MapsViewModel(
    private val repository: Repository,
    private val dbRepository: DBRepository
) : BindingViewModel() {

    private val _confirmedState = MutableLiveData<State<String, List<Confirmed>>>()
    val confirmedState: LiveData<State<String, List<Confirmed>>> get() = _confirmedState

    private val _countryItemState = MutableLiveData<State<String, Countries>>()
    val countryState: LiveData<State<String, Countries>> get() = _countryItemState

    fun getConfirmed() {
        viewModelScope.launch {
            _confirmedState.postValue(State.Loading)
            when (val result = repository.confirmed()) {
                is Either.Success -> _confirmedState.postValue(State.Success(result.value))
                is Either.Failure -> _confirmedState.postValue(State.Error(result.error.getError()))
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