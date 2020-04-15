package tech.awesome.coronatrack.ui.add_country

import androidx.lifecycle.Observer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import tech.awesome.coronatrack.CoroutineRule
import tech.awesome.coronatrack.InstantRuleExecution
import tech.awesome.coronatrack.runBlockingTest
import tech.awesome.data.local.entity.Country
import tech.awesome.data.network.Confirmed
import tech.awesome.data.network.Countries
import tech.awesome.data.network.CountryItem
import tech.awesome.domain.local.DBRepository
import tech.awesome.domain.network.Repository
import tech.awesome.utils.State
import tech.awesome.utils.failure
import tech.awesome.utils.value


@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class AddCountryViewModelTest {

    @get:Rule
    val coroutineRule = CoroutineRule()

    @Mock
    lateinit var repository: Repository

    @Mock
    lateinit var dbrepository: DBRepository

    private lateinit var viewModel: AddCountryViewModel

    @Mock
    private lateinit var observerConfirmed: Observer<State<String, List<Confirmed>>>

    @Mock
    private lateinit var observerCountries: Observer<State<String, Countries>>

    @Mock
    private lateinit var observerCountryDB: Observer<State<String, Country>>

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        InstantRuleExecution.start()
        viewModel = AddCountryViewModel(
            repository,
            dbrepository
        ).apply {
            confirmedState.observeForever(observerConfirmed)
            countryState.observeForever(observerCountries)
            countryDBState.observeForever(observerCountryDB)
        }
    }

    @After
    fun tearDown() {
        InstantRuleExecution.tearDown()
    }

    @Test
    fun `when get confirmed data should success`() {
        coroutineRule.runBlockingTest {
            val data = emptyList<Confirmed>()
            val success = value(data)
            given(repository.confirmed()).willReturn(success)
            viewModel.getConfirmed()
            verify(repository, atLeastOnce()).confirmed()
            verify(observerConfirmed, atLeastOnce()).onChanged(State.Loading)
            verify(observerConfirmed, atLeastOnce()).onChanged(State.Success(data))
            verifyNoMoreInteractions(repository, observerConfirmed)
            clearInvocations(repository, observerConfirmed)
        }
    }

    @Test
    fun `when get confirmed data should error`() {
        coroutineRule.runBlockingTest {
            val errorMessage = "No Data"
            val error = failure(Exception(errorMessage))
            given(repository.confirmed()).willReturn(error)
            viewModel.getConfirmed()
            verify(repository, atLeastOnce()).confirmed()
            verify(observerConfirmed, atLeastOnce()).onChanged(State.Loading)
            verify(observerConfirmed, atLeastOnce()).onChanged(State.Error(errorMessage))
            verifyNoMoreInteractions(repository, observerConfirmed)
            clearInvocations(repository, observerConfirmed)
        }
    }

    @Test
    fun `when get countries data should success`() {
        coroutineRule.runBlockingTest {
            val countries = emptyList<CountryItem>()
            val data = Countries(countries)
            val success = value(data)
            given(repository.countries()).willReturn(success)
            viewModel.getCountries()
            verify(repository, atLeastOnce()).countries()
            verify(observerCountries, atLeastOnce()).onChanged(State.Loading)
            verify(observerCountries, atLeastOnce()).onChanged(State.Success(data))
            verifyNoMoreInteractions(repository, observerCountries)
            clearInvocations(repository, observerCountries)
        }
    }

    @Test
    fun `when get countries data should error`() {
        coroutineRule.runBlockingTest {
            val errorMessage = "No Data"
            val error = failure(Exception(errorMessage))
            given(repository.countries()).willReturn(error)
            viewModel.getCountries()
            verify(repository, atLeastOnce()).countries()
            verify(observerCountries, atLeastOnce()).onChanged(State.Loading)
            verify(observerCountries, atLeastOnce()).onChanged(State.Error(errorMessage))
            verifyNoMoreInteractions(repository, observerCountries)
            clearInvocations(repository, observerCountries)
        }
    }

    @Test
    fun `when set country db data should success`() {
        coroutineRule.runBlockingTest {
            val data = Country()
            val success = value(true)
            given(dbrepository.insertCountry(data)).willReturn(success)
            viewModel.setCountryDB(data)
            verify(dbrepository, atLeastOnce()).insertCountry(data)
            verify(observerCountryDB, atLeastOnce()).onChanged(State.Loading)
            verify(observerCountryDB, atLeastOnce()).onChanged(State.Success(data))
            verifyNoMoreInteractions(repository, observerCountryDB)
            clearInvocations(repository, observerCountryDB)
        }
    }

    @Test
    fun `when set country db data should error`() {
        coroutineRule.runBlockingTest {
            val data = Country()
            val errorMessage = "No Data"
            val error = failure(Exception(errorMessage))
            given(dbrepository.insertCountry(data)).willReturn(error)
            viewModel.setCountryDB(data)
            verify(dbrepository, atLeastOnce()).insertCountry(data)
            verify(observerCountryDB, atLeastOnce()).onChanged(State.Loading)
            verify(observerCountryDB, atLeastOnce()).onChanged(State.Error(errorMessage))
            verifyNoMoreInteractions(repository, observerCountryDB)
            clearInvocations(repository, observerCountryDB)
        }
    }
}