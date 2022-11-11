package com.mijan.dev.githubprofile.data.model

data class SnackbarMessage(
    val stringId: Int? = null,
    val message: String,
    val status: SnackbarStatus
)

enum class SnackbarStatus {
    SUCCESS, ERROR
}