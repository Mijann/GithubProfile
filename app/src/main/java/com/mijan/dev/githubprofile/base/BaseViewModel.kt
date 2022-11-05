package com.mijan.dev.githubprofile.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mijan.dev.githubprofile.data.model.AppError
import com.mijan.dev.githubprofile.data.model.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

open class BaseViewModel : ViewModel() {

    private val _errorFlow = MutableSharedFlow<AppError>()
    private val _loadingFlow = MutableSharedFlow<Boolean>()
    val errorsFlow = _errorFlow.asSharedFlow()
    val loadingFlow = _loadingFlow.asSharedFlow()

    fun emitError(error: AppError) {
        viewModelScope.launch {
            _errorFlow.emit(error)
        }
    }

    fun emitLoading(showLoading: Boolean) {
        viewModelScope.launch {
            _loadingFlow.emit(showLoading)
        }
    }
}

fun <T> BaseViewModel.callApi(
    flow: Flow<Resource<T>>,
    onSuccess: (data: T) -> Unit,
    onFailure: ((error: AppError) -> Unit)? = null,
    showLoading: Boolean = true,
) {
    viewModelScope.launch {
        emitLoading(showLoading)
        flow.collect {
            emitLoading(false)
            when (it) {
                is Resource.Success -> {
                    onSuccess.invoke(it.data)
                }
                is Resource.Failure -> {
                    if (onFailure == null) {
                        emitError(it.appError)
                    } else {
                        onFailure.invoke(it.appError)
                    }
                }
            }
        }
    }
}