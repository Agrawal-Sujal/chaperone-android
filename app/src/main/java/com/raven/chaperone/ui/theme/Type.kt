package com.raven.chaperone.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.raven.chaperone.R

val LexendFontFamily = FontFamily(
    Font(R.font.lexend)
)
private val DefaultTypography = Typography()

val Typography = Typography(
    displayLarge = DefaultTypography.displayLarge.copy(fontFamily = LexendFontFamily),
    displayMedium = DefaultTypography.displayMedium.copy(fontFamily = LexendFontFamily),
    displaySmall = DefaultTypography.displaySmall.copy(fontFamily = LexendFontFamily),
    headlineLarge = DefaultTypography.headlineLarge.copy(fontFamily = LexendFontFamily),
    headlineMedium = DefaultTypography.headlineMedium.copy(fontFamily = LexendFontFamily),
    headlineSmall = DefaultTypography.headlineSmall.copy(fontFamily = LexendFontFamily),
    titleLarge = DefaultTypography.titleLarge.copy(fontFamily = LexendFontFamily),
    titleMedium = DefaultTypography.titleMedium.copy(fontFamily = LexendFontFamily),
    titleSmall = DefaultTypography.titleSmall.copy(fontFamily = LexendFontFamily),
    bodyLarge = DefaultTypography.bodyLarge.copy(fontFamily = LexendFontFamily),
    bodyMedium = DefaultTypography.bodyMedium.copy(fontFamily = LexendFontFamily),
    bodySmall = DefaultTypography.bodySmall.copy(fontFamily = LexendFontFamily),
    labelLarge = DefaultTypography.labelLarge.copy(fontFamily = LexendFontFamily),
    labelMedium = DefaultTypography.labelMedium.copy(fontFamily = LexendFontFamily),
    labelSmall = DefaultTypography.labelSmall.copy(fontFamily = LexendFontFamily)
)