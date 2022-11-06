package com.mijan.dev.githubprofile.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    @SerializedName("id") val id: Int,
    @SerializedName("username") val username: String,
    @SerializedName("avatarUrl") val avatarUrl: String,
    @SerializedName("url") val url: String,
)