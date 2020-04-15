package tech.awesome.utils

import android.animation.ValueAnimator
import android.widget.TextView
import java.text.NumberFormat

object Animator {
    fun animateIncrementNumber(
        view: TextView,
        finalValue: Int = 0,
        initialValue: Int = 0
    ) {
        val valueAnimator = ValueAnimator.ofInt(initialValue, finalValue)
        valueAnimator.duration = 1000L
        valueAnimator.addUpdateListener { value ->
            view.text = NumberFormat.getIntegerInstance()
                .format(value.animatedValue.toString().toIntOrNull() ?: 0)
        }
        valueAnimator.start()
    }
}