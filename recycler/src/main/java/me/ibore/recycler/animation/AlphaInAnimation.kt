package me.ibore.recycler.animation

import android.animation.Animator
import android.animation.ObjectAnimator
import android.view.View


class AlphaInAnimation @JvmOverloads constructor(private val mFrom: Float = 0f) : BaseAnimation {

    override fun getAnimators(view: View): Array<Animator> {
        return arrayOf(ObjectAnimator.ofFloat(view, "alpha", mFrom, 1f))
    }
}