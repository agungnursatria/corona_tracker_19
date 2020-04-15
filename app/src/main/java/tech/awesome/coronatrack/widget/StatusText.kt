package tech.awesome.coronatrack.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import tech.awesome.coronatrack.R
import tech.awesome.utils.StatusConstant
import java.text.NumberFormat

class StatusText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {
    var value: Int = 0
    var status: Int = 0
        @SuppressLint("SetTextI18n")
        set(status) {
            var color = 0
            lateinit var textStatus: String
            when (status) {
                StatusConstant.CONFIRMED -> {
                    color = ContextCompat.getColor(context, R.color.confirmed)
                    textStatus = context.getString(R.string.label_confirmed)
                }
                StatusConstant.RECOVERED -> {
                    color = ContextCompat.getColor(context, R.color.recovered)
                    textStatus = context.getString(R.string.label_recovered)
                }
                StatusConstant.DEATHS -> {
                    color = ContextCompat.getColor(context, R.color.death)
                    textStatus = context.getString(R.string.label_death)
                }
            }
            if (color != 0) {
                text = "$textStatus: ${NumberFormat.getIntegerInstance().format(value)}"
                setTextColor(color)
            }
            field = status
        }
}