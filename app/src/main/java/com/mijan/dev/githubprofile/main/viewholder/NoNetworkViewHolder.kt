package com.mijan.dev.githubprofile.main.viewholder

import com.mijan.dev.githubprofile.data.model.UsersPagingDataPresentationModel
import com.mijan.dev.githubprofile.databinding.LayoutNoNetworkBinding
import com.mijan.dev.githubprofile.main.UsersPagingDataAdapter

class NoNetworkViewHolder(private val binding: LayoutNoNetworkBinding) :
    UsersPagingDataAdapter.BaseUsersPagingDataViewHolder(binding.root) {
    override fun bind(
        item: UsersPagingDataPresentationModel,
        position: Int?,
        onItemClick: ((username: String) -> Unit)?
    ) {
        if (item !is UsersPagingDataPresentationModel.NoNetworkPresentationModel) return

    }
}