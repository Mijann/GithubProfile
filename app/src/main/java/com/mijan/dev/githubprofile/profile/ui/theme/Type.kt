package com.mijan.dev.githubprofile.profile.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.mijan.dev.githubprofile.R

// Set of Material typography styles to start with
val nunitoSans = FontFamily(
    Font(R.font.nunito_sans_extra_bold, FontWeight.ExtraBold),
    Font(R.font.nunito_sans_semi_bold, FontWeight.SemiBold),
    Font(R.font.nunito_sans_regular, FontWeight.Normal),
)
val Typography = Typography(
    h6 = TextStyle(
        fontFamily = nunitoSans,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 20.sp
    ),
    body1 = TextStyle(
        fontFamily = nunitoSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 17.sp
    ),
    body2 = TextStyle(
        fontFamily = nunitoSans,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
    ),
    subtitle1 = TextStyle(
        fontFamily = nunitoSans,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
    ),

    /* Other default text styles to override
    button = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W500,
        fontSize = 14.sp
    ),
    caption = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    )
    */
)