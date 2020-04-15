package tech.awesome.coronatrack.ui.daily_updates

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_daily_updates.*
import org.koin.android.viewmodel.ext.android.viewModel
import tech.awesome.coronatrack.R
import tech.awesome.coronatrack.databinding.ActivityDailyUpdatesBinding
import tech.awesome.coronatrack.ui.base.BindingActivity
import tech.awesome.coronatrack.ui.daily_updates.adapter.DailyUpdatesAdapter
import tech.awesome.data.network.Countries
import tech.awesome.data.network.Daily
import tech.awesome.data.network.DailySummary
import tech.awesome.utils.extension.*
import tech.awesome.utils.*


class DailyUpdatesActivity : BindingActivity() {
    private val binding: ActivityDailyUpdatesBinding by binding(R.layout.activity_daily_updates)
    private val vm by viewModel<DailyUpdatesViewModel>()
    private val dailyAdapter by lazy { DailyUpdatesAdapter(StatusConstant.CONFIRMED) }

    companion object {
        fun getIntent(context: Context?): Intent =
            Intent(context, DailyUpdatesActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupToolbar(binding.toolbar, true)
        binding.shownIndex = dailyAdapter.getStatus()
        binding.rvDaily.adapter = dailyAdapter
        initObserver()
        initView()
    }

    private fun initView() {
        vm.getDailySummary()
        vm.getCountries()

        val layoutManager = LinearLayoutManager(this)
        binding.rvDaily.layoutManager = layoutManager
        binding.rvDaily.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (binding.rvDaily.isVisible && !dailyAdapter.isLastPage) {
                    if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                        Handler().postDelayed({ dailyAdapter.loadMore() }, 1000)
                    }
                }
            }
        })


        binding.srl.setOnRefreshListener {
            vm.getDailySummary()
            vm.getCountries()
        }
        btn_confirmed.setOnClickListener {
            dailyAdapter.setStatus(StatusConstant.CONFIRMED)
            binding.shownIndex = StatusConstant.CONFIRMED
        }
        btn_recovered.setOnClickListener {
            dailyAdapter.setStatus(StatusConstant.RECOVERED)
            binding.shownIndex = StatusConstant.RECOVERED
        }
        btn_death.setOnClickListener {
            dailyAdapter.setStatus(StatusConstant.DEATHS)
            binding.shownIndex = StatusConstant.DEATHS
        }
    }

    private fun initObserver() {
        observe(vm.dailySummaryState, ::onUpdateDailySummary)
        observe(vm.countryState, ::onUpdateCountries)
        observe(vm.dailyState, ::onUpdateDaily)
    }


    private fun onUpdateCountries(state: State<String, Countries>) {
        when (state) {
            is State.Loading -> onLoadingRv()
            is State.Error -> {
                onLoadedRv()
                showToast(state.message)
            }
            is State.Success -> {
                binding.countries = state.value
                dailyAdapter.setCountries(state.value)
            }
        }
    }

    private fun onUpdateDaily(state: State<String, List<Daily>>) {
        when (state) {
            is State.Error -> {
                onLoadedRv()
                showToast(state.message)
            }
            is State.Success -> {
                dailyAdapter.setDailys(state.value.toTypedArray())
                onLoadedRv()
            }
        }
    }

    private fun onUpdateDailySummary(state: State<String, List<DailySummary>>) {
        when (state) {
            is State.Loading -> onLoadingSummary()
            is State.Success -> {
                onLoadedSummary()
                if (state.value.isNotEmpty()) {
                    val lastIndex = state.value.lastIndex
                    val beforeLastIndex = state.value.lastIndex - 1

                    val totalConfirmedLastIndex = state.value[lastIndex].totalConfirmed ?: 0
                    val totalConfirmedBeforeLastIndex =
                        if (beforeLastIndex < 0) 0
                        else state.value[beforeLastIndex].totalConfirmed ?: 0
                    val dailyConfirmed = totalConfirmedLastIndex - totalConfirmedBeforeLastIndex

                    val totalRecoveredLastIndex = state.value[lastIndex].totalRecovered ?: 0
                    val totalRecoveredBeforeLastIndex =
                        if (beforeLastIndex < 0) 0
                        else state.value[beforeLastIndex].totalRecovered ?: 0
                    val dailyRecovered = totalRecoveredLastIndex - totalRecoveredBeforeLastIndex

                    val totalDeathLastIndex = state.value[lastIndex].deaths?.total ?: 0
                    val totalDeathBeforeLastIndex =
                        if (beforeLastIndex < 0) 0
                        else state.value[beforeLastIndex].deaths?.total ?: 0
                    val dailyDeath = totalDeathLastIndex - totalDeathBeforeLastIndex

                    binding.confirmed = dailyConfirmed
                    binding.recovered = dailyRecovered
                    binding.deaths = dailyDeath
                    state.value[lastIndex].reportDate?.run {
                        binding.lastUpdate = this
                        vm.getDaily(getDailyPathDate())
                    }
                } else {
                    binding.confirmed = 0
                    binding.recovered = 0
                    binding.deaths = 0
                }
            }
            is State.Error -> {
                onLoadedSummary()
                showToast(state.message)
            }
        }
    }

    private fun onLoadingRv() {
        binding.rvDaily.gone()
        binding.pbRv.visible()
    }

    private fun onLoadedRv() {
        binding.rvDaily.visible()
        binding.pbRv.gone()
    }

    private fun onLoadingSummary() {
        binding.tvConfirmedValue.invisible()
        binding.tvRecoveredValue.invisible()
        binding.tvDeathsValue.invisible()
        binding.tvLastUpdate.invisible()
        binding.sflConfirmedValue.startShimmering()
        binding.sflRecoveredValue.startShimmering()
        binding.sflDeathsValue.startShimmering()
        binding.sflLastUpdate.startShimmering()
    }

    private fun onLoadedSummary() {
        binding.tvConfirmedValue.visible()
        binding.tvRecoveredValue.visible()
        binding.tvDeathsValue.visible()
        binding.tvLastUpdate.visible()
        binding.sflConfirmedValue.stopShimmering()
        binding.sflRecoveredValue.stopShimmering()
        binding.sflDeathsValue.stopShimmering()
        binding.sflLastUpdate.stopShimmering()
        binding.srl.isRefreshing = false
    }
}
