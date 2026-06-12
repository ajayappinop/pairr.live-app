package com.example.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class AppBorderWeight {
    Subtle,
    Default,
    Strong,
    Focus
}

@Composable
fun appTitleText(): Color = MaterialTheme.colorScheme.onSurface

@Composable
fun appBodyText(): Color = MaterialTheme.colorScheme.onSurface

@Composable
fun appSecondaryText(): Color {
    return if (isAppDarkTheme()) {
        MaterialTheme.colorScheme.onSurfaceVariant
    } else {
        LightTextSecondary
    }
}

@Composable
fun appMutedText(): Color {
    return if (isAppDarkTheme()) {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
    } else {
        LightTextMuted
    }
}

@Composable
fun appCaptionText(): Color {
    return if (isAppDarkTheme()) {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.42f)
    } else {
        LightTextCaption
    }
}

@Composable
fun appBorderColor(weight: AppBorderWeight = AppBorderWeight.Default): Color {
    if (isAppDarkTheme()) {
        return when (weight) {
            AppBorderWeight.Subtle -> MaterialTheme.colorScheme.outlineVariant
            AppBorderWeight.Strong -> MaterialTheme.colorScheme.outline
            AppBorderWeight.Focus -> PinkPrimaryDark.copy(alpha = 0.55f)
            AppBorderWeight.Default -> MaterialTheme.colorScheme.outline
        }
    }
    return when (weight) {
        AppBorderWeight.Subtle -> LightOutlineSubtle
        AppBorderWeight.Default -> LightBorder
        AppBorderWeight.Strong -> LightBorderStrong
        AppBorderWeight.Focus -> PinkPrimary
    }
}

@Composable
fun appShadowAmbient(): Color {
    return if (isAppDarkTheme()) Color.Black.copy(alpha = 0.14f) else LightShadowAmbient
}

@Composable
fun appShadowSpot(): Color {
    return if (isAppDarkTheme()) Color.Black.copy(alpha = 0.2f) else LightShadowSpot
}

@Composable
fun appCardElevation(): Dp = if (isAppDarkTheme()) 4.dp else 10.dp

@Composable
fun appInputElevation(): Dp = if (isAppDarkTheme()) 2.dp else 6.dp

@Composable
fun Modifier.appSoftShadow(
    shape: Shape,
    elevation: Dp? = null
): Modifier {
    val elev = elevation ?: appCardElevation()
    return shadow(
        elevation = elev,
        shape = shape,
        ambientColor = appShadowAmbient(),
        spotColor = appShadowSpot(),
        clip = false
    )
}

@Composable
fun appSubtleFill(): Color {
    return if (isAppDarkTheme()) {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
    } else {
        LightSurfaceVariant
    }
}

@Composable
fun AppSegmentedTabs(
    tabs: List<String>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 15.sp,
    verticalPadding: Dp = 12.dp
) {
    val isLight = !isAppDarkTheme()
    val shape = RoundedCornerShape(16.dp)
    val tabShape = RoundedCornerShape(12.dp)
    val containerBg = if (isLight) Color.White else MaterialTheme.colorScheme.surface

    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(if (isLight) Modifier.appSoftShadow(shape) else Modifier)
            .clip(shape)
            .background(containerBg)
            .border(1.dp, appBorderColor(AppBorderWeight.Default), shape)
            .padding(4.dp)
    ) {
        tabs.forEachIndexed { index, title ->
            val selected = selectedIndex == index
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(tabShape)
                    .background(if (selected) PinkPrimary else Color.Transparent)
                    .clickable { onTabSelected(index) }
                    .padding(vertical = verticalPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title,
                    color = when {
                        selected -> Color.White
                        isLight -> LightOnSurface
                        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    },
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
                    fontSize = fontSize,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun AppFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 12.sp
) {
    val shape = RoundedCornerShape(20.dp)
    Box(
        modifier = modifier
            .clip(shape)
            .then(
                if (selected) {
                    Modifier.background(PinkPrimary, shape)
                } else if (isAppDarkTheme()) {
                    Modifier
                        .background(MaterialTheme.colorScheme.surfaceVariant, shape)
                        .border(1.dp, appBorderColor(AppBorderWeight.Subtle), shape)
                } else {
                    Modifier
                        .background(Color.White, shape)
                        .border(1.dp, appBorderColor(AppBorderWeight.Default), shape)
                }
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (selected) Color.White else appSecondaryText(),
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun Modifier.appSurfaceCard(
    shape: Shape = RoundedCornerShape(20.dp),
    borderWeight: AppBorderWeight = AppBorderWeight.Default,
    withShadow: Boolean = true
): Modifier {
    var mod = if (withShadow) this.appSoftShadow(shape) else this
    mod = mod
        .clip(shape)
        .then(
            if (isAppDarkTheme()) {
                Modifier.background(MaterialTheme.colorScheme.surface)
            } else {
                Modifier.background(Color.White)
            }
        )
        .border(1.dp, appBorderColor(borderWeight), shape)
    return mod
}

@Composable
fun appOutlinedFieldColors(): TextFieldColors {
    val isDark = isAppDarkTheme()
    val container = if (isDark) {
        MaterialTheme.colorScheme.surface
    } else {
        MaterialTheme.colorScheme.surfaceContainerHighest
    }
    return OutlinedTextFieldDefaults.colors(
        focusedContainerColor = container,
        unfocusedContainerColor = container,
        disabledContainerColor = container,
        focusedBorderColor = appBorderColor(AppBorderWeight.Focus),
        unfocusedBorderColor = appBorderColor(AppBorderWeight.Default),
        errorBorderColor = MaterialTheme.colorScheme.error,
        focusedTextColor = appBodyText(),
        unfocusedTextColor = appBodyText(),
        focusedPlaceholderColor = appCaptionText(),
        unfocusedPlaceholderColor = appCaptionText(),
        focusedLeadingIconColor = appAccentColor(),
        unfocusedLeadingIconColor = appMutedText(),
        focusedTrailingIconColor = appMutedText(),
        unfocusedTrailingIconColor = appMutedText(),
        cursorColor = appAccentColor()
    )
}

@Composable
fun Modifier.appInputSurface(shape: Shape = RoundedCornerShape(16.dp)): Modifier {
    return this
        .appSoftShadow(shape, appInputElevation())
        .clip(shape)
        .background(
            if (isAppDarkTheme()) {
                MaterialTheme.colorScheme.surface
            } else {
                MaterialTheme.colorScheme.surfaceContainerHighest
            }
        )
        .border(1.dp, appBorderColor(AppBorderWeight.Subtle), shape)
}
