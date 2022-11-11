package com.mijan.dev.githubprofile.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.mijan.dev.githubprofile.Constants
import com.mijan.dev.githubprofile.base.BaseViewModel
import com.mijan.dev.githubprofile.base.callApi
import com.mijan.dev.githubprofile.data.model.GithubUser
import com.mijan.dev.githubprofile.data.model.SnackbarMessage
import com.mijan.dev.githubprofile.data.model.SnackbarStatus
import com.mijan.dev.githubprofile.data.remote.repo.GithubRepo
import com.mijan.dev.githubprofile.manager.NetworkConnectionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val githubRepo: GithubRepo,
    savedStateHandle: SavedStateHandle,
    networkConnectionManager: NetworkConnectionManager
) : BaseViewModel() {
    private val username = savedStateHandle.get<String>(Constants.USERNAME).orEmpty()
    private val cacheUserFlow = githubRepo.getCacheUser(username)
    private val githubUser = MutableStateFlow<GithubUser?>(null)
    private val _notes = MutableStateFlow<String?>(null)
    val notes = _notes.asStateFlow()
    private val _snackbarMessage = MutableSharedFlow<SnackbarMessage>()
    val snackbarMessage = _snackbarMessage.asSharedFlow()
    private val isPreviousNoNetwork = MutableStateFlow(false)
    private val _isNetworkConnectionAvailable = networkConnectionManager.isNetworkAvailable

    init {
        getUser()
    }

    val isNetworkConnectionAvailable = combine(
        _isNetworkConnectionAvailable,
        isPreviousNoNetwork,
        githubUser
    ) { isNetworkAvailable, isNoNetworkBefore, githubUser ->

        return@combine when {
            isNetworkAvailable == false -> {
                isPreviousNoNetwork.value = true
                false
            }
            isNetworkAvailable == true && isNoNetworkBefore -> {
                isPreviousNoNetwork.value = false
                if (githubUser == null) getUser()
                true
            }
            else -> null
        }
    }

    val user = combine(
        cacheUserFlow,
        githubUser,
        _notes,
    ) { cacheUser, githubUser, notesValue ->
        val updatedUser = when {
            githubUser != null -> cacheUser?.copy(
                name = githubUser.name,
                company = githubUser.company,
                blog = githubUser.blog,
                avatarUrl = githubUser.avatarUrl,
                following = githubUser.following,
                followers = githubUser.followers
            )
            else -> cacheUser
        }
        if (notesValue == null) {
            _notes.value = updatedUser?.notes.orEmpty()
        }
        updatedUser
    }.distinctUntilChanged()

    private fun getUser() {
        callApi(githubRepo.getUser(username), onSuccess = { response ->
            githubUser.value = response
        })
    }

    fun setNotes(notes: String) {
        _notes.value = notes
    }

    fun saveNotes() {
        callApi(
            githubRepo.updateUserNotes(username, notes.value.orEmpty()),
            onSuccess = { message ->
                viewModelScope.launch {
                    _snackbarMessage.emit(
                        SnackbarMessage(
                            message = message,
                            status = SnackbarStatus.SUCCESS
                        )
                    )
                }
            })
    }
}