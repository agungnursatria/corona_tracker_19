package tech.awesome.coronatrack.ui.maps

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
import tech.awesome.data.network.*
import tech.awesome.domain.local.DBRepository
import tech.awesome.domain.network.Repository
import tech.awesome.utils.State
import tech.awesome.utils.failure
import tech.awesome.utils.value


@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MapsViewModelTest {

    @get:Rule
    val coroutineRule = CoroutineRule()

    @Mock
    lateinit var repository: Repository

    @Mock
    lateinit var dbrepository: DBRepository

    private lateinit var viewModel: MapsViewModel

    @Mock
    private lateinit var observerConfirmed: Observer<State<String, List<Confirmed>>>

    @Mock
    private lateinit var observerCountries: Observer<State<String, Countries>>


    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        InstantRuleExecution.start()
        viewModel = MapsViewModel(
            repository,
            dbrepository
        ).apply {
            confirmedState.observeForever(observerConfirmed)
            countryState.observeForever(observerCountries)
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

}