package com.redmadrobot.recyclerviewmarginitemdecorators.grid

import android.graphics.Rect
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.v4.app.Fragment
import android.support.v7.util.DiffUtil
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SeekBar
import com.example.marginitemdecarator.util.DiffUtilCallback
import com.example.marginitemdecarator.util.dpToPx
import com.hannesdorfmann.adapterdelegates3.ListDelegationAdapter
import com.redmadrobot.decorators.GridLayoutMarginItemDecoration
import com.redmadrobot.recyclerviewmarginitemdecorators.ContentInflaterDialog
import com.redmadrobot.recyclerviewmarginitemdecorators.IntListItem
import com.redmadrobot.recyclerviewmarginitemdecorators.IntListItemAdapterDelegate
import com.redmadrobot.recyclerviewmarginitemdecorators.R
import kotlinx.android.synthetic.main.dialog_grid_settings.*
import kotlinx.android.synthetic.main.fragment_page.*
import java.util.*

class GridExamplePageFragment : Fragment() {

    private val adapter: Adapter by lazy { Adapter() }
    private val layoutManager: GridLayoutManager
        get() = recyclerView.layoutManager as GridLayoutManager


    private val isVertical: Boolean
        get() = arguments!!.getBoolean(ARG_ORIENTATION)

    private val random = Random(32)
    private var data: List<SpanWrapper<Any>> = emptyList()
    private val diffUtilCallback = DiffUtilCallback { left, right ->
        left is IntListItem &&
            right is IntListItem &&
            left.data == right.data
    }

    private val spanSizeLookup: GridLayoutManager.SpanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {

        init {
            isSpanIndexCacheEnabled = true
        }

        override fun getSpanSize(position: Int): Int =
            data[position].spanCount.takeIf { it > 0 } ?: layoutManager.spanCount
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.layoutManager = GridLayoutManager(
            context,
            3,
            if (isVertical) GridLayoutManager.VERTICAL else GridLayoutManager.HORIZONTAL,
            false
        ).apply {
            spanSizeLookup = this@GridExamplePageFragment.spanSizeLookup
        }
        recyclerView.addItemDecoration(
            GridLayoutMarginItemDecoration(
                context!!.dpToPx(32F).toInt().let { size -> Rect(size, size, size, size) },
                context!!.dpToPx(8f).toInt().let { size -> Rect(size, size, size, size) },
                { position: Int -> data[position].spanCount > 0 }
            )
        )

        updateDataSet(generateData())
        recyclerView.adapter = adapter
        fab.setOnClickListener { SettingBottomSheet().show() }
    }

    private fun updateLayoutManager(reverseLayout: Boolean, spanCount: Int) {
        layoutManager.apply {
            this.spanCount = spanCount
            this.reverseLayout = reverseLayout
            spanSizeLookup.invalidateSpanIndexCache()
            recyclerView.invalidateItemDecorations()
        }
    }

    private fun updateDataSet(data: List<SpanWrapper<Any>>) {
        spanSizeLookup.invalidateSpanIndexCache()

        val unwrappedData = data.map { it.listItem }
        diffUtilCallback.apply { oldList = adapter.items ?: emptyList(); newList = unwrappedData }
        this.data = data
        adapter.items = unwrappedData
        DiffUtil.calculateDiff(diffUtilCallback).dispatchUpdatesTo(adapter)
        recyclerView.invalidateItemDecorations()
    }

    private fun generateData(): List<SpanWrapper<Any>> =
        (0..20).map { IntListItem(random.nextInt(100)) }
            .map { SpanWrapper<Any>(if (it.data < 70) 1 else -1, it) }

    private fun createContentInflaterDialog(): ContentInflaterDialog {
        var wrappedData = data.toList()
        return ContentInflaterDialog(
            context!!,
            adapterFactory = { Adapter() },
            layoutManagerFactory = {
                GridLayoutManager(
                    context,
                    layoutManager.spanCount,
                    layoutManager.orientation,
                    layoutManager.reverseLayout
                ).apply {
                    spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                        override fun getSpanSize(position: Int): Int =
                            wrappedData[position].spanCount.let { spanSize -> if (spanSize > 0) spanSize else spanCount }
                    }
                }
            },
            itemDecorationFactory = {
                GridLayoutMarginItemDecoration(
                    context!!.dpToPx(8f).toInt().let { Rect(it, it, it, it) },
                    context!!.dpToPx(2f).toInt().let { Rect(it, it, it, it) }
                ) { (wrappedData[it] as SpanWrapper<*>).spanCount > -1 }
            },
            createActions = { dialog, layout ->
                with(layout) {
                    addView(Button(context).apply {
                        setOnClickListener {
                            wrappedData = wrappedData.shuffled()
                            dialog.updateData(wrappedData.map { it.listItem })
                        }
                        text = "Shuffle"
                    })
                    addView(Button(context).apply {
                        setOnClickListener {
                            wrappedData = wrappedData.toMutableList()
                                .apply { add(SpanWrapper(-1, IntListItem(random.nextInt(70)))) }
                            dialog.updateData(wrappedData.map { it.listItem })
                        }
                        text = "+1 Header"
                    })

                    addView(Button(context).apply {
                        setOnClickListener {
                            wrappedData = wrappedData.toMutableList()
                                .apply { add(SpanWrapper(1, IntListItem(random.nextInt(70)))) }
                            dialog.updateData(wrappedData.map { it.listItem })
                        }
                        text = "+1 Item 1 span"
                    })

                    addView(Button(context).apply {
                        setOnClickListener {
                            wrappedData = wrappedData.toMutableList()
                                .apply { add(SpanWrapper(2, IntListItem(random.nextInt(70)))) }
                            dialog.updateData(wrappedData.map { it.listItem })
                        }
                        text = "+1 Item 2 span"
                    })

                    addView(Button(context).apply {
                        setOnClickListener {
                            if (wrappedData.isNotEmpty()) {
                                wrappedData = wrappedData.toMutableList()
                                    .apply { removeAt(wrappedData.size - 1) }
                                dialog.updateData(wrappedData.map { it.listItem })
                            }
                        }
                        text = "Remove Last"
                    })

                    addView(Button(context).apply {
                        setOnClickListener {
                            if (dialog.data.isNotEmpty()) {
                                wrappedData = wrappedData.toMutableList()
                                    .apply { removeAt(0) }
                                dialog.updateData(wrappedData.map { it.listItem })
                            }
                        }
                        text = "Remove First"
                    })

                    addView(Button(context).apply {
                        setOnClickListener {
                            if (dialog.data.isNotEmpty()) {
                                wrappedData = emptyList()
                                dialog.updateData(wrappedData)
                            }
                        }
                        text = "Remove All"
                    })

                    addView(Button(context).apply {
                        setOnClickListener {
                            handler.postDelayed({ updateDataSet(wrappedData) }, 500)
                            dialog.dismiss()
                        }
                        text = "Apply"
                    })
                }
            }
        ).apply { updateData(adapter.items) }
    }

    inner class Adapter : ListDelegationAdapter<List<Any>>() {
        init {
            delegatesManager.addDelegate(IntListItemAdapterDelegate(isVertical))
        }
    }

    inner class SettingBottomSheet : BottomSheetDialog(context!!) {
        init {
            setContentView(R.layout.dialog_grid_settings)

            reverseCheckBox.isChecked = layoutManager.reverseLayout

            changeDataSetTextView.setOnClickListener {
                createContentInflaterDialog().show()
                dismiss()
            }
            spanSelectorRangeBar.progress = layoutManager.spanCount - 1
            spanSelectorRangeBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser) updateLayoutManager(reverseCheckBox.isChecked, progress + 1)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })

            reverseCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
                updateLayoutManager(isChecked, spanSelectorRangeBar.progress + 1)
                dismiss()
            }
        }
    }

    data class SpanWrapper<T>(val spanCount: Int, val listItem: T)

    companion object {
        private const val ARG_ORIENTATION = "ARG_ORIENTATION"
        fun create(isVertical: Boolean) = GridExamplePageFragment().apply {
            arguments = Bundle().apply {
                putBoolean(ARG_ORIENTATION, isVertical)
            }
        }
    }
}