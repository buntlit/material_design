package com.buntlit.pictureoftheday.ui.view

import android.view.View
import android.view.ViewGroup
import androidx.transition.ChangeBounds
import androidx.transition.Fade
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet

class SliderAnimation(
    container: ViewGroup,
    private val view: View,
    private val durationTime: Long,
    private val isOpen: Boolean
) :
    TransitionManager() {

    init {
        beginDelayedTransition(container, TransitionSet().apply {
            ordering = TransitionSet.ORDERING_TOGETHER
            duration = durationTime
            addTransition(ChangeBounds())
            addTransition(Fade())
        }).apply {
            if (isOpen) {
                view.animate().apply {
                    animateSlideOut()
                }
            } else {
                if (view.height == 0) {
                    view.visibility = View.VISIBLE
                    view.post {
                        view.y = 0f - view.height
                        animateSlideIn()
                    }
                } else {
                    animateSlideIn()
                }
            }
        }
        view.visibility = if (isOpen) View.GONE else View.VISIBLE
    }

    private fun animateSlideIn() {
        view.animate().apply {
            translationY(0f)
            alpha(1.0f)
            duration = durationTime
        }
    }

    private fun animateSlideOut() {
        view.animate().apply {
            translationY(-view.height.toFloat())
            alpha(0.0f)
            duration = durationTime
        }
    }

}