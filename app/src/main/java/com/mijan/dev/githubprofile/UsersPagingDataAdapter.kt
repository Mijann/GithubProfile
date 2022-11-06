package com.mijan.dev.githubprofile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mijan.dev.githubprofile.data.local.entity.UserEntity
import com.mijan.dev.githubprofile.databinding.LayoutUserItemBinding
import com.mijan.dev.githubprofile.utils.loadImageUrl

class UsersPagingDataAdapter : PagingDataAdapter<UserEntity, UsersPagingDataAdapter.ViewHolder>(
    DiffCallback()
) {
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let {
            val isFourthItem = position != 0 && position % 4 == 0
            holder.bind(it, isFourthItem)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            LayoutUserItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    class ViewHolder(private val binding: LayoutUserItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: UserEntity, shouldInvertImage: Boolean) {
            with(binding) {
                imageAvatar.loadImageUrl(
                    url = user.avatarUrl,
                    shouldInvert = shouldInvertImage,
                    placeHolderResId = R.drawable.image_user_placeholder
                )
                textUsername.text = user.username
                textDetails.text = user.url
            }
        }
    }
}

class DiffCallback : DiffUtil.ItemCallback<UserEntity>() {
    override fun areItemsTheSame(oldItem: UserEntity, newItem: UserEntity): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: UserEntity, newItem: UserEntity): Boolean {
        return oldItem == newItem
    }
}