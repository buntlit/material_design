package com.buntlit.pictureoftheday.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.text.method.ScrollingMovementMethod
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatTextView

internal class ScrollTextView : AppCompatTextView {
    private val mLastDefY = -1f
    var mLastY = 0f

    constructor(context: Context) : super(context) {
        movementMethod = ScrollingMovementMethod.getInstance()
    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        movementMethod = ScrollingMovementMethod.getInstance()
    }

    constructor(context: Context, attributeSet: AttributeSet?, i: Int) : super(
        context, attributeSet, i
    ) {
        movementMethod = ScrollingMovementMethod.getInstance()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(motionEvent: MotionEvent): Boolean {
        val action: Int = motionEvent.action
        if (action == 0) {
            mLastY = motionEvent.y
            parent.requestDisallowInterceptTouchEvent(true)
        } else if (action == 2) {
            val y: Float = motionEvent.y - mLastY
            if (mLastY != mLastDefY) {
                if (y > 0.0f) {
                    mLastY = mLastDefY
                    parent.requestDisallowInterceptTouchEvent(canScrollVertically(-1))
                }
                if (y < 0.0f) {
                    mLastY = mLastDefY
                    parent.requestDisallowInterceptTouchEvent(canScrollVertically(1))
                }
            }
        } else if (action == 1 || action == 3) {
            parent.requestDisallowInterceptTouchEvent(false)
        }
        return super.onTouchEvent(motionEvent)
    }
}