package com.mijan.dev.githubprofile

import androidx.paging.*
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.mijan.dev.githubprofile.data.UsersRemoteMediator
import com.mijan.dev.githubprofile.data.local.AppDatabase
import com.mijan.dev.githubprofile.data.local.dao.UserDao
import com.mijan.dev.githubprofile.data.local.entity.UserEntity
import com.mijan.dev.githubprofile.utils.fromJson
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class UsersRemoteMediatorUnitTest {

    private lateinit var appDatabase: AppDatabase
    private lateinit var userDao: UserDao
    private lateinit var usersRemoteMediator: UsersRemoteMediator
    private val githubApi = TestGithubApi()

    @Before
    fun setup() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        appDatabase =
            Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
                .allowMainThreadQueries()
                .build()

        userDao = appDatabase.getUserDao()
        usersRemoteMediator = UsersRemoteMediator(appDatabase, userDao, githubApi)
    }

    @ExperimentalPagingApi
    @Test
    fun given_moreDataIsPresent_when_refreshLoad_thenReturn_successResult() = runTest {
        githubApi.addUsers(fromJson("github_users_response.json"))
        val pagingState = PagingState<Int, UserEntity>(listOf(), null, PagingConfig(10), 10)
        val result = usersRemoteMediator.load(LoadType.REFRESH, pagingState)
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @ExperimentalPagingApi
    @Test
    fun given_noMoreData_when_refreshLoad_thenReturn_successResultAndEndOfPagination() =
        runTest {
            val pagingState = PagingState<Int, UserEntity>(listOf(), null, PagingConfig(10), 10)
            val result = usersRemoteMediator.load(LoadType.REFRESH, pagingState)
            assertTrue(result is RemoteMediator.MediatorResult.Success)
            assertTrue((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
        }

    @ExperimentalPagingApi
    @Test
    fun given_errorOccurs_when_refreshLoad_thenReturn_errorResult() = runTest {
        // Set up failure message to throw exception from the mock API.
        githubApi.failureMsg = "Throw test failure"
        val pagingState = PagingState<Int, UserEntity>(listOf(), null, PagingConfig(10), 10)
        val result = usersRemoteMediator.load(LoadType.REFRESH, pagingState)
        assertTrue(result is RemoteMediator.MediatorResult.Error)
    }

    @After
    fun teardown() {
        appDatabase.close()
        githubApi.clear()
    }
}