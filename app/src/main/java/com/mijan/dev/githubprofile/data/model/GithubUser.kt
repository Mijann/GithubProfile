package com.mijan.dev.githubprofile.data.model

import com.google.gson.annotations.SerializedName
import com.mijan.dev.githubprofile.data.local.entity.UserEntity

data class GithubUser(
    @SerializedName("id") val id: Int,
    @SerializedName("login") val username: String,
    @SerializedName("avatar_url") val avatarUrl: String,
    @SerializedName("url") val url: String,
)

fun GithubUser.toUserEntity() = UserEntity(
    id = this.id,
    username = this.username,
    avatarUrl = this.avatarUrl,
    url = this.url
)