package tech.awesome.data.network

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class DailySummarySub(
    val total: Int?,
    val china: Int?,
    val outsideChina: Int?
) : Parcelable