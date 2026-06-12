package com.example.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

fun Color.luminance(): Float = 0.299f * red + 0.587f * green + 0.114f * blue

@Composable
fun isAppDarkTheme(): Boolean = MaterialTheme.colorScheme.background.luminance() < 0.5f

@Composable
fun accentHorizontalGradient(): Brush {
    return Brush.horizontalGradient(listOf(PinkPrimary, OrangeSecondary))
}

@Composable
fun accentVerticalGradient(): Brush {
    return Brush.verticalGradient(listOf(PinkPrimary, OrangeSecondary))
}

@Composable
fun appScreenBackgroundBrush(): Brush {
    return if (isAppDarkTheme()) {
        Brush.verticalGradient(
            colors = listOf(DarkBackground, DarkBackground, Color(0xFF12101A))
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                LightLavenderTint,
                LightPinkTint,
                LightBackground,
                LightPeachTint.copy(alpha = 0.55f)
            )
        )
    }
}

@Composable
fun appAtmosphereBrush(): Brush {
    return if (isAppDarkTheme()) {
        Brush.radialGradient(
            colors = listOf(PinkPrimary.copy(alpha = 0.08f), Color.Transparent),
            radius = 1200f
        )
    } else {
        Brush.radialGradient(
            colors = listOf(
                PinkPrimary.copy(alpha = 0.07f),
                OrangeSecondary.copy(alpha = 0.04f),
                Color.Transparent
            ),
            radius = 900f
        )
    }
}

@Composable
fun softCardBrush(): Brush {
    return if (isAppDarkTheme()) {
        Brush.verticalGradient(
            listOf(MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.surface)
        )
    } else {
        Brush.verticalGradient(
            listOf(LightSurface, LightSurfaceContainer)
        )
    }
}

@Composable
fun selectedChipBrush(): Brush {
    return accentHorizontalGradient()
}

@Composable
fun appAccentColor(): Color = if (isAppDarkTheme()) PinkPrimaryDark else PinkPrimary

@Composable
fun bottomBarBrush(): Brush {
    return if (isAppDarkTheme()) {
        Brush.verticalGradient(
            listOf(MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.surface)
        )
    } else {
        Brush.verticalGradient(
            listOf(
                Color.White.copy(alpha = 0.98f),
                LightSurfaceContainer,
                LightPinkTint.copy(alpha = 0.35f)
            )
        )
    }
}

object LightPromoGradients {
    val tutorial = listOf(Color(0xFFFFB8D9), Color(0xFFFFD4B8), Color(0xFFFFF0E8))
    val welcome = listOf(Color(0xFFE8D4FF), Color(0xFFF5D4FF), Color(0xFFFFF5FC))
    val audio = listOf(Color(0xFFB8F0E8), Color(0xFFD4F5EE), Color(0xFFEDFCF8))
    val video = listOf(Color(0xFFFFC8D8), Color(0xFFD4D8FF), Color(0xFFF5F0FF))
    val guide = listOf(Color(0xFFFFD4B8), Color(0xFFFFE8CC), Color(0xFFFFF8F0))
}

object DarkPromoGradients {
    val tutorial = listOf(PinkPrimary, OrangeSecondary)
    val welcome = listOf(Color(0xFF7B2FF7), PinkPrimary)
    val audio = listOf(Color(0xFF11998E), Color(0xFF38EF7D))
    val video = listOf(Color(0xFFFC466B), Color(0xFF3F5EFB))
    val guide = listOf(Color(0xFFFF512F), OrangeSecondary)
}

@Composable
fun SoftScreenBackground(
    modifier: Modifier = Modifier,
    showAtmosphere: Boolean = true,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(appScreenBackgroundBrush())
    ) {
        if (showAtmosphere) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(appAtmosphereBrush())
            )
        }
        content()
    }
}
