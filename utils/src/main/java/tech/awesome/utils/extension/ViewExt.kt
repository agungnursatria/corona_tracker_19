package tech.awesome.utils.extension

import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import com.facebook.shimmer.ShimmerFrameLayout

fun View.visible(){
    this.visibility = View.VISIBLE
}

fun View.gone(){
    this.visibility = View.GONE
}

fun View.invisible(){
    this.visibility = View.INVISIBLE
}

fun ShimmerFrameLayout.startShimmering() {
    visible()
    startShimmer()
}

fun ShimmerFrameLayout.stopShimmering() {
    invisible()
    stopShimmer()
}

fun Context.color(resource: Int): Int {
    return ContextCompat.getColor(this, resource)
}