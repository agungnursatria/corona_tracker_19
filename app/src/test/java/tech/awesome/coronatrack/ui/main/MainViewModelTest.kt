package tech.awesome.coronatrack.ui.main

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
import tech.awesome.data.network.DailySummary
import tech.awesome.data.network.Overview
import tech.awesome.domain.local.DBRepository
import tech.awesome.domain.network.Repository
import tech.awesome.utils.State
import tech.awesome.utils.failure
import tech.awesome.utils.value


@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    @get:Rule
    val coroutineRule = CoroutineRule()

    @Mock
    lateinit var repository: Repository

    @Mock
    lateinit var dbrepository: DBRepository

    private lateinit var viewModel: MainViewModel

    @Mock
    private lateinit var observerOverview: Observer<State<String, Overview>>

    @Mock
    private lateinit var observerOverviewCountry: Observer<State<String, Overview>>

    @Mock
    private lateinit var observerDailySummary: Observer<State<String, List<DailySummary>>>

    @Mock
    private lateinit var observerCountryDB: Observer<State<String, Country>>

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        InstantRuleExecution.start()
        viewModel = MainViewModel(
            repository,
            dbrepository
        ).apply {
            overviewState.observeForever(observerOverview)
            overviewCountryState.observeForever(observerOverviewCountry)
            dailySummaryState.observeForever(observerDailySummary)
            countryDBState.observeForever(observerCountryDB)
        }
    }

    @After
    fun tearDown() {
        InstantRuleExecution.tearDown()
    }

    @Test
    fun `when get overview data should success`() {
        coroutineRule.runBlockingTest {
            val overview = Overview()
            val success = value(overview)
            given(repository.overview()).willReturn(success)
            viewModel.getOverview()
            verify(repository, atLeastOnce()).overview()
            verify(observerOverview, atLeastOnce()).onChanged(State.Loading)
            verify(observerOverview, atLeastOnce()).onChanged(State.Success(overview))
            verifyNoMoreInteractions(repository, observerOverview)
            clearInvocations(repository, observerOverview)
        }
    }

    @Test
    fun `when get overview data should error`() {
        coroutineRule.runBlockingTest {
            val errorMessage = "No Data"
            val error = failure(Exception(errorMessage))
            given(repository.overview()).willReturn(error)
            viewModel.getOverview()
            verify(repository, atLeastOnce()).overview()
            verify(observerOverview, atLeastOnce()).onChanged(State.Loading)
            verify(observerOverview, atLeastOnce()).onChanged(State.Error(errorMessage))
            verifyNoMoreInteractions(repository, observerOverview)
            clearInvocations(repository, observerOverview)
        }
    }

    @Test
    fun `when get overview country data should success`() {
        coroutineRule.runBlockingTest {
            val countryName = "name"
            val overview = Overview()
            val success = value(overview)
            given(repository.overviewCountry(countryName)).willReturn(success)
            viewModel.getOverviewCountry(countryName)
            verify(repository, atLeastOnce()).overviewCountry(countryName)
            verify(observerOverviewCountry, atLeastOnce()).onChanged(State.Loading)
            verify(observerOverviewCountry, atLeastOnce()).onChanged(State.Success(overview))
            verifyNoMoreInteractions(repository, observerOverviewCountry)
            clearInvocations(repository, observerOverviewCountry)
        }
    }

    @Test
    fun `when get overview country data should error`() {
        coroutineRule.runBlockingTest {
            val countryName = "name"
            val errorMessage = "No Data"
            val error = failure(Exception(errorMessage))
            given(repository.overviewCountry(countryName)).willReturn(error)
            viewModel.getOverviewCountry(countryName)
            verify(repository, atLeastOnce()).overviewCountry(countryName)
            verify(observerOverviewCountry, atLeastOnce()).onChanged(State.Loading)
            verify(observerOverviewCountry, atLeastOnce()).onChanged(State.Error(errorMessage))
            verifyNoMoreInteractions(repository, observerOverviewCountry)
            clearInvocations(repository, observerOverviewCountry)
        }
    }

    @Test
    fun `when get daily summary data should success`() {
        coroutineRule.runBlockingTest {
            val data = emptyList<DailySummary>()
            val success = value(data)
            given(repository.dailySummary()).willReturn(success)
            viewModel.getDaily()
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
            viewModel.getDaily()
            verify(repository, atLeastOnce()).dailySummary()
            verify(observerDailySummary, atLeastOnce()).onChanged(State.Loading)
            verify(observerDailySummary, atLeastOnce()).onChanged(State.Error(errorMessage))
            verifyNoMoreInteractions(repository, observerDailySummary)
            clearInvocations(repository, observerDailySummary)
        }
    }

    @Test
    fun `when get country db data should success`() {
        coroutineRule.runBlockingTest {
            val countryName = "name"
            val data = Country()
            val listData = listOf<Country>(data)
            val success = value(listData)
            given(dbrepository.getCountry(countryName)).willReturn(success)
            viewModel.getCountryDB(countryName)
            verify(dbrepository, atLeastOnce()).getCountry(countryName)
            verify(observerCountryDB, atLeastOnce()).onChanged(State.Loading)
            verify(observerCountryDB, atLeastOnce()).onChanged(State.Success(data))
            verifyNoMoreInteractions(repository, observerCountryDB)
            clearInvocations(repository, observerCountryDB)
        }
    }

    @Test
    fun `when get country db data should error`() {
        coroutineRule.runBlockingTest {
            val countryName = "name"
            val errorMessage = "No Data"
            val error = failure(Exception(errorMessage))
            given(dbrepository.getCountry(countryName)).willReturn(error)
            viewModel.getCountryDB(countryName)
            verify(dbrepository, atLeastOnce()).getCountry(countryName)
            verify(observerCountryDB, atLeastOnce()).onChanged(State.Loading)
            verify(observerCountryDB, atLeastOnce()).onChanged(State.Error(errorMessage))
            verifyNoMoreInteractions(repository, observerCountryDB)
            clearInvocations(repository, observerCountryDB)
        }
    }

}