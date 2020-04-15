package tech.awesome.data.network

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class Daily(
    val provinceState: String?,
    val countryRegion: String?,
    val lastUpdate: String?,
    val lat: String?,
    val long: String?,
    val confirmed: Int = 0,
    val recovered: Int = 0,
    val deaths: Int = 0,
    val active: Int?,
    val iso2: String?,
    val iso3: String?
) : Parcelable {
    val location get() = if (provinceState != null) "$provinceState, $countryRegion" else countryRegion
}