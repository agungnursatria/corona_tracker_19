package tech.awesome.coronatrack.ui.maps.fragment

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import tech.awesome.coronatrack.R
import tech.awesome.coronatrack.databinding.FragmentMapsBinding
import tech.awesome.coronatrack.ui.base.BindingFragment
import tech.awesome.data.network.Confirmed
import tech.awesome.utils.ExtraConstant
import tech.awesome.utils.StatusConstant
import tech.awesome.utils.extension.getColorFixed
import tech.awesome.utils.extension.isDarkMode
import kotlin.math.pow

class MapFragment : BindingFragment(), OnMapReadyCallback {
    private var binding: FragmentMapsBinding? = null
    private lateinit var mMap: GoogleMap
    private var pulseCircle: Circle? = null
    private val mMarkers = mutableListOf<Marker>()
    private var valueAnimator: ValueAnimator? = null
    private val shownStatus by lazy {
        arguments?.getInt(ExtraConstant.EXTRA_STATUS) ?: StatusConstant.CONFIRMED
    }

    companion object {
        fun newInstance(status: Int) = MapFragment().apply {
            arguments = Bundle().apply { putInt(ExtraConstant.EXTRA_STATUS, status) }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = binding(inflater, R.layout.fragment_maps, container)
        return binding?.root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMarkerClickListener {
            val latlng = it.position
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 4f))
            startPulseAnimation(latlng)
            false
        }

        valueAnimator = ValueAnimator.ofFloat(
            0f,
            calculatePulseRadius(mMap.cameraPosition?.zoom ?: 4f).apply { }
        ).apply {
            startDelay = 100
            duration = 800
            interpolator = AccelerateDecelerateInterpolator()
        }

        if (context?.isDarkMode() == true) {
            try {
                mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                        context, R.raw.style_json
                    )
                )
            } catch (e: Resources.NotFoundException) {
                e.printStackTrace()
            }
        }

    }

    private fun startPulseAnimation(latLng: LatLng) {
        valueAnimator?.apply {
            removeAllUpdateListeners()
            removeAllListeners()
            end()
        }

        pulseCircle?.remove()
        pulseCircle = mMap.addCircle(
            CircleOptions().center(
                latLng
            ).radius(0.0).strokeWidth(0f)
        )

        valueAnimator?.addUpdateListener {
            context?.let {
                pulseCircle?.fillColor = when (shownStatus) {
                    StatusConstant.RECOVERED -> it.getColorFixed(R.color.pulse_recovered)
                    StatusConstant.DEATHS -> it.getColorFixed(R.color.pulse_death)
                    else -> it.getColorFixed(R.color.pulse_confirmed)
                }
            }
            pulseCircle?.radius = (valueAnimator?.animatedValue as Float).toDouble()
        }

        valueAnimator?.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                valueAnimator?.startDelay = 100
                valueAnimator?.start()
            }
        })

        valueAnimator?.start()
    }

    private fun calculatePulseRadius(zoomLevel: Float): Float {
        return 2.0.pow(16 - zoomLevel.toDouble()).toFloat() * 30
    }

    fun setMarkers(confirmeds: List<Confirmed>) {
        mMarkers.clear()
        confirmeds.forEach {
            val snippets = when (shownStatus) {
                StatusConstant.RECOVERED -> "Recovered: ${it.recovered ?: 0}"
                StatusConstant.DEATHS -> "Deaths: ${it.deaths ?: 0}"
                else -> "Confirmed: ${it.confirmed ?: 0}"
            }
            val marker = mMap.addMarker(
                getMarkerOption(
                    LatLng(it.lat ?: 0.0, it.long ?: 0.0),
                    it.detailname,
                    snippets
                )
            )
            mMarkers.add(marker)
        }
    }

    fun getMarkerOption(latLng: LatLng, title: String?, snippets: String? = null): MarkerOptions {
        return MarkerOptions().position(latLng)
            .anchor(0.5f, 0.5f)
            .title(title)
            .icon(
                BitmapDescriptorFactory.fromResource(
                    when (shownStatus) {
                        StatusConstant.RECOVERED -> R.drawable.img_recovered_marker
                        StatusConstant.DEATHS -> R.drawable.img_deaths_marker
                        else -> R.drawable.img_confirmed_marker
                    }
                )
            ).apply {
                if (!snippets.isNullOrBlank()) {
                    snippet(snippets)
                }
            }
    }

    fun setFocusMarker(markerOptions: MarkerOptions) {
        mMarkers.clear()
        val marker = mMap.addMarker(markerOptions)
        mMarkers.add(marker)
    }

    fun setFocus(latLng: LatLng) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 4f))
        startPulseAnimation(latLng)
    }
}
