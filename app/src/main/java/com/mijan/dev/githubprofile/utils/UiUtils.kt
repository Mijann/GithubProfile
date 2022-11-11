package com.mijan.dev.githubprofile.utils

import android.content.res.Resources
import android.text.TextUtils
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import com.google.android.material.snackbar.Snackbar
import com.mijan.dev.githubprofile.R
import com.mijan.dev.githubprofile.data.model.SnackbarStatus

fun View.showSnackbar(
    status: SnackbarStatus = SnackbarStatus.SUCCESS,
    message: String,
    duration: Int = Snackbar.LENGTH_SHORT
) {
    Snackbar.make(this, message, duration).also { snackbar ->
        val resources = snackbar.context.resources
        snackbar.view.background = androidx.core.content.res.ResourcesCompat.getDrawable(
            resources,
            R.drawable.background_snackbar,
            null
        )
        snackbar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
            ?.setupTextView(status)

    }.show()
}

private fun TextView.setupTextView(status: SnackbarStatus) {
    TextViewCompat.setLineHeight(this, 17.sp)
    maxLines = 3
    ellipsize = TextUtils.TruncateAt.END
    setTextColor(ContextCompat.getColor(context, R.color.white))
    compoundDrawablePadding = 16.dp
    with((layoutParams as LinearLayout.LayoutParams)) {
        rightMargin = 16.dp
    }
    when (status) {
        SnackbarStatus.SUCCESS -> setCompoundDrawablesWithIntrinsicBounds(
            R.drawable.ic_baseline_check_circle_24,
            0,
            0,
            0
        )
        SnackbarStatus.ERROR -> setCompoundDrawablesWithIntrinsicBounds(
            R.drawable.ic_baseline_info_24,
            0,
            0,
            0
        )
    }
}

val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

val Int.sp: Int
    get() = (this * Resources.getSystem().displayMetrics.scaledDensity).toInt()