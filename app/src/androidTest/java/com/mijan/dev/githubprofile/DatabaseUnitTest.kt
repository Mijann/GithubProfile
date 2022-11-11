package com.mijan.dev.githubprofile

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.mijan.dev.githubprofile.data.local.AppDatabase
import com.mijan.dev.githubprofile.data.local.dao.UserDao
import com.mijan.dev.githubprofile.data.model.GithubUser
import com.mijan.dev.githubprofile.data.model.toUserEntity
import com.mijan.dev.githubprofile.utils.fromJson
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class DatabaseUnitTest {

    private lateinit var appDatabase: AppDatabase
    private lateinit var userDao: UserDao

    @Before
    fun setupDatabase() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        appDatabase =
            Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
                .allowMainThreadQueries()
                .build()

        userDao = appDatabase.getUserDao()
    }

    @Test
    fun addUsers_thenReturn_expectedUsers() = runTest {
        val githubUsersResponse = fromJson<List<GithubUser>>("github_users_response.json")
        val userEntities = githubUsersResponse.map { it.toUserEntity() }
        userDao.addUsers(userEntities)
        assertEquals(userEntities, userDao.getUsers())
    }

    @Test
    fun addOrUpdateUsers_thenReturn_expectedUsers() = runTest {
        val githubUsersResponse = fromJson<List<GithubUser>>("github_users_response.json")
        val userEntities = githubUsersResponse.map { it.toUserEntity() }
        userDao.addOrUpdateUsers(userEntities)
        assertEquals(userEntities, userDao.getUsers())
    }

    @Test
    fun addUser_thenReturn_expectedUser() = runTest {
        val githubUsersResponse = fromJson<GithubUser>("github_user_mojombo_response.json")
        val userEntity = githubUsersResponse.toUserEntity()
        userDao.addUser(userEntity)
        assertEquals(userEntity, userDao.getUser(userEntity.id))
    }

    @Test
    fun addOrUpdateUser_thenReturn_expectedUser() = runTest {
        val githubUsersResponse = fromJson<GithubUser>("github_user_mojombo_response.json")
        val userEntity = githubUsersResponse.toUserEntity()
        userDao.addOrUpdateUser(userEntity)
        assertEquals(userEntity, userDao.getUser(userEntity.id))
    }

    @Test
    fun getUserFlow_thenReturn_expectedUserFlow() = runTest {
        val githubUserResponse = fromJson<GithubUser>("github_user_mojombo_response.json")
        val userEntity = githubUserResponse.toUserEntity()
        userDao.addUser(userEntity)
        assertEquals(userEntity, userDao.getUserFlow(userEntity.username).first())
    }

    @Test
    fun updateUser_thenReturn_userIsUpdated() = runTest {
        val githubUserResponse = fromJson<GithubUser>("github_user_mojombo_response.json")
        val userEntity = githubUserResponse.toUserEntity()
        userDao.addUser(userEntity)
        val updatedUser = userEntity.copy(company = "Apple")
        userDao.updateUser(updatedUser)
        assertEquals(updatedUser, userDao.getUser(userEntity.id))
    }

    @Test
    fun updateUserNotes_thenReturn_userNotesIsUpdated() = runTest {
        val githubUserResponse = fromJson<GithubUser>("github_user_mojombo_response.json")
        val userEntity = githubUserResponse.toUserEntity()
        userDao.addUser(userEntity)
        userDao.updateUserNotes(userEntity.username, "New notes")
        assertEquals("New notes", userDao.getUserFlow(userEntity.username).first()?.notes)
    }

    @After
    fun closeDatabase() {
        appDatabase.close()
    }
}