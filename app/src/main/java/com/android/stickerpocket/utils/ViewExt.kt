package com.android.stickerpocket.utils

import android.graphics.Color
import android.view.View
import com.android.stickerpocket.R
import com.google.android.material.card.MaterialCardView

object ViewExt {
    fun View.shakeMe() {
        val animation = android.view.animation.AnimationUtils.loadAnimation(this.context, R.anim.shake_item)
        this.startAnimation(animation)
    }

    fun MaterialCardView.setBorder() {
        this.strokeColor = Color.GREEN
        this.strokeWidth = 6
    }

    fun MaterialCardView.removeBorder() {
        this.strokeColor = Color.TRANSPARENT
        this.strokeWidth = 0
    }
}