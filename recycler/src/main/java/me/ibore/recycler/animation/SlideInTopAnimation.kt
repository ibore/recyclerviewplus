package me.ibore.recycler.animation

import android.animation.Animator
import android.animation.ObjectAnimator
import android.view.View

class SlideInTopAnimation: BaseAnimation {
    override fun getAnimators(view: View): Array<Animator> {
        return arrayOf(ObjectAnimator.ofFloat(view, "translationY", -view.measuredHeight.toFloat(), 0F))
    }
}