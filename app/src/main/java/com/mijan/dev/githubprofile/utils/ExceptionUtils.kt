package com.mijan.dev.githubprofile.utils

import com.mijan.dev.githubprofile.R
import com.mijan.dev.githubprofile.data.model.AppError
import java.net.ConnectException
import java.net.UnknownHostException

fun Exception.toAppError(): AppError {
    return when (this) {
        is UnknownHostException -> AppError(errorResId = R.string.network_error_no_connection)
        is ConnectException -> AppError(errorResId = R.string.network_error_no_connection)
        else -> AppError(errorMessage = message.orEmpty())
    }
}