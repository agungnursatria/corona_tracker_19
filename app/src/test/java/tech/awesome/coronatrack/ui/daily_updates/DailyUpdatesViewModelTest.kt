package tech.awesome.coronatrack.ui.daily_updates

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
import tech.awesome.data.network.*
import tech.awesome.domain.local.DBRepository
import tech.awesome.domain.network.Repository
import tech.awesome.utils.State
import tech.awesome.utils.failure
import tech.awesome.utils.value


@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class DailyUpdatesViewModelTest {

    @get:Rule
    val coroutineRule = CoroutineRule()

    @Mock
    lateinit var repository: Repository

    @Mock
    lateinit var dbrepository: DBRepository

    private lateinit var viewModel: DailyUpdatesViewModel

    @Mock
    private lateinit var observerDaily: Observer<State<String, List<Daily>>>

    @Mock
    private lateinit var observerDailySummary: Observer<State<String, List<DailySummary>>>

    @Mock
    private lateinit var observerCountries: Observer<State<String, Countries>>


    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        InstantRuleExecution.start()
        viewModel = DailyUpdatesViewModel(
            repository,
            dbrepository
        ).apply {
            dailyState.observeForever(observerDaily)
            dailySummaryState.observeForever(observerDailySummary)
            countryState.observeForever(observerCountries)
        }
    }

    @After
    fun tearDown() {
        InstantRuleExecution.tearDown()
    }

    @Test
    fun `when get daily data should success`() {
        coroutineRule.runBlockingTest {
            val date = ""
            val data = emptyList<Daily>()
            val success = value(data)
            given(repository.daily(date)).willReturn(success)
            viewModel.getDaily(date)
            verify(repository, atLeastOnce()).daily(date)
            verify(observerDaily, atLeastOnce()).onChanged(State.Loading)
            verify(observerDaily, atLeastOnce()).onChanged(State.Success(data))
            verifyNoMoreInteractions(repository, observerDaily)
            clearInvocations(repository, observerDaily)
        }
    }

    @Test
    fun `when get daily data should error`() {
        coroutineRule.runBlockingTest {
            val date = ""
            val errorMessage = "No Data"
            val error = failure(Exception(errorMessage))
            given(repository.daily(date)).willReturn(error)
            viewModel.getDaily(date)
            verify(repository, atLeastOnce()).daily(date)
            verify(observerDaily, atLeastOnce()).onChanged(State.Loading)
            verify(observerDaily, atLeastOnce()).onChanged(State.Error(errorMessage))
            verifyNoMoreInteractions(repository, observerDaily)
            clearInvocations(repository, observerDaily)
        }
    }


    @Test
    fun `when get daily summary data should success`() {
        coroutineRule.runBlockingTest {
            val data = emptyList<DailySummary>()
            val success = value(data)
            given(repository.dailySummary()).willReturn(success)
            viewModel.getDailySummary()
            verify(repository, atLeastOnce()).dailySummary()
            verify(observerDailySummary, atLeastOnce()).onChanged(State.Loading)
            verify(observerDailySummary, atLeastOnce()).onChanged(State.Success(data))
            verifyNoMoreInteractions(repository, observerDailySummary)
            clearInvocations(repository, observerDailySummary)
        }
    }

    @Test
    fun `when get daily summary data should error`() {
        coroutineRule.runBlockingTest {
            val errorMessage = "No Data"
            val error = failure(Exception(errorMessage))
            given(repository.dailySummary()).willReturn(error)
            viewModel.getDailySummary()
            verify(repository, atLeastOnce()).dailySummary()
            verify(observerDailySummary, atLeastOnce()).onChanged(State.Loading)
            verify(observerDailySummary, atLeastOnce()).onChanged(State.Error(errorMessage))
            verifyNoMoreInteractions(repository, observerDailySummary)
            clearInvocations(repository, observerDailySummary)
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