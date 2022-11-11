package com.mijan.dev.githubprofile.profile

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.mijan.dev.githubprofile.R
import com.mijan.dev.githubprofile.data.local.entity.UserEntity
import com.mijan.dev.githubprofile.data.model.AppError
import com.mijan.dev.githubprofile.data.model.SnackbarStatus
import com.mijan.dev.githubprofile.databinding.ActivityProfileBinding
import com.mijan.dev.githubprofile.profile.ui.theme.GithubProfileTheme
import com.mijan.dev.githubprofile.profile.ui.theme.Typography
import com.mijan.dev.githubprofile.profile.ui.theme.myColors
import com.mijan.dev.githubprofile.utils.showSnackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class ProfileActivity : ComponentActivity() {
    private lateinit var binding: ActivityProfileBinding

    private val viewModel by viewModels<ProfileViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        binding.composeView.setContent {
            GithubProfileTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val user = viewModel.user.collectAsState(null).value
                    val notes = viewModel.notes.collectAsState().value
                    ProfileApp(
                        user,
                        notes.orEmpty(),
                        onValueChange = { newValue ->
                            viewModel.setNotes(newValue)
                        }, onSave = {
                            viewModel.saveNotes()
                        }) {
                        this.onBackPressed()
                    }
                }
            }
        }
        setContentView(binding.root)

        viewModel.snackbarMessage
            .flowWithLifecycle(lifecycle)
            .filterNotNull()
            .onEach { snackbar ->
                binding.root.showSnackbar(
                    snackbar.status,
                    if (snackbar.stringId != null) getString(snackbar.stringId) else snackbar.message
                )
            }
            .launchIn(lifecycleScope)

        viewModel.errorsFlow
            .flowWithLifecycle(lifecycle)
            .filterNotNull()
            .distinctUntilChanged()
            .onEach { appError ->
                binding.root.showSnackbar(
                    SnackbarStatus.ERROR,
                    if (appError.errorResId != null) getString(appError.errorResId) else appError.errorMessage
                )
            }
            .launchIn(lifecycleScope)

        viewModel.isNetworkConnectionAvailable
            .flowWithLifecycle(lifecycle)
            .filterNotNull()
            .distinctUntilChanged()
            .onEach { isAvailable ->
                when {
                    isAvailable -> {
                        binding.root.showSnackbar(message = getString(R.string.network_connection_restored))
                    }
                    else -> {
                        viewModel.emitError(AppError(errorResId = R.string.network_error_no_connection))
                    }
                }
            }
            .launchIn(lifecycleScope)
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ProfileApp(
    user: UserEntity?,
    notes: String,
    onValueChange: (notes: String) -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    Column {
        TopAppBar(
            title = { Text(text = user?.name.orEmpty(), style = Typography.body1) },
            navigationIcon = {
                IconButton(onClick = { onBack.invoke() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Navigation Icon"
                    )
                }
            },
        )
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
        ) {
            val painter = loadImageUrl(
                url = user?.avatarUrl.orEmpty(),
            )
            if (painter != null) {
                Image(
                    painter = painter,
                    contentDescription = "Avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(460.dp)
                )
            }
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1F)
                ) {
                    Text(
                        text = user?.followers?.toString() ?: "0",
                        style = Typography.h6,
                        color = MaterialTheme.myColors.onSurface,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = stringResource(id = R.string.followers),
                        style = Typography.body1,
                        color = MaterialTheme.myColors.onSurface60,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1F)
                ) {
                    Text(
                        text = user?.following?.toString() ?: "0",
                        style = Typography.h6,
                        color = MaterialTheme.myColors.onSurface,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = stringResource(id = R.string.followers),
                        style = Typography.body1,
                        color = MaterialTheme.myColors.onSurface60,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                    )
                }
            }
            if (user?.name?.isNotBlank() == true) {
                Row(
                    modifier = Modifier
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        tint = MaterialTheme.myColors.primary,
                        imageVector = Icons.Outlined.AccountCircle,
                        contentDescription = "Icon Account",
                        modifier = Modifier
                            .width(24.dp)
                            .height(24.dp)
                    )
                    Text(
                        text = user.name,
                        style = Typography.body1,
                        color = MaterialTheme.myColors.onSurface,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
            }
            if (user?.company?.isNotBlank() == true) {
                Row(
                    modifier = Modifier
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        tint = MaterialTheme.myColors.primary,
                        imageVector = Icons.Outlined.Home,
                        contentDescription = "Icon Company",
                        modifier = Modifier
                            .width(24.dp)
                            .height(24.dp)
                    )
                    Text(
                        text = user.company,
                        color = MaterialTheme.myColors.onSurface,
                        style = Typography.body1,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
            }
            if (user?.blog?.isNotBlank() == true) {
                Row(
                    modifier = Modifier
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        tint = MaterialTheme.myColors.primary,
                        imageVector = Icons.Outlined.Info,
                        contentDescription = "Icon Blog",
                        modifier = Modifier
                            .width(24.dp)
                            .height(24.dp)
                    )
                    Text(
                        text = user.blog,
                        color = MaterialTheme.myColors.onSurface,
                        style = Typography.body1,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
            }
            TextField(
                label = {
                    Text(
                        text = stringResource(id = R.string.notes),
                        style = Typography.body1,
                    )
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                        onSave.invoke()
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(horizontal = 16.dp),
                value = notes,
                onValueChange = { newValue ->
                    onValueChange(newValue)
                }
            )
            Button(
                onClick = { onSave.invoke() },
                modifier = Modifier
                    .align(Alignment.End)
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(text = stringResource(id = R.string.save))
            }
        }
    }
}

@Composable
fun loadImageUrl(url: String, placeholder: Painter? = null): Painter? {

    var state by remember {
        mutableStateOf(placeholder)
    }

    val context = LocalContext.current
    val result = object : CustomTarget<Bitmap>() {
        override fun onLoadCleared(p: Drawable?) {
            state = placeholder
        }

        override fun onResourceReady(
            resource: Bitmap,
            transition: Transition<in Bitmap>?,
        ) {
            state = BitmapPainter(resource.asImageBitmap())
        }
    }
    try {
        Glide.with(context)
            .asBitmap()
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .into(result)
    } catch (e: Exception) {
        // Can't use LocalContext in Compose Preview
    }
    return state
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    GithubProfileTheme {
        ProfileApp(
            UserEntity(
                1,
                "username",
                "",
                "",
                "Name",
                "Apple",
                "apple.com",
            ),
            "Notes",
            onValueChange = {},
            onSave = {}
        ) {}
    }
}