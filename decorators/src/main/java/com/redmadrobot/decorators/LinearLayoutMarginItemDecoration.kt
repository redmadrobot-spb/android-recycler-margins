package com.redmadrobot.decorators

import android.graphics.Rect
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View

class LinearLayoutMarginItemDecoration(
    outRect: Rect,
    inRect: Rect,
    private val isNeedApplyMargin: (position: Int) -> Boolean
) : RecyclerView.ItemDecoration() {

    private val outMarginRect = Rect(outRect)
    private val inMarginRect = Rect(inRect)

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        val itemPosition = parent.getChildAdapterPosition(view)
        if (itemPosition == RecyclerView.NO_POSITION) return
        val itemCount = parent.adapter?.itemCount ?: 0
        if (itemCount == 0) return
        if (!isNeedApplyMargin(itemPosition)) return

        val layoutManager: LinearLayoutManager = parent.layoutManager as LinearLayoutManager
        val isVertical = layoutManager.orientation == LinearLayoutManager.VERTICAL
        val isLayoutLeftToRight: Boolean = parent.layoutDirection == RecyclerView.LAYOUT_DIRECTION_LTR
        val isNotOrderReverse: Boolean = !layoutManager.reverseLayout

        outRect.set(outMarginRect)

        val isInnerMarginTop: Boolean = itemPosition != 0 && isNeedApplyMargin(itemPosition - 1)
        val isInnerMarginBottom: Boolean = itemPosition != itemCount - 1 && isNeedApplyMargin(itemPosition + 1)

        if (isInnerMarginTop) {
            if (isVertical) {
                if (isNotOrderReverse) {
                    outRect.top = inMarginRect.top
                } else {
                    outRect.bottom = inMarginRect.bottom
                }
            } else {
                if(isLayoutLeftToRight) {
                    if (isNotOrderReverse) {
                        outRect.left = inMarginRect.left
                    } else {
                        outRect.right = inMarginRect.right
                    }
                } else {
                    if (isNotOrderReverse) {
                        outRect.right = inMarginRect.right
                    } else {
                        outRect.left = inMarginRect.left
                    }
                }
            }
        }

        if (isInnerMarginBottom) {
            if (isVertical) {
                if (isNotOrderReverse) {
                    outRect.bottom = inMarginRect.bottom
                } else {
                    outRect.top = inMarginRect.top
                }
            } else {
                if (isLayoutLeftToRight) {
                    if (isNotOrderReverse) {
                        outRect.right = inMarginRect.right
                    } else {
                        outRect.left = inMarginRect.left
                    }
                } else {
                    if (isNotOrderReverse) {
                        outRect.left = inMarginRect.left
                    } else {
                        outRect.right = inMarginRect.right
                    }
                }
            }
        }
    }
}