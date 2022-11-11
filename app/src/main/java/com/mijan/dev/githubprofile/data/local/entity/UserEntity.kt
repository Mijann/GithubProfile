package com.mijan.dev.githubprofile.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey (autoGenerate = true)
    @SerializedName("id") val id: Int,
    @SerializedName("username") val username: String,
    @SerializedName("avatarUrl") val avatarUrl: String,
    @SerializedName("url") val url: String,
    @SerializedName("name") val name: String? = "",
    @SerializedName("company") val company: String? = "",
    @SerializedName("blog") val blog: String? = "",
    @SerializedName("followers") val followers: Int? = 0,
    @SerializedName("following") val following: Int? = 0,
    @SerializedName("notes") val notes: String? = "",
) {
    val hasNotes get() = !notes.isNullOrBlank()
}