package com.mijan.dev.githubprofile.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.mijan.dev.githubprofile.data.local.AppDatabase
import com.mijan.dev.githubprofile.data.local.dao.UserDao
import com.mijan.dev.githubprofile.data.local.entity.UserEntity
import com.mijan.dev.githubprofile.data.model.toUserEntity
import com.mijan.dev.githubprofile.data.remote.api.GithubApi
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class UsersRemoteMediator(
    private val database: AppDatabase,
    private val userDao: UserDao,
    private val networkService: GithubApi,
) : RemoteMediator<Int, UserEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, UserEntity>
    ): MediatorResult {
        return try {
            val loadKey = when (loadType) {
                LoadType.REFRESH -> 0
                LoadType.PREPEND ->
                    return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                        ?: return MediatorResult.Success(
                            endOfPaginationReached = true
                        )
                    lastItem.id
                }
                else -> 0
            }

            val response = networkService.getUsers(loadKey)

            database.withTransaction {
                userDao.addUsers(response.map { it.toUserEntity() })
            }

            MediatorResult.Success(response.isEmpty())
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }
}