package com.mijan.dev.githubprofile.data.remote.repo

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.mijan.dev.githubprofile.data.UsersRemoteMediator
import com.mijan.dev.githubprofile.data.local.AppDatabase
import com.mijan.dev.githubprofile.data.local.dao.UserDao
import com.mijan.dev.githubprofile.data.model.Resource
import com.mijan.dev.githubprofile.data.model.toUserEntity
import com.mijan.dev.githubprofile.data.remote.api.GithubApi
import com.mijan.dev.githubprofile.utils.toAppError
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GithubRepo @Inject constructor(
    private val githubApi: GithubApi,
    private val database: AppDatabase,
    private val userDao: UserDao,
) {
    @ExperimentalPagingApi
    fun getUsersPagingDataFlow(pageSize: Int) = Pager(
        config = PagingConfig(pageSize),
        remoteMediator = UsersRemoteMediator(database = database, userDao, githubApi)
    ) {
        userDao.getUsersPagingSource()
    }.flow

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