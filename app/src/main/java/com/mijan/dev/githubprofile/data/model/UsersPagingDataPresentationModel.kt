package com.mijan.dev.githubprofile.data.model

import com.mijan.dev.githubprofile.data.local.entity.UserEntity

sealed class UsersPagingDataPresentationModel {
    class EmptyPresentationModel(val stringId: Int? = null) :
        UsersPagingDataPresentationModel()

    object NoNetworkPresentationModel : UsersPagingDataPresentationModel()
    data class UserEntityPresentationModel(val userEntity: UserEntity) :
        UsersPagingDataPresentationModel()
}

fun UserEntity.toPresentationModel(): UsersPagingDataPresentationModel =
    UsersPagingDataPresentationModel.UserEntityPresentationModel(this)
