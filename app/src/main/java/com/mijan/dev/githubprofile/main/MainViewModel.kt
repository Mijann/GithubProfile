package com.mijan.dev.githubprofile.main

import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.map
import com.mijan.dev.githubprofile.Constants
import com.mijan.dev.githubprofile.R
import com.mijan.dev.githubprofile.base.BaseViewModel
import com.mijan.dev.githubprofile.base.callApi
import com.mijan.dev.githubprofile.data.model.UsersPagingDataPresentationModel
import com.mijan.dev.githubprofile.data.model.toPresentationModel
import com.mijan.dev.githubprofile.data.remote.repo.GithubRepo
import com.mijan.dev.githubprofile.manager.NetworkConnectionManager
import com.mijan.dev.githubprofile.manager.SharedPreferenceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val githubRepo: GithubRepo,
    networkConnectionManager: NetworkConnectionManager,
    private val sharedPreferenceManager: SharedPreferenceManager,
) : BaseViewModel() {

    private val search = MutableStateFlow("")
    private val pageSize = MutableStateFlow(0)
    private val initialPageSize = sharedPreferenceManager.getInt(Constants.PAGE_SIZE_KEY)
    private val isPreviousNoNetwork = MutableStateFlow(false)
    private val _isNetworkConnectionAvailable = networkConnectionManager.isNetworkAvailable
    private lateinit var usersPagingData: Flow<PagingData<UsersPagingDataPresentationModel>>

    init {
        if (initialPageSize > 0) {
            pageSize.value = initialPageSize
        }
        getUsers()
    }

    private fun getUsers() {
        callApi(githubRepo.getUsers(), onSuccess = { response ->
            pageSize.value = response.size
            sharedPreferenceManager.putInt(Constants.PAGE_SIZE_KEY, response.size)
        })
    }

    val isNetworkConnectionAvailable = combine(
        _isNetworkConnectionAvailable,
        isPreviousNoNetwork,
        pageSize,
    ) { isNetworkAvailable, isNoNetworkBefore, pageSize ->

        return@combine when {
            isNetworkAvailable == false -> {
                isPreviousNoNetwork.value = true
                false
            }
            isNetworkAvailable == true && isNoNetworkBefore -> {
                isPreviousNoNetwork.value = false
                if (pageSize == 0) getUsers()
                true
            }
            else -> null
        }
    }

    @OptIn(ExperimentalPagingApi::class, ExperimentalCoroutinesApi::class)
    val users = combine(
        search,
        pageSize,
        _isNetworkConnectionAvailable,
        ::Triple
    ).distinctUntilChanged()
        .flatMapLatest { (search, pageSize, isNetworkAvailable) ->
            when {
                search.isNotBlank() -> {
                    emitLoading(true)
                    val searchUsers = githubRepo.getCacheUsers()
                        .filter { userEntity ->
                            userEntity.username.lowercase()
                                .contains(
                                    search.lowercase().toRegex()
                                ) || userEntity.notes?.lowercase()
                                ?.contains(search.lowercase().toRegex()) == true
                        }
                    emitLoading(false)
                    if (searchUsers.isNotEmpty()) return@flatMapLatest flowOf(
                        PagingData.from(
                            searchUsers.map { it.toPresentationModel() })
                    )
                    return@flatMapLatest flowOf(
                        PagingData.from(
                            listOf(
                                UsersPagingDataPresentationModel.EmptyPresentationModel(R.string.search_no_user_found) as UsersPagingDataPresentationModel
                            )
                        )
                    )
                }
                pageSize > 0 -> {
                    if (!::usersPagingData.isInitialized) {
                        usersPagingData = githubRepo.getUsersPagingDataFlow(pageSize)
                            .map { pagingData ->
                                pagingData.map { it.toPresentationModel() }
                            }
                    }
                    return@flatMapLatest usersPagingData
                }
                isNetworkAvailable == false -> {
                    return@flatMapLatest flowOf(
                        PagingData.from(
                            listOf(
                                UsersPagingDataPresentationModel.NoNetworkPresentationModel as UsersPagingDataPresentationModel
                            )
                        )
                    )
                }
                else -> {
                    return@flatMapLatest flowOf(
                        PagingData.from(
                            listOf(
                                UsersPagingDataPresentationModel.EmptyPresentationModel(R.string.no_data) as UsersPagingDataPresentationModel
                            )
                        )
                    )
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
            initialValue = PagingData.from(
                listOf(
                    UsersPagingDataPresentationModel.EmptyPresentationModel(R.string.no_data) as UsersPagingDataPresentationModel
                )
            )
        )

    fun setSearch(search: String) {
        this.search.value = search
    }
}