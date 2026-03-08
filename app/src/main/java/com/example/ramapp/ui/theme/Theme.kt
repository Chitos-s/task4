package com.example.ramapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val ColorWhite = androidx.compose.ui.graphics.Color(0xFFFFFFFF)

private val LightColors = lightColorScheme(
    primary = BlueGray700,
    onPrimary = BlueGray50,
    secondary = Emerald400,
    tertiary = Amber400,
    background = BlueGray50,
    surface = ColorWhite
)

private val DarkColors = darkColorScheme(
    primary = BlueGray50,
    onPrimary = BlueGray700,
    secondary = Emerald400,
    tertiary = Amber400
)

@Composable
fun RamAppTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as android.app.Activity).window
            window.statusBarColor = BlueGray50.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colorScheme = LightColors,
        typography = Typography,
        content = content
    )
}
