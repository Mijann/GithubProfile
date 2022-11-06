package com.mijan.dev.githubprofile.utils

import android.graphics.ColorMatrixColorFilter
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

fun ImageView.loadImageUrl(
    url: String,
    shouldInvert: Boolean = false,
    placeHolderResId: Int? = null
) {
    Glide.with(context)
        .load(url)
        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
        .addListener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean
            ) = true

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                if (shouldInvert) resource?.toNegative()
                setImageDrawable(resource)
                return true
            }

        })
        .apply {
            placeHolderResId?.let { resId ->
                placeholder(resId)
            }
        }
        .into(this)
}

fun Drawable.toNegative(): Drawable {
    val negative = floatArrayOf(
        -1.0f, .0f, .0f, .0f, 255.0f,
        .0f, -1.0f, .0f, .0f, 255.0f,
        .0f, .0f, -1.0f, .0f, 255.0f,
        .0f, .0f, .0f, 1.0f, .0f
    )
    this.colorFilter = ColorMatrixColorFilter(negative)
    return this
}