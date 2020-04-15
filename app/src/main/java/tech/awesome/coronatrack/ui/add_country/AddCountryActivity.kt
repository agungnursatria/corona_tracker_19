package tech.awesome.coronatrack.ui.add_country

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import tech.awesome.coronatrack.R
import tech.awesome.coronatrack.databinding.ActivityAddCountryBinding
import tech.awesome.coronatrack.ui.add_country.adapter.AddCountryAdapter
import tech.awesome.coronatrack.ui.base.BindingActivity
import tech.awesome.data.local.entity.Country
import tech.awesome.data.network.Confirmed
import tech.awesome.data.network.Countries
import tech.awesome.domain.pref.AppPref
import tech.awesome.utils.Animator
import tech.awesome.utils.State
import tech.awesome.utils.extension.*


class AddCountryActivity : BindingActivity(), AddCountryAdapter.AddCountryListener {
    private val binding: ActivityAddCountryBinding by binding(R.layout.activity_add_country)
    private val vm by viewModel<AddCountryViewModel>()
    private val mAdapter by lazy { AddCountryAdapter(this) }
    private val pref by inject<AppPref>()
    private var countries: Countries? = null

    private var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>? = null

    companion object {
        fun getIntent(context: Context?): Intent =
            Intent(context, AddCountryActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupToolbar(binding.toolbar, true)
        binding.rvRegion.adapter = mAdapter
        initObserver()
        initView()
    }

    private fun initView() {
        vm.getConfirmed()
        vm.getCountries()

        bottomSheetBehavior = BottomSheetBehavior.from(binding.bsAddCountry)
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
        bottomSheetBehavior?.setBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(p0: View, p1: Float) {}

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    mAdapter.setSelected(-1)
                }
            }

        })

        with(binding.etSearch) {
            setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    hideKeyboard()
                }
                false
            }

            addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(mEditable: Editable?) {}
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    mAdapter.setShownData(p0.toString())
                }
            })
        }
    }

    private fun initObserver() {
        observe(vm.confirmedState, ::onUpdateConfirmedState)
        observe(vm.countryState, ::onUpdateCountriesState)
        observe(vm.countryDBState, ::onUpdateCountryDBState)
    }

    private fun onUpdateCountryDBState(state: State<String, Country>) {
        when (state) {
            is State.Loading -> onLoadingRv()
            is State.Error -> {
                onLoadedRv()
                showToast(state.message)
            }
            is State.Success -> {
                onLoadedRv()
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }
    private fun onUpdateCountriesState(state: State<String, Countries>) {
        when (state) {
            is State.Error -> {
                showToast(state.message)
            }
            is State.Success -> {
                countries = state.value
            }
        }
    }

    private fun onUpdateConfirmedState(state: State<String, List<Confirmed>>) {
        when (state) {
            is State.Loading -> onLoadingRv()
            is State.Error -> {
                onLoadedRv()
                showToast(state.message)
            }
            is State.Success -> {
                mAdapter.setData(state.value.toTypedArray())
                binding.etSearch.text.clear()
                onLoadedRv()
            }
        }
    }

    override fun onClickItem(confirmed: Confirmed, index: Int) {
        binding.lastUpdate = confirmed.lastUpdate
        Animator.animateIncrementNumber(binding.tvConfirmedValue, confirmed.confirmed ?: 0)
        Animator.animateIncrementNumber(binding.tvRecoveredValue, confirmed.recovered ?: 0)
        Animator.animateIncrementNumber(binding.tvDeathsValue, confirmed.deaths ?: 0)
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
        mAdapter.setSelected(index)

        binding.btnSelectCountry.setOnClickListener {
            confirmed.detailname?.let { pref.setCountryPrimaryKey(it) }

            val countryName = confirmed.countryRegion
            if (!countryName.isNullOrBlank()) {
                val flag: String? = getCountryFlag(countryName)
                val country = Country(
                    confirmed.detailname ?: countryName,
                    countryName,
                    flag,
                    confirmed.lat,
                    confirmed.long,
                    confirmed.provinceState
                )
                vm.setCountryDB(country)
            }
        }
    }

    private fun getCountryFlag(countryName: String): String? {
        val filteredCountry =
            countries?.countries?.filter { it.name.isContains(countryName) }
                ?: emptyList()
        if (filteredCountry.isNotEmpty())
            return filteredCountry[0].iso2 ?: filteredCountry[0].iso3
        return null
    }


    private fun onLoadingRv() {
        binding.rvRegion.gone()
        binding.pbRv.visible()
    }

    private fun onLoadedRv() {
        binding.rvRegion.visible()
        binding.pbRv.gone()
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        super.onBackPressed()
    }
}
