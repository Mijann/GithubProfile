package com.mijan.dev.githubprofile

import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.cachedIn
import com.mijan.dev.githubprofile.base.BaseViewModel
import com.mijan.dev.githubprofile.base.callApi
import com.mijan.dev.githubprofile.data.model.GithubUser
import com.mijan.dev.githubprofile.data.remote.repo.GithubRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val githubRepo: GithubRepo
) : BaseViewModel() {

    private val _users = MutableStateFlow<List<GithubUser>>(emptyList())
    val users = _users.asStateFlow()

    @OptIn(ExperimentalPagingApi::class)
    val usersPagingData
        get() = githubRepo.getUsersPagingDataFlow(30)
            .cachedIn(viewModelScope)

    fun getUsers() {
        callApi(githubRepo.getUsers(), onSuccess = { response ->
            _users.value = response
        })
    }
}