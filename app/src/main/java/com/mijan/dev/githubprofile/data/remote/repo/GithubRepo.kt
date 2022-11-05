package com.mijan.dev.githubprofile.data.remote.repo

import com.mijan.dev.githubprofile.data.model.Resource
import com.mijan.dev.githubprofile.data.remote.api.GithubApi
import com.mijan.dev.githubprofile.utils.toAppError
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GithubRepo @Inject constructor(
    private val githubApi: GithubApi
) {
    fun getUsers() = flow {
        try {
            emit(Resource.Success(githubApi.getUsers()))
        } catch (e: Exception) {
            emit(Resource.Failure(e.toAppError()))
        }
    }

    fun getUser(username: String) = flow {
        try {
            emit(Resource.Success(githubApi.getUser(username)))
        } catch (e: Exception) {
            emit(Resource.Failure(e.toAppError()))
        }
    }
}