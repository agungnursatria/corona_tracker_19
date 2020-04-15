package tech.awesome.data.network

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class DailySummary(
    val totalConfirmed: Int?,
    val mainlandChina: Int?,
    val otherLocations: Int?,
    val deltaConfirmed: Int?,
    val totalRecovered: Int?,
    val deltaRecovered: Int?,
    val confirmed: DailySummarySub?,
    val deaths: DailySummarySub?,
    val recovered: DailySummarySub?,
    val reportDate: String?
) : Parcelable