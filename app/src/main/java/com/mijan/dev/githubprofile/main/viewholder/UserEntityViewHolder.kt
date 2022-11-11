package com.mijan.dev.githubprofile.main.viewholder

import androidx.core.view.isVisible
import com.mijan.dev.githubprofile.R
import com.mijan.dev.githubprofile.data.model.UsersPagingDataPresentationModel
import com.mijan.dev.githubprofile.databinding.LayoutUserItemBinding
import com.mijan.dev.githubprofile.main.UsersPagingDataAdapter
import com.mijan.dev.githubprofile.utils.loadImageUrl
import java.util.*

class UserEntityViewHolder(private val binding: LayoutUserItemBinding) :
    UsersPagingDataAdapter.BaseUsersPagingDataViewHolder(binding.root) {
    override fun bind(
        item: UsersPagingDataPresentationModel,
        position: Int?,
        onItemClick: ((username: String) -> Unit)?
    ) {
        if (item !is UsersPagingDataPresentationModel.UserEntityPresentationModel) return
        val user = item.userEntity
        val shouldInvertImage = if (position != null) {
            position != 0 && (position + 1) % 4 == 0
        } else {
            false
        }
        with(binding) {
            imageAvatar.loadImageUrl(
                url = user.avatarUrl,
                shouldInvert = shouldInvertImage,
                placeHolderResId = R.drawable.image_user_placeholder
            )
            textUsername.text = user.username.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.getDefault()
                ) else it.toString()
            }
            textDetails.text = user.url
            root.setOnClickListener {
                onItemClick?.invoke(user.username)
            }
            imageNote.isVisible = user.hasNotes
        }
    }
}