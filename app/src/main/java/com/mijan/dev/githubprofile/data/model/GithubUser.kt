package com.mijan.dev.githubprofile.data.model

import com.google.gson.annotations.SerializedName

data class GithubUser(
    @SerializedName("id") val id: Int,
    @SerializedName("login") val username: String,
    @SerializedName("avatar_url") val avatarUrl: String,
    @SerializedName("url") val url: String,
)