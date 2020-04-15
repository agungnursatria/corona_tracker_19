package tech.awesome.coronatrack.ui.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.gms.maps.model.LatLng
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import tech.awesome.coronatrack.R
import tech.awesome.coronatrack.databinding.ActivityMainBinding
import tech.awesome.coronatrack.ui.add_country.AddCountryActivity
import tech.awesome.coronatrack.ui.base.BindingActivity
import tech.awesome.coronatrack.ui.daily_updates.DailyUpdatesActivity
import tech.awesome.coronatrack.ui.maps.MapsActivity
import tech.awesome.coronatrack.ui.maps.fragment.MapFragment
import tech.awesome.coronatrack.ui.webview.WebviewActivity
import tech.awesome.data.local.entity.Country
import tech.awesome.data.network.DailySummary
import tech.awesome.data.network.Overview
import tech.awesome.domain.pref.AppPref
import tech.awesome.utils.Animator
import tech.awesome.utils.IntentRequestConstant
import tech.awesome.utils.State
import tech.awesome.utils.StatusConstant
import tech.awesome.utils.extension.*

class MainActivity : BindingActivity() {

    private val binding: ActivityMainBinding by binding(R.layout.activity_main)
    private val vm by viewModel<MainViewModel>()
    private val pref by inject<AppPref>()
    private lateinit var mapsFragment: MapFragment

    companion object {
        fun getIntent(context: Context): Intent = Intent(context, MainActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initObserver()
        initView()
    }

    private fun initView() {
        val isNightMode = pref.getColorMode()
        if (!isNightMode) {
            binding.ivLogo.setAssetImage(this, R.drawable.logo_dashboard_light)
        } else {
            binding.ivLogo.setAssetImage(this, R.drawable.logo_dashboard)
        }

        binding.tvDailyUpdates.setOnClickListener { showToast("Preparing...") }
        binding.ivBtnChartPie.setOnClickListener { onTapBtnPieChart() }
        binding.ivBtnChartGraph.setOnClickListener { onTapBtnLineChart() }
        binding.ivReportMap.setOnClickListener { startActivity(MapsActivity.getIntent(this)) }
        binding.ivMode.setOnClickListener {
            val isNightMode = pref.getColorMode()
            pref.setColorMode(!isNightMode)
            setColorMode()
        }
        binding.tvDailyUpdates.setOnClickListener {
            startActivity(DailyUpdatesActivity.getIntent(this))
        }
        binding.clCountry.setOnClickListener {
            startActivityForResult(
                AddCountryActivity.getIntent(this),
                IntentRequestConstant.ADD_COUNTRY
            )
        }
        binding.ivCountrySelectedEdit.setOnClickListener {
            startActivityForResult(
                AddCountryActivity.getIntent(this),
                IntentRequestConstant.ADD_COUNTRY
            )
        }
        binding.clTips.setOnClickListener {
            startActivity(
                WebviewActivity.getIntent(
                    this,
                    "https://www.who.int/emergencies/diseases/novel-coronavirus-2019/advice-for-public",
                    "Tips & Trick"
                )
            )
        }
        binding.clSymptoms.setOnClickListener {
            startActivity(
                WebviewActivity.getIntent(
                    this,
                    "https://www.who.int/news-room/q-a-detail/q-a-coronaviruses#:~:text=symptoms",
                    "Check Symptoms"
                )
            )
        }

        vm.getOverview()
        vm.getDaily()
        initCountrySelected()
    }

    private fun setColorMode() {
        val isNightMode = pref.getColorMode()
        if (isNightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        recreate()
    }

    private fun initCountrySelected() {
        val name = pref.getCountryName()

        if (!name.isNullOrBlank()) {
            mapsFragment = MapFragment.newInstance(StatusConstant.CONFIRMED)
            mapsFragment.let {
                supportFragmentManager.beginTransaction()
                    .replace(binding.fCountrySelectedMap.id, it)
                    .commitAllowingStateLoss()
            }

            vm.getCountryDB(name)
        } else {
            binding.clCountrySelected.gone()
        }
    }

    private fun initObserver() {
        observe(vm.overviewState, ::onUpdateOverview)
        observe(vm.overviewCountryState, ::onUpdateOverviewCountry)
        observe(vm.dailySummaryState, ::onUpdateDailySummary)
        observe(vm.countryDBState, ::onUpdateCountryDB)
    }

    private fun onUpdateCountryDB(state: State<String, Country>) {
        when (state) {
            is State.Success -> {
                binding.clCountry.gone()
                binding.clCountrySelected.visible()

                val country = state.value
                vm.getOverviewCountry(country.countryName)
                binding.countryName = country.name

                if (!country.flag.isNullOrBlank()) {
                    binding.countryFlag = country.flag
                }

                val latlng = LatLng(country.latitude ?: 0.0, country.longitude ?: 0.0)
                val mOption = mapsFragment.getMarkerOption(latlng, country.name)
                try {
                    mapsFragment.setFocusMarker(mOption)
                    mapsFragment.setFocus(latlng)
                } catch (e: Throwable) {}

                binding.fCountrySelectedMap.setOnClickListener {
                    startActivity(MapsActivity.getIntentWithFocus(this, latlng))
                }
                binding.tvCountrySelectedMap.setOnClickListener {
                    startActivity(MapsActivity.getIntentWithFocus(this, latlng))
                }
            }
            is State.Error -> {
                binding.clCountry.gone()
                showToast(state.message)
            }
        }
    }

    private fun onUpdateOverview(state: State<String, Overview>) {
        when (state) {
            is State.Loading -> onLoading()
            is State.Success -> {
                if (vm.overviewCountryState.value !is State.Loading) onLoaded()
                binding.overview = state.value
                setPieChart(state.value)
                Animator.animateIncrementNumber(
                    binding.tvReportConfirmedValue,
                    state.value.confirmed.value
                )
                Animator.animateIncrementNumber(
                    binding.tvReportRecoveredValue,
                    state.value.recovered.value
                )
                Animator.animateIncrementNumber(
                    binding.tvReportDeathValue,
                    state.value.deaths.value
                )
            }
            is State.Error -> {
                if (vm.overviewCountryState.value !is State.Loading) onLoaded()
                showToast(state.message)
            }
        }
    }

    private fun onUpdateOverviewCountry(state: State<String, Overview>) {
        when (state) {
            is State.Loading -> onLoading()
            is State.Success -> {
                if (vm.overviewState.value !is State.Loading) onLoaded()
                binding.overviewCountry = state.value
                Animator.animateIncrementNumber(
                    binding.tvCountrySelectedConfirmedValue,
                    state.value.confirmed.value
                )
                Animator.animateIncrementNumber(
                    binding.tvCountrySelectedRecoveredValue,
                    state.value.recovered.value
                )
                Animator.animateIncrementNumber(
                    binding.tvCountrySelectedDeathsValue,
                    state.value.deaths.value
                )
            }
            is State.Error -> {
                if (vm.overviewState.value !is State.Loading) onLoaded()
                showToast(state.message)
            }
        }
    }

    private fun onUpdateDailySummary(state: State<String, List<DailySummary>>) {
        when (state) {
            is State.Loading -> onLoadingDaily()
            is State.Success -> {
                onLoadedDaily()
                setLineChart(state.value)
            }
        }
    }

    private fun setPieChart(overview: Overview) {
        val pieDataSet = PieDataSet(
            listOf(
                PieEntry(overview.confirmed.value.toFloat(), getString(R.string.label_confirmed)),
                PieEntry(overview.recovered.value.toFloat(), getString(R.string.label_recovered)),
                PieEntry(overview.deaths.value.toFloat(), getString(R.string.label_death))
            ), getString(R.string.app_name)
        ).apply {
            colors = listOf(
                ContextCompat.getColor(this@MainActivity, R.color.confirmed),
                ContextCompat.getColor(this@MainActivity, R.color.recovered),
                ContextCompat.getColor(this@MainActivity, R.color.death)
            )
        }
        val pieData = PieData(pieDataSet).apply { setDrawValues(false) }
        with(binding.pcReport) {
            data = pieData
            legend.isEnabled = false
            description = null
            holeRadius = 75f
            setHoleColor(
                ContextCompat.getColor(
                    this@MainActivity,
                    R.color.bg_container
                )
            )
            setDrawEntryLabels(false)
            animateY(1000, com.github.mikephil.charting.animation.Easing.EaseInOutQuart)
            invalidate()
        }
    }

    private fun setLineChart(dailys: List<DailySummary>) {
        val totalConfirmedDataSet = LineDataSet(
            dailys.mapIndexed { index, dailyItem ->
                Entry(
                    index.toFloat(),
                    dailyItem.totalConfirmed?.toFloat() ?: 0F,
                    dailyItem.reportDate?.getFormattedDate()
                )
            }, "Confirmed"
        ).apply {
            setLineChartStyle(this, R.color.confirmed)
        }

        val lineData = LineData(totalConfirmedDataSet)

        with(binding.lcReport) {
            animateX(1500)
            legend.textColor = color(R.color.textColor)

            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.textColor = color(R.color.subtitle)

            axisLeft.textColor = color(R.color.subtitle)

            legend.isEnabled = false
            axisRight.isEnabled = false
            description.isEnabled = false

            axisLeft.enableGridDashedLine(10f, 10f, 2f)
            xAxis.enableGridDashedLine(10f, 10f, 2f)

            var lastDate: String? = null
            val dates = dailys.map { it.reportDate.getMonth() }
            xAxis.valueFormatter = object : IndexAxisValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    if (lastDate == dates[value.toInt()]) return ""
                    lastDate = dates[value.toInt()]
                    return dates[value.toInt()]
                }
            }

            axisLeft.valueFormatter = object : IndexAxisValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    val valueInt = value.toInt()
                    if (valueInt >= 1000) {
                        return "${valueInt / 1000}K"
                    }
                    return valueInt.toString()
                }
            }

            data = lineData
        }

    }

    private fun setLineChartStyle(lineDataSet: LineDataSet, @ColorRes colorResId: Int) {
        with(lineDataSet) {
            color = color(colorResId)
            lineWidth = 2f
            circleRadius = 1f
            setDrawCircleHole(false)
            setCircleColor(color(colorResId))
            mode = LineDataSet.Mode.CUBIC_BEZIER
            valueTextColor = color(R.color.textColor)
            setDrawFilled(true)
            fillColor = color(colorResId)
            fillAlpha = 60
        }
    }

    private fun onTapBtnPieChart() {
        binding.pcReport.visible()
        binding.clConfirmed.visible()
        binding.clRecovered.visible()
        binding.clDeaths.visible()
        binding.lcReport.invisible()

        binding.ivBtnChartGraph.setAssetImage(this, R.drawable.ic_graph_inactive)
        binding.ivBtnChartPie.setAssetImage(this, R.drawable.ic_pie_chart_active)
    }

    private fun onTapBtnLineChart() {
        binding.pcReport.invisible()
        binding.clConfirmed.invisible()
        binding.clRecovered.invisible()
        binding.clDeaths.invisible()
        binding.lcReport.visible()

        binding.ivBtnChartGraph.setAssetImage(this, R.drawable.ic_graph_active)
        binding.ivBtnChartPie.setAssetImage(this, R.drawable.ic_pie_chart_inactive)
    }

    private fun onLoadingDaily() {
        binding.ivBtnChartPie.gone()
        binding.ivBtnChartGraph.gone()
    }

    private fun onLoadedDaily() {
        binding.ivBtnChartPie.visible()
        binding.ivBtnChartGraph.visible()
    }

    private fun onLoading() {
        binding.sflReportChart.startShimmering()
        binding.sflReportLastUpdateDate.startShimmering()
        binding.sflReportConfirmedValue.startShimmering()
        binding.sflReportRecoveredValue.startShimmering()
        binding.sflReportDeathValue.startShimmering()

        binding.sflCountrySelectedChart.startShimmering()
        binding.sflCountrySelectedConfirmedValue.startShimmering()
        binding.sflCountrySelectedRecoveredValue.startShimmering()
        binding.sflCountrySelectedDeathsValue.startShimmering()
        binding.sflCountrySelectedFlag.startShimmering()
        binding.sflCountrySelectedInfo.startShimmering()
        binding.sflCountrySelectedMap.startShimmering()
        binding.sflCountrySelectedName.startShimmering()
        binding.sflCountrySelectedLastUpdateDate.startShimmering()

        binding.pcReport.invisible()
        binding.tvReportLastUpdateDate.invisible()
        binding.tvReportConfirmedValue.invisible()
        binding.tvReportRecoveredValue.invisible()
        binding.tvReportDeathValue.invisible()

        binding.tvCountrySelectedChart.invisible()
        binding.tvCountrySelectedConfirmedValue.invisible()
        binding.tvCountrySelectedRecoveredValue.invisible()
        binding.tvCountrySelectedDeathsValue.invisible()
        binding.ivCountrySelectedFlag.invisible()
        binding.tvCountrySelectedInfo.invisible()
        binding.tvCountrySelectedMap.invisible()
        binding.tvCountrySelectedName.invisible()
        binding.tvCountrySelectedLastUpdateDate.invisible()

    }

    private fun onLoaded() {
        binding.sflReportChart.stopShimmering()
        binding.sflReportLastUpdateDate.stopShimmering()
        binding.sflReportConfirmedValue.stopShimmering()
        binding.sflReportRecoveredValue.stopShimmering()
        binding.sflReportDeathValue.stopShimmering()

        binding.sflCountrySelectedChart.stopShimmering()
        binding.sflCountrySelectedConfirmedValue.stopShimmering()
        binding.sflCountrySelectedRecoveredValue.stopShimmering()
        binding.sflCountrySelectedDeathsValue.stopShimmering()
        binding.sflCountrySelectedFlag.stopShimmering()
        binding.sflCountrySelectedInfo.stopShimmering()
        binding.sflCountrySelectedMap.stopShimmering()
        binding.sflCountrySelectedName.stopShimmering()
        binding.sflCountrySelectedLastUpdateDate.stopShimmering()

        binding.pcReport.visible()
        binding.tvReportLastUpdateDate.visible()
        binding.tvReportConfirmedValue.visible()
        binding.tvReportRecoveredValue.visible()
        binding.tvReportDeathValue.visible()

        binding.ivCountrySelectedFlag.visible()
        binding.tvCountrySelectedConfirmedValue.visible()
        binding.tvCountrySelectedRecoveredValue.visible()
        binding.tvCountrySelectedDeathsValue.visible()
        binding.tvCountrySelectedChart.gone()
        binding.tvCountrySelectedInfo.gone()
        binding.tvCountrySelectedMap.visible()
        binding.tvCountrySelectedName.visible()
        binding.tvCountrySelectedLastUpdateDate.visible()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            IntentRequestConstant.ADD_COUNTRY -> {
                if (resultCode == Activity.RESULT_OK) initCountrySelected()
            }
        }

    }
}
