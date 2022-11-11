package com.mijan.dev.githubprofile.profile.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

private val DarkColorPalette = MyColors(
    material = darkColors(
        primary = Blue200,
        primaryVariant = Blue700,
        secondary = Teal200,
        onSurface = onSurface20Dark
    ),
    onSurface60 = OnSurface60Dark
)

private val LightColorPalette = MyColors(
    material = lightColors(
        primary = Blue400,
        primaryVariant = Blue700,
        secondary = Teal200,
        onSurface = onSurface20Light
    ),
    onSurface60 = OnSurface60Light

/* Other default colors to override
background = Color.White,
surface = Color.White,
onPrimary = Color.White,
onSecondary = Color.Black,
onBackground = Color.Black,
onSurface = Color.Black,
*/
)


private val LocalColors = staticCompositionLocalOf { LightColorPalette }

@Composable
fun GithubProfileTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    CompositionLocalProvider(LocalColors provides colors) {
        MaterialTheme(
            colors = colors.material,
            typography = Typography,
            shapes = Shapes,
            content = content,
        )
    }
}

val MaterialTheme.myColors: MyColors
    @Composable
    @ReadOnlyComposable
    get() = LocalColors.current