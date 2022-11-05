package com.mijan.dev.githubprofile.data.remote.api

import com.mijan.dev.githubprofile.data.model.GithubUser
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GithubApi {

    @GET("users")
    suspend fun getUsers(@Query("since") int: Int? = 0): List<GithubUser>

    @GET("users/{username}")
    suspend fun getUser(@Path("username") username: String): GithubUser

}