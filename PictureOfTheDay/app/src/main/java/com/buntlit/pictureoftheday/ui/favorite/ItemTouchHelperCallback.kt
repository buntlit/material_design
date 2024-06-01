package com.buntlit.pictureoftheday.ui.favorite

import android.graphics.Canvas
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.buntlit.pictureoftheday.R

class ItemTouchHelperCallback :
    ItemTouchHelper.Callback() {

    private var limitScrollX = 0
    private var currentScrollX = 0
    private var currentScrollXWhileInActive = 0
    private var initXWhenInActive = 0f
    private var fistInActive = false

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int = makeMovementFlags(0, ItemTouchHelper.START or ItemTouchHelper.END)


    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = true

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float = Float.MAX_VALUE

    override fun getSwipeEscapeVelocity(defaultValue: Float): Float = Float.MAX_VALUE

    override fun isLongPressDragEnabled(): Boolean = false

    override fun isItemViewSwipeEnabled(): Boolean = false

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            (viewHolder as ItemTouchHelperViewHolder).onItemSelected()
        }
        super.onSelectedChanged(viewHolder, actionState)
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)

        (viewHolder as ItemTouchHelperViewHolder).onItemClear()

        if (viewHolder.itemView.scrollX >= limitScrollX) {
            viewHolder.itemView.scrollTo(limitScrollX, 0)
        } else if (viewHolder.itemView.scrollX < 0) {
            viewHolder.itemView.scrollTo(0, 0)
        } else if(viewHolder.itemView.scrollX == 0){
            (viewHolder as ItemTouchHelperViewHolder).onItemOnStartPosition()
        }
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            limitScrollX =
                viewHolder.itemView.findViewById<View>(R.id.delete_prompt).width
            if (dX == 0f) {
                currentScrollX = viewHolder.itemView.scrollX
                fistInActive = true
            }

            if (isCurrentlyActive) {

                var scrollOffset = currentScrollX - dX.toInt()
                if (scrollOffset > limitScrollX) {
                    scrollOffset = limitScrollX
                } else if (scrollOffset < 0) {
                    scrollOffset = 0
                }

                viewHolder.itemView.scrollTo(scrollOffset, 0)

            } else {

                if (fistInActive) {
                    fistInActive = false
                    currentScrollXWhileInActive = viewHolder.itemView.scrollX
                    initXWhenInActive = dX
                }

                if (viewHolder.itemView.scrollX < limitScrollX) {
                    viewHolder.itemView.scrollTo(
                        (currentScrollXWhileInActive * dX / initXWhenInActive).toInt(),
                        0
                    )
                }

            }

        }

    }

}