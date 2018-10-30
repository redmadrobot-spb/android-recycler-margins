package com.redmadrobot.recyclerviewmarginitemdecorators.linear

import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.BottomSheetDialog
import android.support.v4.app.Fragment
import android.support.v7.util.DiffUtil
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.marginitemdecarator.util.DiffUtilCallback
import com.example.marginitemdecarator.util.dpToPx
import com.hannesdorfmann.adapterdelegates3.ListDelegationAdapter
import com.redmadrobot.decorators.LinearLayoutMarginItemDecoration
import com.redmadrobot.recyclerviewmarginitemdecorators.ContentInflaterDialog
import com.redmadrobot.recyclerviewmarginitemdecorators.IntListItem
import com.redmadrobot.recyclerviewmarginitemdecorators.IntListItemAdapterDelegate
import com.redmadrobot.recyclerviewmarginitemdecorators.R
import kotlinx.android.synthetic.main.dialog_linear_setting.*
import kotlinx.android.synthetic.main.fragment_page.*
import java.util.*

class LinearExamplePageFragment : Fragment() {

    private val adapter by lazy { Adapter() }

    private val layoutManager: LinearLayoutManager
        get() = recyclerView.layoutManager as LinearLayoutManager

    private val isVertical: Boolean
        get() = arguments!!.getBoolean(ARG_ORIENTATION)

    private var random = Random(32)
    private var data: List<Any> = emptyList()
    private val diffUtilCallback = DiffUtilCallback { left, right ->
        left is IntListItem &&
            right is IntListItem &&
            left.data == right.data
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.layoutManager = LinearLayoutManager(
            context,
            if (isVertical) LinearLayoutManager.VERTICAL else LinearLayoutManager.HORIZONTAL,
            false
        )
        recyclerView.addItemDecoration(
            LinearLayoutMarginItemDecoration(
                context!!.dpToPx(32f).toInt().let { size -> Rect(size, size, size, size) },
                context!!.dpToPx(8f).toInt().let { size -> Rect(size, size, size, size) },
                { position: Int -> (adapter.items[position] as IntListItem).data < 70 }
            )
        )

        updateDataSet(generateData())
        recyclerView.adapter = adapter
        fab.setOnClickListener { SettingBottomSheet().show() }
    }

    private fun updateLayoutManager(reverseLayout: Boolean) {
        layoutManager.reverseLayout = reverseLayout
        recyclerView.invalidateItemDecorations()
    }

    private fun updateDataSet(data: List<Any>) {
        this.data = data
        diffUtilCallback.apply { oldList = adapter.items ?: emptyList(); newList = data }
        adapter.items = data
        recyclerView.invalidateItemDecorations()
        DiffUtil.calculateDiff(diffUtilCallback).dispatchUpdatesTo(adapter)
    }

    private fun generateData(): List<Any> = (0..20).map { IntListItem(random.nextInt(100)) }

    private fun createContentInflaterDialog(): ContentInflaterDialog {
        var dialogData = data.toList()
        return ContentInflaterDialog(
            context!!,
            adapterFactory = { Adapter() },
            layoutManagerFactory = {
                LinearLayoutManager(
                    context,
                    layoutManager.orientation,
                    layoutManager.reverseLayout
                )
            },
            itemDecorationFactory = {
                LinearLayoutMarginItemDecoration(
                    context!!.dpToPx(8f).toInt().let { Rect(it, it, it, it) },
                    context!!.dpToPx(4f).toInt().let { Rect(it, it, it, it) }
                ) { (dialogData[it] as IntListItem).data < 70 }
            },
            createActions = { dialog, layout ->
                with(layout) {
                    addView(Button(context).apply {
                        setOnClickListener {
                            dialogData = dialogData.shuffled()
                            dialog.updateData(dialogData)
                        }
                        text = "Shuffle"
                    })
                    addView(Button(context).apply {
                        setOnClickListener {
                            dialogData = dialogData.toMutableList()
                                .apply { add(IntListItem(random.nextInt(30) + 70)) }
                            dialog.updateData(dialogData)
                        }
                        text = "+1 Header"
                    })

                    addView(Button(context).apply {
                        setOnClickListener {
                            dialogData = dialogData.toMutableList()
                                .apply { add(IntListItem(random.nextInt(70))) }
                            dialog.updateData(dialogData)
                        }
                        text = "+1 Item"
                    })

                    addView(Button(context).apply {
                        setOnClickListener {
                            if (dialog.data.isNotEmpty()) {
                                dialogData = dialogData.toMutableList()
                                    .apply { removeAt(dialogData.size - 1) }
                                dialog.updateData(dialogData)
                            }
                        }
                        text = "Remove Last"
                    })

                    addView(Button(context).apply {
                        setOnClickListener {
                            if (dialog.data.isNotEmpty()) {
                                dialogData = dialogData.toMutableList()
                                    .apply { removeAt(0) }
                                dialog.updateData(dialogData)
                            }
                        }
                        text = "Remove First"
                    })

                    addView(Button(context).apply {
                        setOnClickListener {
                            if (dialog.data.isNotEmpty()) {
                                dialogData = emptyList()
                                dialog.updateData(dialogData)
                            }
                        }
                        text = "Remove All"
                    })

                    addView(Button(context).apply {
                        setOnClickListener {
                            handler.postDelayed({ updateDataSet(dialogData) }, 500)
                            dialog.dismiss()
                        }
                        text = "Apply"
                    })
                }
            }
        ).apply { updateData(adapter.items) }
    }


    private inner class Adapter : ListDelegationAdapter<List<Any>>() {
        init {
            delegatesManager.addDelegate(IntListItemAdapterDelegate(isVertical))
        }
    }

    private inner class SettingBottomSheet : BottomSheetDialog(context!!) {
        init {
            setContentView(R.layout.dialog_linear_setting)

            reverseCheckBox.isChecked = layoutManager.reverseLayout

            changeDataSetTextView.setOnClickListener {
                createContentInflaterDialog().show()
                dismiss()
            }

            reverseCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
                updateLayoutManager(isChecked)
                dismiss()
            }
        }
    }

    companion object {
        private const val ARG_ORIENTATION = "ARG_ORIENTATION"
        fun create(isVertical: Boolean) = LinearExamplePageFragment().apply {
            arguments = Bundle().apply {
                putBoolean(ARG_ORIENTATION, isVertical)
            }
        }
    }
}