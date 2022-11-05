package com.mijan.dev.githubprofile.utils

import com.mijan.dev.githubprofile.data.model.AppError

fun Exception.toAppError() = AppError(errorMessage = message.orEmpty())