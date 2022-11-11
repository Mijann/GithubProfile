package com.mijan.dev.githubprofile

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.mijan.dev.githubprofile.data.model.GithubUser
import com.mijan.dev.githubprofile.data.model.Resource
import com.mijan.dev.githubprofile.data.remote.repo.GithubRepo
import com.mijan.dev.githubprofile.main.MainViewModel
import com.mijan.dev.githubprofile.manager.NetworkConnectionManager
import com.mijan.dev.githubprofile.manager.SharedPreferenceManager
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
import org.mockito.kotlin.verify
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalTime
class MainViewModelUnitTest : BaseUnitTest() {

    @Mock
    private lateinit var githubRepo: GithubRepo

    @Mock
    private lateinit var sharedPreferenceManager: SharedPreferenceManager

    @Mock
    private lateinit var networkConnectionManager: NetworkConnectionManager

    private lateinit var viewModel: MainViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    private fun setupViewModel(hasNetworkConnection: Boolean = true) {
        sharedPreferenceManager.stub {
            on { getInt(Constants.PAGE_SIZE_KEY) } doReturn 0
        }
        networkConnectionManager.stub {
            on { isNetworkAvailable } doReturn MutableStateFlow(hasNetworkConnection)
        }
        val githubUsersResponse = fromJson<List<GithubUser>>("github_users_response.json")
        githubRepo.stub {
            on { getUsers() } doReturn mockFlow { Resource.Success(githubUsersResponse) }
        }

        viewModel = MainViewModel(githubRepo, networkConnectionManager, sharedPreferenceManager)

    }

    @Test
    fun `given github repo returns list of user, when init, then getUsers is invoked`() =
        runTest {
            // given
            setupViewModel()

            // then
            verify(githubRepo).getUsers()
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