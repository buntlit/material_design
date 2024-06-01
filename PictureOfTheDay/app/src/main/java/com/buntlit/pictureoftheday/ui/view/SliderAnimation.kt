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
    private val isOpen: Boolean,
    private val isClicked: Boolean
) :
    TransitionManager() {

    init {
        if (isClicked) {
            beginDelayedTransition(container, TransitionSet().apply {
                ordering = TransitionSet.ORDERING_TOGETHER
                duration = durationTime
                addTransition(ChangeBounds())
                addTransition(Fade())
            }).apply {
                if (isOpen) {
                    if (view.height == 0) {
                        view.visibility = View.VISIBLE
                        view.post {
                            view.y = 0f - view.height
                            animateSlideIn()
                        }
                    } else {
                        animateSlideIn()
                    }
                } else {
                    animateSlideOut()
                }

            }
        } else {
            if (isOpen) {
                if (view.height == 0) {
                    view.visibility = View.VISIBLE
                    view.post {
                        view.y = 0f
                        view.alpha = 1f
                    }
                } else {
                    view.y = 0f
                    view.alpha = 1f
                }
            } else {
                view.y = 0f - view.height
                view.alpha = 0f
            }
        }
        view.visibility = if (isOpen) View.VISIBLE else View.GONE
    }

    private fun animateSlideIn() {
        view.animate().apply {
            translationY(0f)
            alpha(1f)
            duration = durationTime
        }
    }

    private fun animateSlideOut() {
        view.animate().apply {
            translationY(-view.height.toFloat())
            alpha(0f)
            duration = durationTime
        }
    }

}