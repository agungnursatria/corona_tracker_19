package tech.awesome.data.network

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class Detail(
    val confirmed: Int?,
    val countryRegion: String?,
    val deaths: Int?,
    val lastUpdate: Long?,
    val lat: Double?,
    val long: Double?,
    val provinceState: String? = null,
    val recovered: Int?
) : Parcelable {
    val locationName get() = countryRegion + if (!provinceState.isNullOrEmpty()) ", $provinceState" else ""
    val compositeKey get() = countryRegion + provinceState
}