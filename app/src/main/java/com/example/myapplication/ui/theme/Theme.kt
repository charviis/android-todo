package com.example.myapplication.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryColor,
    secondary = SecondaryColor,
    tertiary = PrimaryVariantColor,
    background = BackgroundColorDark,
    surface = BackgroundColorDark,
    onPrimary = TextColorDark,
    onSecondary = TextColorDark,
    onTertiary = TextColorDark,
    onBackground = TextColorDark,
    onSurface = TextColorDark
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryColor,
    secondary = SecondaryColor,
    tertiary = PrimaryVariantColor,
    background = BackgroundColorLight,
    surface = BackgroundColorLight,
    onPrimary = TextColorLight,
    onSecondary = TextColorLight,
    onTertiary = TextColorLight,
    onBackground = TextColorLight,
    onSurface = TextColorLight
)