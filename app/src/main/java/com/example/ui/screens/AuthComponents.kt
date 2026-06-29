package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.theme.OrangeSecondary
import com.example.ui.theme.PinkPrimary
import com.example.ui.theme.appSoftShadow
import com.example.ui.theme.isAppDarkTheme

@Composable
fun BoxScope.AuthBackgroundGlows(isDarkTheme: Boolean = isAppDarkTheme()) {
    if (isDarkTheme) {
        val pinkAlpha = 0.2f
        val orangeAlpha = 0.2f
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 100.dp, y = (-100).dp)
                .size(300.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(PinkPrimary.copy(alpha = pinkAlpha), Color.Transparent)
                    )
                )
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = (-50).dp, y = 50.dp)
                .size(300.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(OrangeSecondary.copy(alpha = orangeAlpha), Color.Transparent)
                    )
                )
        )
    } else {
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 80.dp, y = (-60).dp)
                .size(280.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            PinkPrimary.copy(alpha = 0.14f),
                            Color(0xFFFFB8D9).copy(alpha = 0.08f),
                            Color.Transparent
                        )
                    )
                )
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = (-40).dp, y = 40.dp)
                .size(260.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            OrangeSecondary.copy(alpha = 0.12f),
                            Color(0xFFFFE8CC).copy(alpha = 0.10f),
                            Color.Transparent
                        )
                    )
                )
        )
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(360.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFF0E8FF).copy(alpha = 0.35f),
                            Color.Transparent
                        )
                    )
                )
        )
    }
}

@Composable
fun AuthAppLogo(
    size: Dp,
    cornerRadius: Dp,
    modifier: Modifier = Modifier
) {
    val isDarkTheme = isAppDarkTheme()
    val logoModifier = if (isDarkTheme) {
        modifier
            .size(size)
            .clip(RoundedCornerShape(cornerRadius))
    } else {
        modifier
            .appSoftShadow(RoundedCornerShape(cornerRadius), elevation = 8.dp)
            .size(size)
            .clip(RoundedCornerShape(cornerRadius))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(cornerRadius))
    }

    Image(
        painter = painterResource(id = R.drawable.ic_app_logo),
        contentDescription = "Pairr Logo",
        modifier = logoModifier
    )
}
