package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.LightShadow
import com.example.ui.theme.OrangeSecondary
import com.example.ui.theme.PinkPrimary
import com.example.ui.theme.appShadowAmbient
import com.example.ui.theme.appShadowSpot
import com.example.ui.theme.appSoftShadow
import com.example.ui.theme.isAppDarkTheme

enum class SocialProvider {
    Google,
    Facebook
}

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

@Composable
fun AuthSocialButton(
    provider: SocialProvider,
    label: String,
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    val isDarkTheme = isAppDarkTheme()
    val surfaceColor = if (isDarkTheme) {
        MaterialTheme.colorScheme.surface
    } else {
        MaterialTheme.colorScheme.surface
    }
    val borderColor = MaterialTheme.colorScheme.outlineVariant
    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant
    val shape = RoundedCornerShape(16.dp)

    val buttonModifier = if (isDarkTheme) {
        modifier
            .clip(shape)
            .background(surfaceColor)
            .border(1.dp, MaterialTheme.colorScheme.outline, shape)
    } else {
        modifier
            .appSoftShadow(shape, elevation = 4.dp)
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(Color.White, MaterialTheme.colorScheme.surfaceContainerHighest)
                )
            )
            .border(1.dp, borderColor, shape)
    }

    Column(
        modifier = buttonModifier.padding(
            vertical = if (compact) 10.dp else 14.dp,
            horizontal = 8.dp
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(if (compact) 30.dp else 36.dp)
                .then(
                    if (!isDarkTheme) {
                        Modifier
                            .shadow(2.dp, CircleShape, ambientColor = LightShadow)
                            .background(Color.White, CircleShape)
                    } else {
                        Modifier.background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(
                    id = when (provider) {
                        SocialProvider.Google -> R.drawable.ic_google
                        SocialProvider.Facebook -> R.drawable.ic_facebook
                    }
                ),
                contentDescription = label,
                modifier = Modifier.size(if (compact) 18.dp else 22.dp)
            )
        }
        Spacer(modifier = Modifier.height(if (compact) 6.dp else 8.dp))
        Text(
            text = label,
            color = labelColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
