package com.example.marginitemdecarator.util

import android.content.Context
import android.support.annotation.Dimension
import android.util.TypedValue

fun Context.dpToPx(@Dimension(unit = Dimension.DP) dp: Float) =
    resources.displayMetrics.let { displayMetrics ->
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            displayMetrics
        )
    }
