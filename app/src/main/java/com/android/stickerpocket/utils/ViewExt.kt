package com.android.stickerpocket.utils

import android.view.View
import com.android.stickerpocket.R

object ViewExt {
    fun View.shakeMe() {
        val animation = android.view.animation.AnimationUtils.loadAnimation(this.context, R.anim.shake_item)
        this.startAnimation(animation)
    }
}