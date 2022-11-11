package com.mijan.dev.githubprofile.data.model

import com.google.gson.annotations.SerializedName
import com.mijan.dev.githubprofile.data.local.entity.UserEntity

data class GithubUser(
    @SerializedName("id") val id: Int,
    @SerializedName("login") val username: String,
    @SerializedName("avatar_url") val avatarUrl: String,
    @SerializedName("url") val url: String,
    @SerializedName("name") val name: String? = "",
    @SerializedName("company") val company: String? = "",
    @SerializedName("blog") val blog: String? = "",
    @SerializedName("followers") val followers: Int? = 0,
    @SerializedName("following") val following: Int? = 0,
)

fun GithubUser.toUserEntity() = UserEntity(
    id = this.id,
    username = this.username,
    avatarUrl = this.avatarUrl,
    url = this.url,
    name = this.name,
    company = this.company,
    blog = this.blog,
    followers = this.followers,
    following = this.following,
)