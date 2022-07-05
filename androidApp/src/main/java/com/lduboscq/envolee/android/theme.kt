package com.lduboscq.envolee.android

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

val subtitleColor = Color(0XFF687287)

private val darkColorScheme = darkColorScheme(
    surface = Color.Black, // main surface background
    primary = Color(0XFF333d50), // buttons background
    // secondary = Color(0XFF86b9b0),
    surfaceVariant = Color(0XFF1c1c23), // cards background
    onSurface = Color.White, // texts, icons, etc
    onPrimary = Color.White, // buttons text color
    primaryContainer = Color(0XFF86b9b0), // floating action buttons background color
    onPrimaryContainer = Color.Black // texts and icons color on fabs
)

private val lightColorScheme = lightColorScheme(
    surface = Color.White, // main surface background
    primary = Color(0XFF333d50), // buttons background
    //secondary = Color(0XFF86b9b0),
    surfaceVariant = Color(0XFFf5f7f9), // cards background
    onSurface = Color(0XFF1a1b1c), // texts, icons, etc
    onPrimary = Color.White, // buttons text color
    primaryContainer = Color(0XFF86b9b0), // floating action buttons background color
    onPrimaryContainer = Color.Black // texts and icons color on fabs
)

val paddingHorizontal = 32.dp

@Composable
fun EnvoleeTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    val ourColorScheme = if (darkTheme) darkColorScheme else lightColorScheme

    MaterialTheme(
        colorScheme = ourColorScheme,
        content = content
    )
}