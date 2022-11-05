package com.mijan.dev.githubprofile

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.mijan.dev.githubprofile.data.model.AppError
import com.mijan.dev.githubprofile.data.model.GithubUser
import com.mijan.dev.githubprofile.data.model.Resource
import com.mijan.dev.githubprofile.data.remote.repo.GithubRepo
import com.mijan.dev.githubprofile.utils.fromJson
import com.mijan.dev.githubprofile.utils.mockFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.stub
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalTime
class MainViewModelUnitTest : BaseUnitTest() {

    @Mock
    private lateinit var githubRepo: GithubRepo

    @InjectMocks
    private lateinit var viewModel: MainViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `given github repo returns list of user, when getUsers, then return expected list of user`() =
        runTest {
            // given
            val githubUsersResponse = fromJson<List<GithubUser>>("github_users_response.json")
            githubRepo.stub {
                onBlocking { getUsers() } doReturn mockFlow { Resource.Success(githubUsersResponse) }
            }

            // when
            viewModel.getUsers()

            // then
            val expectedUsers = githubUsersResponse
            viewModel.users.test {
                val users = expectMostRecentItem()
                assertThat(users).isEqualTo(expectedUsers)
                assertThat(users.size).isEqualTo(expectedUsers.size)
            }
        }

    @Test
    fun `given github repo returns something went wrong error, when getUsers, then return expected error`() =
        runTest {
            // given
            githubRepo.stub {
                onBlocking { getUsers() } doReturn mockFlow { Resource.Failure(AppError("Something went wrong")) }
            }

            viewModel.errorsFlow.test {
                // when
                viewModel.getUsers()

                // then
                viewModel.users.test {
                    assertThat(expectMostRecentItem()).isEmpty()
                }

                val expectedError = AppError("Something went wrong")
                assertThat(expectMostRecentItem()).isEqualTo(expectedError)
            }
        }
}