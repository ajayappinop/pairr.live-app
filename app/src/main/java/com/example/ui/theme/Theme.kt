package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = PinkPrimaryDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF3D1230),
    onPrimaryContainer = Color(0xFFFFB8D9),
    secondary = OrangeSecondaryDark,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF3D2210),
    onSecondaryContainer = Color(0xFFFFD4B8),
    tertiary = YellowTertiaryDark,
    onTertiary = Color(0xFF1A1A2E),
    tertiaryContainer = Color(0xFF3D3510),
    onTertiaryContainer = Color(0xFFFFF0B8),
    background = DarkBackground,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    onSurfaceVariant = Color(0xFFB8B4C4),
    outline = DarkBorder,
    outlineVariant = Color(0xFF353344),
    surfaceVariant = Color(0xFF1F1E29),
    surfaceContainerHighest = Color(0xFF252433),
    inverseSurface = LightSurface,
    inverseOnSurface = LightOnSurface,
    errorContainer = Color(0xFF4A1515),
    onErrorContainer = Color(0xFFFFB4AB),
  )

private val LightColorScheme =
  lightColorScheme(
    primary = PinkPrimary,
    onPrimary = Color.White,
    primaryContainer = LightPrimaryContainer,
    onPrimaryContainer = LightOnPrimaryContainer,
    secondary = OrangeSecondary,
    onSecondary = Color.White,
    secondaryContainer = LightSecondaryContainer,
    onSecondaryContainer = LightOnSecondaryContainer,
    tertiary = YellowTertiary,
    onTertiary = LightOnSurface,
    tertiaryContainer = LightTertiaryContainer,
    onTertiaryContainer = LightOnTertiaryContainer,
    background = LightBackground,
    onBackground = LightOnSurface,
    surface = LightSurface,
    onSurface = LightOnSurface,
    onSurfaceVariant = LightTextSecondary,
    outline = LightBorderStrong,
    outlineVariant = LightOutlineVariant,
    surfaceVariant = LightSurfaceVariant,
    surfaceContainerHighest = LightSurfaceContainer,
    surfaceContainer = LightSurfaceContainer,
    surfaceContainerLow = LightPinkTint,
    inverseSurface = DarkSurface,
    inverseOnSurface = DarkOnSurface,
    errorContainer = LightErrorContainer,
    onErrorContainer = Color(0xFF8C1D18),
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
