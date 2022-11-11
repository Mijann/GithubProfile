package com.mijan.dev.githubprofile.data.local.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.mijan.dev.githubprofile.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUsers(entities: List<UserEntity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUser(entity: UserEntity): Long

    @Transaction
    suspend fun addOrUpdateUsers(entities: List<UserEntity>) {
        entities.forEach { userEntity ->
            addOrUpdateUser(userEntity)
        }
    }

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    fun getUser(id: Int): UserEntity?

    @Update
    fun updateUser(entity: UserEntity)

    @Transaction
    suspend fun addOrUpdateUser(entity: UserEntity) {
        val user = getUser(entity.id)
        if (user != null) {
            updateUser(
                user.copy(
                    username = entity.username,
                    name = if (!entity.name.isNullOrBlank()) entity.name else user.name,
                    avatarUrl = entity.avatarUrl,
                    company = if (!entity.company.isNullOrBlank()) entity.company else user.company,
                    blog = if (!entity.blog.isNullOrBlank()) entity.blog else user.blog,
                    followers = entity.followers ?: user.followers,
                    following = entity.following ?: user.following,
                )
            )
        } else {
            addUser(entity)
        }
    }

    @Query("UPDATE users SET notes = :notes WHERE username = :username")
    fun updateUserNotes(username: String, notes: String)

    @Query("SELECT * FROM users WHERE username = :username")
    fun getUserFlow(username: String): Flow<UserEntity?>

    @Query("SELECT * FROM users")
    suspend fun getUsers(): List<UserEntity>

    @Query("SELECT * FROM users")
    fun getUsersPagingSource(): PagingSource<Int, UserEntity>
}