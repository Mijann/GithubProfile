package com.mijan.dev.githubprofile.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mijan.dev.githubprofile.data.model.UsersPagingDataPresentationModel
import com.mijan.dev.githubprofile.databinding.LayoutEmptyBinding
import com.mijan.dev.githubprofile.databinding.LayoutNoNetworkBinding
import com.mijan.dev.githubprofile.databinding.LayoutUserItemBinding
import com.mijan.dev.githubprofile.main.viewholder.EmptyViewHolder
import com.mijan.dev.githubprofile.main.viewholder.NoNetworkViewHolder
import com.mijan.dev.githubprofile.main.viewholder.UserEntityViewHolder

class UsersPagingDataAdapter(
    private val onItemClick: (username: String) -> Unit
) : PagingDataAdapter<UsersPagingDataPresentationModel, UsersPagingDataAdapter.BaseUsersPagingDataViewHolder>(
    DiffCallback()
) {

    override fun onBindViewHolder(holder: BaseUsersPagingDataViewHolder, position: Int) {
        when (holder) {
            is UserEntityViewHolder -> {
                getItem(position)?.let {
                    holder.bind(it, position, onItemClick)
                }
            }
            else -> getItem(position)?.let { holder.bind(it) }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseUsersPagingDataViewHolder {
        return when (viewType) {
            0 -> UserEntityViewHolder(
                LayoutUserItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            1 -> NoNetworkViewHolder(
                LayoutNoNetworkBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            else -> EmptyViewHolder(
                LayoutEmptyBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        getItem(position)?.let { presentationModel ->
            when (presentationModel) {
                is UsersPagingDataPresentationModel.UserEntityPresentationModel -> return 0
                is UsersPagingDataPresentationModel.NoNetworkPresentationModel -> return 1
                else -> return 2
            }
        }
        return 2
    }

    abstract class BaseUsersPagingDataViewHolder(
        itemView: View,
        position: Int? = null,
        onItemClick: ((username: String) -> Unit)? = null
    ) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(
            item: UsersPagingDataPresentationModel,
            position: Int? = null,
            onItemClick: ((username: String) -> Unit)? = null
        )
    }
}

class DiffCallback : DiffUtil.ItemCallback<UsersPagingDataPresentationModel>() {
    override fun areItemsTheSame(
        oldItem: UsersPagingDataPresentationModel,
        newItem: UsersPagingDataPresentationModel
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: UsersPagingDataPresentationModel,
        newItem: UsersPagingDataPresentationModel
    ): Boolean {
        return oldItem == newItem
    }
}