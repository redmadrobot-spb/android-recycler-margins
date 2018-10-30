package com.example.marginitemdecarator.util

import android.support.v7.util.DiffUtil

class DiffUtilCallback(
    private val hasSameId: (Any, Any) -> Boolean
) : DiffUtil.Callback() {

    var oldList: List<Any> = emptyList()
    var newList: List<Any> = emptyList()

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val old = oldList[oldItemPosition]
        val new = newList[newItemPosition]
        return hasSameId(old, new)
    }

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}