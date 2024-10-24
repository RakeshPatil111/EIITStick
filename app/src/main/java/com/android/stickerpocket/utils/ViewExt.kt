package com.android.stickerpocket.utils

import android.graphics.Color
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import com.android.stickerpocket.R
import com.google.android.material.card.MaterialCardView
import kotlin.math.roundToInt


object ViewExt {
    fun View.shakeMe() {
        val animation = android.view.animation.AnimationUtils.loadAnimation(this.context, R.anim.shake_item)
        this.startAnimation(animation)
    }

    fun View.zoomIn() {
        val animation = android.view.animation.AnimationUtils.loadAnimation(this.context, R.anim.zoom_in_item)
        this.startAnimation(animation)
    }

    fun MaterialCardView.setBorder() {
        this.strokeColor = Color.GREEN
        this.strokeWidth = 6
    }

    fun MaterialCardView.setBlueBorder() {
        this.strokeColor = Color.CYAN
        this.strokeWidth = 6
    }

    fun MaterialCardView.removeBorder() {
        this.strokeColor = Color.TRANSPARENT
        this.strokeWidth = 0
    }

    fun ViewGroup.findViewAt(viewGroup: ViewGroup, x: Int, y: Int): View? {
        for (i in 0 until viewGroup.childCount) {
            val child = viewGroup.getChildAt(i)
            if (child is ViewGroup) {
                val foundView = findViewAt(child, x, y)
                if (foundView != null && foundView.isShown) {
                    return foundView
                }
            } else {
                val location = IntArray(2)
                child.getLocationOnScreen(location)
                val rect = Rect(
                    location[0],
                    location[1], location[0] + child.width, location[1] + child.height
                )
                if (rect.contains(x, y)) {
                    return child
                }
            }
        }

        return null
    }

    fun ViewGroup.getViewByCoordinates(x: Float, y: Float) : View? {
        (childCount - 1 downTo 0)
            .map { this.getChildAt(it) }
            .forEach {
                val bounds = Rect()
                it.getHitRect(bounds)
                if (bounds.contains(x.toInt(), y.toInt())) {
                    return it
                }
            }
        return null
    }

    fun ViewGroup.getApproxCustomTag(viewGroup: ViewGroup, draggedViewHeight: Float): Int {
        val firstChild = viewGroup.getChildAt(0)
        val modOfHeight: Float = draggedViewHeight / firstChild.height
        return modOfHeight.roundToInt()
    }
}