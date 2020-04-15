package tech.awesome.utils.extension

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import androidx.annotation.ColorRes

fun Context.getColorFixed(@ColorRes color: Int): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        getColor(color)
    } else {
        resources.getColor(color)
    }
}

fun Context.isDarkMode() : Boolean {
    return resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
}