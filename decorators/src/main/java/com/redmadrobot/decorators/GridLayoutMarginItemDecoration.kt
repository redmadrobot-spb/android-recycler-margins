package com.redmadrobot.decorators

import android.graphics.Rect
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View

class GridLayoutMarginItemDecoration(
    outRect: Rect,
    inRect: Rect,
    private val isNeedApplyInnerMargin: (itemPosition: Int) -> Boolean
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
        if (!isNeedApplyInnerMargin(itemPosition)) return

        val layoutManager: GridLayoutManager = parent.layoutManager as GridLayoutManager
        val spanSizeLookup: GridLayoutManager.SpanSizeLookup = layoutManager.spanSizeLookup
        val countSpan = layoutManager.spanCount
        val isVertical = layoutManager.orientation == GridLayoutManager.VERTICAL
        val isLayoutLeftToRight: Boolean = parent.layoutDirection == RecyclerView.LAYOUT_DIRECTION_LTR
        val isNotOrderReverse: Boolean = !layoutManager.reverseLayout

        outRect.set(outMarginRect)

        val spanSizeCurrentPosition = spanSizeLookup.getSpanSize(itemPosition)
        val spanIndexCurrentPosition = spanSizeLookup.getSpanIndex(itemPosition, countSpan)

        val takeAllSpan = spanSizeCurrentPosition == countSpan
        val isFirstInAdapter = itemPosition == 0
        val isLastInAdapter = itemPosition == itemCount - 1
        val isFirstInRow = spanIndexCurrentPosition == 0
        val isLastInRow = spanIndexCurrentPosition + spanSizeCurrentPosition == countSpan

        val isInnerMarginTop: Boolean =
            !isFirstInAdapter &&
                findItemPositionOfStartPrevRow(spanSizeLookup, itemPosition, countSpan)
                    .let { it != RecyclerView.NO_POSITION && isNeedApplyInnerMargin(it) }
        val isInnerMarginBottom: Boolean =
            !isLastInAdapter &&
                findItemPositionOfStartNextRow(spanSizeLookup, itemPosition, countSpan, itemCount)
                    .let { it != RecyclerView.NO_POSITION && isNeedApplyInnerMargin(it) }
        val isInnerMarginStart: Boolean =
            !takeAllSpan &&
                !isFirstInRow &&
                (isFirstInAdapter ||
                    !isFirstInAdapter && isNeedApplyInnerMargin(itemPosition - 1))

        val isInnerMarginEnd: Boolean =
            !takeAllSpan &&
                !isLastInRow &&
                (isLastInAdapter ||
                    !isLastInAdapter && isNeedApplyInnerMargin(itemPosition + 1) ||
                    !isLastInAdapter && spanIndexCurrentPosition + spanSizeCurrentPosition >= spanSizeLookup.getSpanIndex(itemPosition + 1, countSpan)
                    )


        if (isInnerMarginTop) {
            if (isVertical) {
                if (isNotOrderReverse) {
                    outRect.top = inMarginRect.top
                } else {
                    outRect.bottom = inMarginRect.bottom
                }
            } else {
                if (isLayoutLeftToRight) {
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

        if (isInnerMarginStart) {
            if (isVertical) {
                if (isLayoutLeftToRight) {
                    outRect.left = inMarginRect.left
                } else {
                    outRect.right = inMarginRect.right
                }
            } else {
                outRect.top = inMarginRect.top
            }
        }

        if (isInnerMarginEnd) {
            if (isVertical) {
                if (isLayoutLeftToRight) {
                    outRect.right = inMarginRect.right
                } else {
                    outRect.left = inMarginRect.left
                }
            } else {
                outRect.bottom = inMarginRect.bottom
            }
        }
    }


    private fun findItemPositionOfStartNextRow(
        spanSizeLookup: GridLayoutManager.SpanSizeLookup,
        currentPosition: Int,
        countSpan: Int,
        countItem: Int
    ): Int {
        val currentRowIndex = spanSizeLookup.getSpanGroupIndex(currentPosition, countSpan)
        var nextIndex = currentPosition + 1
        while (countItem > nextIndex) {
            val rowIndex = spanSizeLookup.getSpanGroupIndex(nextIndex, countSpan)
            if (rowIndex != currentRowIndex) return nextIndex
            else nextIndex += 1
        }
        return RecyclerView.NO_POSITION
    }

    private fun findItemPositionOfStartPrevRow(
        spanSizeLookup: GridLayoutManager.SpanSizeLookup,
        currentPosition: Int,
        countSpan: Int
    ): Int {
        val currentRowIndex = spanSizeLookup.getSpanGroupIndex(currentPosition, countSpan)
        var prevIndex = currentPosition - 1
        while (prevIndex > -1) {
            val rowIndex = spanSizeLookup.getSpanGroupIndex(prevIndex, countSpan)
            val inRowIndex = spanSizeLookup.getSpanIndex(prevIndex, countSpan)
            if (rowIndex != currentRowIndex && inRowIndex == 0) return prevIndex
            else prevIndex -= 1
        }
        return RecyclerView.NO_POSITION
    }
}