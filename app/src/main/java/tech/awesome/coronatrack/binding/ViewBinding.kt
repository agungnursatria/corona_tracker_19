package tech.awesome.coronatrack.binding

import android.annotation.SuppressLint
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import tech.awesome.data.network.OverviewItem
import tech.awesome.utils.extension.*
import java.text.NumberFormat

@SuppressLint("SetTextI18n")
@BindingAdapter("setLatestUpdateTime")
fun TextView.setLatestUpdateTime(time: String?) {
    this.text = if (time != null) "Last Update: ${time.getLastUpdate()}" else ""
}
@BindingAdapter("setFormattedDate")
fun TextView.setFormattedDate(time: String?) {
    this.text = if (time != null) "Last Update: ${time.getFormattedDate()}" else ""
}
@BindingAdapter("setFormattedDate2")
fun TextView.setFormattedDate2(time: String?) {
    this.text = time?.getFormattedDate() ?: ""
}
@BindingAdapter("setFormattedDate")
fun TextView.setFormattedDate(time: Long?) {
    this.text = if (time != null) "Last Update: ${time.formattedTime()}" else ""
}

@BindingAdapter("setOverviewValue")
fun TextView.setOverviewValue(item: OverviewItem?) {
    this.text = NumberFormat.getIntegerInstance().format(item?.value ?: 0)
}

@BindingAdapter("setOverviewValue")
fun TextView.setOverviewValue(item: Int?) {
    this.text = NumberFormat.getIntegerInstance().format(item ?: 0)
}

@BindingAdapter("setFlagImage")
fun ImageView.setFlagImage(isoCode: String?) {
    if (!isoCode.isNullOrBlank())
        this.loadImage("https://www.countryflags.io/$isoCode/flat/64.png")
}