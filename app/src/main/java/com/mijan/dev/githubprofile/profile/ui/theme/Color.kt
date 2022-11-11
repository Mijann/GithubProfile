package com.mijan.dev.githubprofile.profile.ui.theme

import androidx.compose.material.Colors
import androidx.compose.ui.graphics.Color

val Blue200 = Color(0xFF536DFE)
val Blue400 = Color(0xFF3D5AFE)
val Blue700 = Color(0xFF304FFE)
val Teal200 = Color(0xFF03DAC5)
val onSurface20Light = Color(0xFF001E2F)
val OnSurface60Light = Color(0xFF74777F)
val onSurface20Dark = Color(0xFFE0F1FF)
val OnSurface60Dark= Color(0xFFA8ADBD)


data class MyColors(
    val material: Colors,
    val onSurface60: Color,
) {
    val primary: Color get() = material.primary
    val primaryVariant: Color get() = material.primaryVariant
    val secondary: Color get() = material.secondary
    val secondaryVariant: Color get() = material.secondaryVariant
    val background: Color get() = material.background
    val surface: Color get() = material.surface
    val error: Color get() = material.error
    val onPrimary: Color get() = material.onPrimary
    val onSecondary: Color get() = material.onSecondary
    val onBackground: Color get() = material.onBackground
    val onSurface: Color get() = material.onSurface
    val onError: Color get() = material.onError
    val isLight: Boolean get() = material.isLight
}
