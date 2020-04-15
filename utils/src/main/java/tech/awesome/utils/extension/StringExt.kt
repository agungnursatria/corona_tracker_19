package tech.awesome.utils.extension

import android.annotation.SuppressLint
import tech.awesome.utils.FormatConstant
import java.text.SimpleDateFormat
import java.util.*

fun String?.isContains(comparedTo: String): Boolean =
    this.toString().toLowerCase(Locale.getDefault()).contains(
        comparedTo.toLowerCase(
            Locale.getDefault()
        )
    )

@SuppressLint("SimpleDateFormat")
fun String?.getLastUpdate(): String {
    if (this.isNullOrBlank()) return ""
    val parser =
        SimpleDateFormat(if (this.contains("T")) FormatConstant.FORMAT_DATE_SERVER_FULL else FormatConstant.FORMAT_DATE_SERVER_FULL_2)
    val formatter = SimpleDateFormat(FormatConstant.FORMAT_DATE_LATEST_UPDATE)
    return formatter.format(parser.parse(this) ?: "")
}

@SuppressLint("SimpleDateFormat")
fun String?.getFormattedDate(): String {
    if (this.isNullOrBlank()) return ""
    val parser = SimpleDateFormat(FormatConstant.FORMAT_DATE_SERVER)
    val formatter = SimpleDateFormat(FormatConstant.FORMAT_DATE)
    return formatter.format(parser.parse(this) ?: "")
}

@SuppressLint("SimpleDateFormat")
fun String?.getDailyPathDate(): String {
    if (this.isNullOrBlank()) return ""
    val parser = SimpleDateFormat(FormatConstant.FORMAT_DATE_SERVER)
    val formatter = SimpleDateFormat(FormatConstant.FORMAT_DATE_DAILY_PATH)
    return formatter.format(parser.parse(this) ?: "")
}

@SuppressLint("SimpleDateFormat")
fun String?.getMonth(): String {
    if (this.isNullOrBlank()) return ""
    val parser = SimpleDateFormat(FormatConstant.FORMAT_DATE_SERVER)
    val formatter = SimpleDateFormat(FormatConstant.FORMAT_DATE_MONTH)
    return formatter.format(parser.parse(this) ?: "")
}