package com.mijan.dev.githubprofile.data.remote.repo

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.room.withTransaction
import com.mijan.dev.githubprofile.data.UsersRemoteMediator
import com.mijan.dev.githubprofile.data.local.AppDatabase
import com.mijan.dev.githubprofile.data.local.dao.UserDao
import com.mijan.dev.githubprofile.data.model.GithubUser
import com.mijan.dev.githubprofile.data.model.Resource
import com.mijan.dev.githubprofile.data.model.toUserEntity
import com.mijan.dev.githubprofile.data.remote.api.GithubApi
import com.mijan.dev.githubprofile.utils.toAppError
import kotlinx.coroutines.flow.distinctUntilChanged
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

    suspend fun getCacheUsers() = userDao.getUsers()

    fun getUsers() = flow {
        try {
            val users = githubApi.getUsers()
            database.withTransaction {
                userDao.addOrUpdateUsers(users.map(GithubUser::toUserEntity))
            }
            emit(Resource.Success(githubApi.getUsers()))
        } catch (e: Exception) {
            emit(Resource.Failure(e.toAppError()))
        }
    }

    fun getUser(username: String) = flow {
        try {
            val user = githubApi.getUser(username)
            database.withTransaction {
                userDao.addOrUpdateUser(user.toUserEntity())
            }
            emit(Resource.Success(user))
        } catch (e: Exception) {
            emit(Resource.Failure(e.toAppError()))
        }
    }

    fun getCacheUser(username: String) = userDao.getUserFlow(username).distinctUntilChanged()

    fun updateUserNotes(username: String, notes: String) = flow {
        try {
            database.withTransaction {
                userDao.updateUserNotes(username, notes)
            }
            emit(Resource.Success("Notes saved success"))
        } catch (e: Exception) {
            emit(Resource.Failure(e.toAppError()))
        }
    }
}