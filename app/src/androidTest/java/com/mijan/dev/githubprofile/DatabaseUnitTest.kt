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
        assertEquals(userEntities, userDao.getUsers().first())
    }

    @After
    fun closeDatabase() {
        appDatabase.close()
    }
}