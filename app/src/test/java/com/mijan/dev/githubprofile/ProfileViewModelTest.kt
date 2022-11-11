package com.mijan.dev.githubprofile

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.mijan.dev.githubprofile.data.local.entity.UserEntity
import com.mijan.dev.githubprofile.data.model.AppError
import com.mijan.dev.githubprofile.data.model.GithubUser
import com.mijan.dev.githubprofile.data.model.Resource
import com.mijan.dev.githubprofile.data.remote.repo.GithubRepo
import com.mijan.dev.githubprofile.manager.NetworkConnectionManager
import com.mijan.dev.githubprofile.profile.ProfileViewModel
import com.mijan.dev.githubprofile.utils.fromJson
import com.mijan.dev.githubprofile.utils.mockFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.stub
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalTime
class ProfileViewModelTest : BaseUnitTest() {

    @Mock
    private lateinit var githubRepo: GithubRepo

    private lateinit var viewModel: ProfileViewModel

    @Mock
    private lateinit var handle: SavedStateHandle

    @Mock
    private lateinit var networkConnectionManager: NetworkConnectionManager

    private val githubUserResponse = fromJson<GithubUser>("github_user_mojombo_response.json")
    private val cacheUser = UserEntity(1, "mojombo", "", "", notes = "Notes")

    private fun setupViewModel(isError: Boolean = false, hasNetworkConnection: Boolean = true) {
        handle.stub {
            on { get<String>(Constants.USERNAME) } doReturn "mojombo"
        }
        githubRepo.stub {
            on { getCacheUser("mojombo") } doReturn mockFlow { cacheUser }
            on { getUser("mojombo") } doReturn mockFlow {
                if (isError) {
                    Resource.Failure(AppError("Something went wrong"))
                } else {
                    Resource.Success(githubUserResponse)
                }
            }
        }
        networkConnectionManager.stub {
            on { isNetworkAvailable } doReturn MutableStateFlow(hasNetworkConnection)
        }

        viewModel = ProfileViewModel(githubRepo, handle, networkConnectionManager)
    }

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `when setNotes, then notes is updated`() = runTest {
        // given
        setupViewModel()

        // when
        viewModel.setNotes("New notes")

        // then
        val expected = "New notes"
        viewModel.notes.test {
            assertThat(expectMostRecentItem()).isEqualTo(expected)
        }
    }

    @Test
    fun `given githubRepo returns github user and cache user, when init, then returns expected user and notes`() =
        runTest {
            // given
            setupViewModel()

            // then
            val expectedUser = cacheUser.copy(
                name = githubUserResponse.name,
                company = githubUserResponse.company,
                blog = githubUserResponse.blog,
                avatarUrl = githubUserResponse.avatarUrl,
                following = githubUserResponse.following,
                followers = githubUserResponse.followers,
            )
            viewModel.user.test {
                assertThat(expectMostRecentItem()).isEqualTo(expectedUser)
            }
            viewModel.notes.test {
                assertThat(expectMostRecentItem()).isEqualTo(expectedUser.notes)
            }
        }

    @Test
    fun `given githubRepo returns only cache user, when init, then returns cache user`() =
        runTest {
            // given
            setupViewModel(isError = true)

            // then
            viewModel.user.test {
                assertThat(expectMostRecentItem()).isEqualTo(cacheUser)
            }
        }

    @Test
    fun `test network connection availability`() =
        runTest {
            // given
            setupViewModel(hasNetworkConnection = false)


            // then
            viewModel.isNetworkConnectionAvailable.test {
                assertThat(expectMostRecentItem()).isFalse()
            }

            // given
            setupViewModel(hasNetworkConnection = true)


            // then
            viewModel.isNetworkConnectionAvailable.test {
                assertThat(expectMostRecentItem()).isNull()
            }
        }
}