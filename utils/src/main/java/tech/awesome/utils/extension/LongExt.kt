package tech.awesome.utils.extension

import tech.awesome.utils.FormatConstant
import java.text.SimpleDateFormat
import java.util.*

fun Long.formattedTime(): String {
    val sdf = SimpleDateFormat(FormatConstant.FORMAT_DATE_LATEST_UPDATE, Locale.getDefault())
    return sdf.format(Date(this))
}
