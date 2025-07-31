package br.com.fiap.inpulse.utils

import android.view.View

fun View.fadeIn(duration: Long = 300) {
    this.alpha = 0f
    this.visibility = View.VISIBLE
    this.animate().alpha(1f).setDuration(duration).start()
}

fun View.fadeOut(duration: Long = 300) {
    this.animate().alpha(0f).setDuration(duration).withEndAction {
        this.visibility = View.GONE
    }.start()
}