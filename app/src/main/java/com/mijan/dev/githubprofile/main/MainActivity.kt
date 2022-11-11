package com.mijan.dev.githubprofile.main

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.mijan.dev.githubprofile.Constants
import com.mijan.dev.githubprofile.R
import com.mijan.dev.githubprofile.data.model.SnackbarStatus
import com.mijan.dev.githubprofile.databinding.ActivityMainBinding
import com.mijan.dev.githubprofile.main.adapter.LoaderStateAdapter
import com.mijan.dev.githubprofile.profile.ProfileActivity
import com.mijan.dev.githubprofile.utils.showSnackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val viewModel by viewModels<MainViewModel>()

    private val usersAdapter by lazy {
        UsersPagingDataAdapter { username ->
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra(Constants.USERNAME, username)
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val loaderStateAdapter = LoaderStateAdapter()
        binding.recyclerviewUser.adapter = usersAdapter.withLoadStateFooter(loaderStateAdapter)

        lifecycleScope.launch {
            viewModel.users.collectLatest { pagingData ->
                usersAdapter.submitData(pagingData)
            }
        }

        viewModel.errorsFlow
            .flowWithLifecycle(lifecycle)
            .filterNotNull()
            .onEach { appError ->
                binding.root.showSnackbar(
                    SnackbarStatus.ERROR,
                    if (appError.errorResId != null) getString(appError.errorResId) else appError.errorMessage
                )
            }
            .launchIn(lifecycleScope)

        viewModel.loadingFlow
            .flowWithLifecycle(lifecycle)
            .distinctUntilChanged()
            .onEach { isLoading ->
                binding.progressBar.isVisible = isLoading
                binding.recyclerviewUser.isVisible = !isLoading
            }
            .launchIn(lifecycleScope)

        viewModel.isNetworkConnectionAvailable
            .flowWithLifecycle(lifecycle)
            .filterNotNull()
            .distinctUntilChanged()
            .onEach { isNetworkAvailable ->
                binding.root.showSnackbar(
                    if (!isNetworkAvailable) SnackbarStatus.ERROR else SnackbarStatus.SUCCESS,
                    getString(if (isNetworkAvailable) R.string.network_connection_restored else R.string.network_error_no_connection)
                )
            }
            .launchIn(lifecycleScope)

        binding.editSearch
            .addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(char: CharSequence?, p1: Int, p2: Int, p3: Int) =
                    Unit

                override fun onTextChanged(char: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

                override fun afterTextChanged(editable: Editable?) {
                    viewModel.setSearch(editable.toString())
                }

            })

    }
}