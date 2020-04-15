package tech.awesome.data.network

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class Overview(
    val confirmed: OverviewItem = OverviewItem(
        "",
        0
    ),
    val recovered: OverviewItem = OverviewItem(
        "",
        0
    ),
    val deaths: OverviewItem = OverviewItem(
        "",
        0
    ),
    val lastUpdate: String? = null
) : Parcelable