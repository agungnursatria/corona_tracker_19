package tech.awesome.utils.extension

import android.content.Context
import android.os.Build
import android.widget.ImageView
import androidx.annotation.DrawableRes
import coil.api.load
import coil.transform.CircleCropTransformation
import tech.awesome.utils.R

fun ImageView.loadImage(url: String) {
    load(url) {
        crossfade(true)
        placeholder(R.drawable.ic_placeholder_flag)
        error(R.drawable.ic_placeholder_flag)
//        transformations(CircleCropTransformation())
    }
}


fun ImageView.setAssetImage(context: Context, @DrawableRes drawableRes: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        setImageDrawable(context.getDrawable(drawableRes))
    } else {
        setImageDrawable(context.resources.getDrawable(drawableRes))
    }
}