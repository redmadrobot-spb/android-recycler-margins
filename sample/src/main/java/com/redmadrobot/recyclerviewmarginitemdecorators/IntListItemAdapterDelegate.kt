package com.redmadrobot.recyclerviewmarginitemdecorators

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hannesdorfmann.adapterdelegates3.AdapterDelegate
import kotlinx.android.synthetic.main.item_list_horizontal_text_view.view.*
import kotlin.math.absoluteValue

data class IntListItem(val data: Int) {
    val color by lazy {
        val aData = data.absoluteValue
        val h = 360 * ((aData % 16) / 16f)
        val s = (aData % 100 / 100f) * 0.5f + 0.3f
        val v = (aData % 100 / 100f) * 0.5f + 0.3f
        return@lazy Color.HSVToColor(floatArrayOf(h, s, v))
    }
}

class IntListItemAdapterDelegate(
    private val isVertical: Boolean
) : AdapterDelegate<List<Any>>() {

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                if (isVertical) R.layout.item_list_vertical_text_view else R.layout.item_list_horizontal_text_view,
                parent,
                false
            )
        )


    override fun isForViewType(items: List<Any>, position: Int): Boolean =
        items[position] is IntListItem

    override fun onBindViewHolder(
        items: List<Any>,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: MutableList<Any>
    ) {
        (holder as ViewHolder).bind(items[position] as IntListItem)
    }

    private class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(listItem: IntListItem) {
            itemView.textView.text = listItem.data.toString()
            itemView.setBackgroundColor(listItem.color)
        }
    }
}