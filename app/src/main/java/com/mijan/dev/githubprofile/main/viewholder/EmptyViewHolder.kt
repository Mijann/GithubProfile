package com.mijan.dev.githubprofile.main.viewholder

import com.mijan.dev.githubprofile.data.model.UsersPagingDataPresentationModel
import com.mijan.dev.githubprofile.databinding.LayoutEmptyBinding
import com.mijan.dev.githubprofile.main.UsersPagingDataAdapter

class EmptyViewHolder(private val binding: LayoutEmptyBinding) :
    UsersPagingDataAdapter.BaseUsersPagingDataViewHolder(binding.root) {
    override fun bind(
        item: UsersPagingDataPresentationModel,
        position: Int?,
        onItemClick: ((username: String) -> Unit)?
    ) {
        if (item !is UsersPagingDataPresentationModel.EmptyPresentationModel) return

        binding.textView.apply {
            item.stringId?.let {
                text = this.context.getString(it)
            }
        }
    }
}