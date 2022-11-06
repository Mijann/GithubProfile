package com.mijan.dev.githubprofile

import com.mijan.dev.githubprofile.data.model.GithubUser
import com.mijan.dev.githubprofile.data.remote.api.GithubApi
import java.io.IOException

class TestGithubApi : GithubApi {
    private val githubUsers = arrayListOf<GithubUser>()
    var failureMsg: String? = null

    fun addUsers(users: List<GithubUser>) {
        githubUsers.addAll(users)
    }

    fun clear() {
        githubUsers.clear()
        failureMsg = null
    }

    override suspend fun getUsers(int: Int?): List<GithubUser> {
        failureMsg?.let {
            throw IOException(it)
        }
        return githubUsers
    }

    override suspend fun getUser(username: String): GithubUser {
        return GithubUser(1, "", "", "")
    }
}