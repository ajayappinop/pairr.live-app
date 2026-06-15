package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.OrangeSecondary
import com.example.ui.theme.PinkPrimary
import com.example.ui.theme.appBorderColor
import com.example.ui.theme.appErrorColor
import com.example.ui.theme.appErrorContainer
import com.example.ui.theme.appSecondaryText
import com.example.ui.theme.AppBorderWeight
import com.example.ui.theme.isAppDarkTheme

enum class AppDialogVariant {
    Default,
    Logout,
    DeleteAccount,
    Block
}

@Composable
fun AppActionDialog(
    title: String,
    message: String,
    confirmText: String,
    variant: AppDialogVariant = AppDialogVariant.Default,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val textColor = MaterialTheme.colorScheme.onSurface
    val surfaceColor = if (isAppDarkTheme()) MaterialTheme.colorScheme.surface else Color.White
    val borderColor = appBorderColor(AppBorderWeight.Default)
    val pinkGradient = Brush.horizontalGradient(listOf(PinkPrimary, OrangeSecondary))
    val destructiveColor = appErrorColor()

    val icon: ImageVector
    val iconTint: Color
    val iconBackground: Color
    val confirmBackground: Brush

    when (variant) {
        AppDialogVariant.Logout -> {
            icon = Icons.AutoMirrored.Filled.Logout
            iconTint = PinkPrimary
            iconBackground = PinkPrimary.copy(alpha = 0.12f)
            confirmBackground = pinkGradient
        }
        AppDialogVariant.DeleteAccount -> {
            icon = Icons.Default.Delete
            iconTint = destructiveColor
            iconBackground = appErrorContainer()
            confirmBackground = Brush.horizontalGradient(listOf(destructiveColor, destructiveColor.copy(alpha = 0.85f)))
        }
        AppDialogVariant.Block -> {
            icon = Icons.Default.Block
            iconTint = destructiveColor
            iconBackground = appErrorContainer()
            confirmBackground = Brush.horizontalGradient(listOf(destructiveColor, destructiveColor.copy(alpha = 0.85f)))
        }
        AppDialogVariant.Default -> {
            icon = Icons.Default.Warning
            iconTint = OrangeSecondary
            iconBackground = OrangeSecondary.copy(alpha = 0.12f)
            confirmBackground = pinkGradient
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = surfaceColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, borderColor, RoundedCornerShape(24.dp))
                    .padding(horizontal = 22.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(iconBackground),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = title,
                    color = textColor,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = message,
                    color = appSecondaryText(),
                    fontSize = 14.sp,
                    lineHeight = 21.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(22.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
                    ) {
                        Text("Cancel", color = appSecondaryText(), fontWeight = FontWeight.SemiBold)
                    }
                    Button(
                        onClick = {
                            onConfirm()
                            onDismiss()
                        },
                        modifier = Modifier.weight(1.2f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(0.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(confirmBackground, RoundedCornerShape(12.dp))
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(confirmText, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BlockUserDialog(
    userName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AppActionDialog(
        title = "Block $userName?",
        message = "They won't be able to message or call you. You can unblock them anytime from Settings.",
        confirmText = "Block",
        variant = AppDialogVariant.Block,
        onConfirm = onConfirm,
        onDismiss = onDismiss
    )
}
