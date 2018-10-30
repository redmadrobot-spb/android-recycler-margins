package com.redmadrobot.recyclerviewmarginitemdecorators

import android.content.Context
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialog
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.hannesdorfmann.adapterdelegates3.ListDelegationAdapter
import kotlinx.android.synthetic.main.fragment_dialog_content_inflater.*

class ContentInflaterDialog(
    context: Context,
    private val adapterFactory: () -> ListDelegationAdapter<List<Any>>,
    layoutManagerFactory: () -> RecyclerView.LayoutManager,
    itemDecorationFactory: () -> RecyclerView.ItemDecoration,
    createActions: (ContentInflaterDialog, LinearLayout) -> Unit
) : BottomSheetDialog(context) {

    private val contentView =
        LayoutInflater.from(context).inflate(R.layout.fragment_dialog_content_inflater, null, false)

    var data: List<Any> = emptyList()
        private set
    private val adapter by lazy { adapterFactory() }

    init {
        setContentView(contentView)
        with(BottomSheetBehavior.from(contentView.parent as View)) {
            state = BottomSheetBehavior.STATE_EXPANDED
            skipCollapsed = true
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = layoutManagerFactory()
        recyclerView.addItemDecoration(itemDecorationFactory())
        createActions(this, actionsLayout)
    }

    fun updateData(data: List<Any>) {
        this.data = data
        adapter.items = data
        adapter.notifyDataSetChanged()
        recyclerView.invalidateItemDecorations()
    }


}