package tech.awesome.data.network

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class Confirmed(
    val provinceState: String?,
    val countryRegion: String?,
    val lastUpdate: Long?,
    val lat: Double?,
    val long: Double?,
    val confirmed: Int?,
    val recovered: Int?,
    val deaths: Int?,
    val active: Int?,
    val admin2: String?,
    val iso2: String?,
    val iso3: String?
) : Parcelable {
    val detailname get() = if (provinceState != null) "$provinceState, $countryRegion" else countryRegion
    var flag : String? = null
}