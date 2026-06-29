package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.AppModel
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.UploadFile
import com.example.data.displayCardImageUrl
import com.example.data.displayProfilePhotoUrl
import com.example.data.publicUsername
import com.example.ui.theme.OrangeSecondary
import com.example.ui.theme.PinkPrimary

@Composable
fun ModelCardMedia(
    model: AppModel,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    @Suppress("UNUSED_PARAMETER") useProfilePhotoOnly: Boolean = false
) {
    val cardImage = model.displayCardImageUrl()
    val displayName = model.publicUsername()

    AsyncImage(
        model = cardImage,
        contentDescription = displayName,
        modifier = modifier.fillMaxSize(),
        contentScale = contentScale
    )
}

@Composable
fun ModelStoryCircle(
    model: AppModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 76.dp,
    showOnlineIndicator: Boolean = true
) {
    val storyRingGradient = Brush.linearGradient(listOf(PinkPrimary, OrangeSecondary, PinkPrimary))
    val imageUrl = model.displayCardImageUrl()
    val displayName = model.publicUsername()
    val textColor = MaterialTheme.colorScheme.onSurface
    val secondaryText = MaterialTheme.colorScheme.onSurfaceVariant

    Column(
        modifier = modifier
            .width(size + 8.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size(size + 8.dp)
                    .background(storyRingGradient, CircleShape)
                    .padding(3.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.background)
                    .padding(2.dp)
                    .clip(CircleShape)
            ) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = displayName,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            if (showOnlineIndicator && model.status == "Online") {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = 2.dp, y = 2.dp)
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.background)
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF4CAF50))
                        .border(1.5.dp, MaterialTheme.colorScheme.background, CircleShape)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = displayName,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.width(size + 12.dp)
        )
        Text(
            text = model.status,
            color = secondaryText,
            fontSize = 10.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.width(size + 12.dp)
        )
    }
}

@Composable
fun ModelRatePerMinuteRow(
    audioPrice: Int,
    videoPrice: Int,
    modifier: Modifier = Modifier
) {
    val cardBg = MaterialTheme.colorScheme.surface
    val borderColor = MaterialTheme.colorScheme.outline
    val textColor = MaterialTheme.colorScheme.onSurface

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        RateChip(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.Call,
            label = "Audio",
            price = audioPrice,
            accent = Color(0xFF3DDC84),
            cardBg = cardBg,
            borderColor = borderColor,
            textColor = textColor
        )
        RateChip(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.Videocam,
            label = "Video",
            price = videoPrice,
            accent = PinkPrimary,
            cardBg = cardBg,
            borderColor = borderColor,
            textColor = textColor
        )
    }
}

@Composable
private fun RateChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    price: Int,
    accent: Color,
    cardBg: Color,
    borderColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(cardBg)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = label, tint = accent, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.height(6.dp))
        Text("$price", color = textColor, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text("Rupees / min", color = textColor.copy(alpha = 0.55f), fontSize = 11.sp)
        Text(label, color = accent, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun ModelProfileCardMediaBanner(
    model: AppModel,
    profilePhotoOverride: String? = null,
    modifier: Modifier = Modifier
) {
    val profilePhoto = profilePhotoOverride?.takeIf { it.isNotBlank() }
        ?: model.displayProfilePhotoUrl()
    val gradient = Brush.verticalGradient(
        colors = listOf(Color.Transparent, MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
    ) {
        AsyncImage(
            model = profilePhoto,
            contentDescription = "Profile photo",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(modifier = Modifier.fillMaxSize().background(gradient))
    }
}

@Composable
fun FullScreenImageDialog(
    imageUrl: String,
    imageIndex: Int,
    totalImages: Int,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.95f))
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Full screen image",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentScale = ContentScale.Fit
            )
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(50))
            ) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
            }
            Text(
                text = "${imageIndex + 1} / $totalImages",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
                    .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
fun ModelProfilePhotoSection(
    profilePhotoUrl: String?,
    cardBg: Color,
    borderColor: Color,
    textColor: Color,
    secondaryTextColor: Color,
    onChangePhoto: () -> Unit,
    onRemovePhoto: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(cardBg)
            .border(1.dp, borderColor, RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Text(
            "Profile Photo",
            color = textColor,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            "This photo is shown on your public profile and model detail page.",
            color = secondaryTextColor,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .border(2.dp, borderColor, CircleShape)
                    .background(textColor.copy(alpha = 0.05f)),
                contentAlignment = Alignment.Center
            ) {
                if (!profilePhotoUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = profilePhotoUrl,
                        contentDescription = "Profile photo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = null,
                        tint = PinkPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onChangePhoto,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PinkPrimary),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PinkPrimary)
                ) {
                    Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Change Photo", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
                OutlinedButton(
                    onClick = onRemovePhoto,
                    enabled = !profilePhotoUrl.isNullOrBlank(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF4D4D)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF4D4D))
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Remove Photo", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
