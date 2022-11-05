package com.mijan.dev.githubprofile.data.model

sealed class Resource<T> {
    class Success<T>(val data: T) : Resource<T>()
    class Failure<T>(val appError: AppError) : Resource<T>()
}