package tech.awesome.utils.extension

import android.content.res.Resources
import java.text.NumberFormat
import java.util.*

fun Int.separatedNumber(): String = NumberFormat.getNumberInstance(Locale.getDefault()).format(this)
fun Int.toDp(): Int = (this / Resources.getSystem().displayMetrics.density).toInt()
fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()