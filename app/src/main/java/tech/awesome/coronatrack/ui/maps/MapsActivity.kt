package tech.awesome.coronatrack.ui.maps

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.android.viewmodel.ext.android.viewModel
import tech.awesome.coronatrack.R
import tech.awesome.coronatrack.databinding.ActivityMapsBinding
import tech.awesome.coronatrack.ui.base.BindingActivity
import tech.awesome.coronatrack.ui.maps.fragment.MapFragment
import tech.awesome.coronatrack.ui.maps.fragment.adapter.MapsAdapter
import tech.awesome.data.network.Confirmed
import tech.awesome.data.network.Countries
import tech.awesome.utils.ExtraConstant
import tech.awesome.utils.State
import tech.awesome.utils.StatusConstant
import tech.awesome.utils.extension.*

class MapsActivity : BindingActivity(), MapsAdapter.MapsListener {

    companion object {
        fun getIntent(context: Context?): Intent =
            Intent(context, MapsActivity::class.java)

        fun getIntentWithFocus(context: Context?, latLng: LatLng) : Intent = getIntent(context).apply {
            putExtra(ExtraConstant.EXTRA_LATLNG, latLng)
        }
    }

    private val binding: ActivityMapsBinding by binding(R.layout.activity_maps)
    private val vm by viewModel<MapsViewModel>()
    private val mAdapter by lazy { MapsAdapter(this, StatusConstant.CONFIRMED) }
    private var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>? = null
    private lateinit var mapsFragment: MapFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.rv.adapter = mAdapter
        binding.ivBack.setOnClickListener { onBackPressed() }
        initObserver()
        initViews()
    }

    private fun initObserver() {
        observe(vm.confirmedState, ::onUpdateConfirmedState)
        observe(vm.countryState, ::onUpdateCountriesState)

    }

    private fun onUpdateCountriesState(state: State<String, Countries>) {
        when (state) {
            is State.Loading -> onLoadingRv()
            is State.Error -> {
                onLoadedRv()
                showToast(state.message)
            }
            is State.Success -> {
                mAdapter.setCountries(state.value)
                onLoadedRv()
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
                mapsFragment.setMarkers(state.value)
                binding.etSearch.text.clear()

                val latlng = intent.getParcelableExtra<LatLng>(ExtraConstant.EXTRA_LATLNG)
                if (latlng != null) {
                    mapsFragment.setFocus(latlng)
                }
                onLoadedRv()
            }
        }
    }


    private fun onLoadingRv() {
        binding.rlPb.visible()
    }

    private fun onLoadedRv() {
        binding.rlPb.gone()
    }

    private fun initViews() {
        mapsFragment = MapFragment.newInstance(StatusConstant.CONFIRMED)
        mapsFragment.let {
            supportFragmentManager.beginTransaction().replace(binding.fMap.id, it)
                .commitAllowingStateLoss()
        }

        bottomSheetBehavior = BottomSheetBehavior.from(binding.bs)
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehavior?.setBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(p0: View, p1: Float) {}

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
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

        vm.getCountries()
        vm.getConfirmed()
    }


    override fun onClickItem(confirmed: Confirmed, index: Int) {
        hideKeyboard()
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        val latlng = LatLng(confirmed.lat ?: 0.0, confirmed.long ?: 0.0)
        mapsFragment.setFocus(latlng)
    }
}
