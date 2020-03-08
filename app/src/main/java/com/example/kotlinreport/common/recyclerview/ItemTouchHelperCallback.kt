package com.example.kotlinreport.common.recyclerview

import android.graphics.Canvas
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinreport.adapter.UserAdapter
import com.loopeer.itemtouchhelperextension.ItemTouchHelperExtension

class ItemTouchHelperCallback: ItemTouchHelperExtension.Callback() {
    override fun getMovementFlags(p0: RecyclerView?, p1: RecyclerView.ViewHolder?): Int {
        return makeMovementFlags(0, ItemTouchHelper.START)
    }

    override fun onMove(
        p0: RecyclerView?,
        p1: RecyclerView.ViewHolder?,
        p2: RecyclerView.ViewHolder?
    ): Boolean {
        return false
    }

    override fun onSwiped(p0: RecyclerView.ViewHolder?, p1: Int) {

    }

    override fun onChildDraw(
        c: Canvas?,
        recyclerView: RecyclerView?,
        viewHolder: RecyclerView.ViewHolder?,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        var dxNew: Float = dX
        if (viewHolder is UserAdapter.UserViewHolder) {

            if (dX < -viewHolder.mSwipeContainer.width) {
                dxNew = -viewHolder.mSwipeContainer.width.toFloat()
            }
            viewHolder.mViewContent.translationX = dxNew
        }


    }

}
