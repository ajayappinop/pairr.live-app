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
import com.example.data.displayImageUrls
import com.example.data.displayIntroVideoUrl
import com.example.data.displayProfilePhotoUrl
import com.example.data.storedImageUrls
import com.example.ui.theme.OrangeSecondary
import com.example.ui.theme.PinkPrimary

@Composable
fun ModelCardMedia(
    model: AppModel,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    /** When true, always show profile/first photo (for home lists & story rings). */
    useProfilePhotoOnly: Boolean = false
) {
    val introVideo = if (useProfilePhotoOnly) null else model.displayIntroVideoUrl()
    val cardImage = model.displayCardImageUrl()

    Box(modifier = modifier) {
        AsyncImage(
            model = cardImage,
            contentDescription = model.name,
            modifier = Modifier.fillMaxSize(),
            contentScale = contentScale
        )
        if (introVideo != null) {
            VideoPlayer(
                videoUrl = introVideo,
                modifier = Modifier.fillMaxSize(),
                autoPlay = true,
                loop = true,
                muted = true,
                useController = false
            )
        }
    }
}

@Composable
fun ModelStoryCircle(
    model: AppModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 76.dp,
    showLiveBadge: Boolean = true
) {
    val storyRingGradient = Brush.linearGradient(listOf(PinkPrimary, OrangeSecondary, PinkPrimary))
    val imageUrl = model.displayCardImageUrl()
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
                    contentDescription = model.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            if (showLiveBadge && model.status == "Online") {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = 6.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFFF5252))
                        .padding(horizontal = 7.dp, vertical = 2.dp)
                ) {
                    Text("LIVE", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Black)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = model.name.split(" ").firstOrNull() ?: model.name,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.width(size + 12.dp)
        )
        Text(
            text = model.categories.firstOrNull() ?: "Live",
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
        Text("Tokens / min", color = textColor.copy(alpha = 0.55f), fontSize = 11.sp)
        Text(label, color = accent, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun ModelImageGridGallery(
    imageUrls: List<String>,
    modifier: Modifier = Modifier,
    onImageClick: ((String, Int) -> Unit)? = null
) {
    val images = imageUrls.take(5)
    if (images.isEmpty()) return

    val columns = 2
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        images.chunked(columns).forEach { rowImages ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowImages.forEachIndexed { rowIndex, url ->
                    val globalIndex = images.indexOf(url)
                    val isFeatured = globalIndex == 0 && images.size > 1
                    AsyncImage(
                        model = url,
                        contentDescription = "Gallery image ${globalIndex + 1}",
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(if (isFeatured && rowIndex == 0 && rowImages.size == 1) 1.2f else 1f)
                            .clip(RoundedCornerShape(14.dp))
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp))
                            .clickable { onImageClick?.invoke(url, globalIndex) },
                        contentScale = ContentScale.Crop
                    )
                }
                repeat(columns - rowImages.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun ModelIntroVideoSection(
    videoUrl: String,
    modifier: Modifier = Modifier,
    height: androidx.compose.ui.unit.Dp = 280.dp
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .clip(RoundedCornerShape(bottomStart = 0.dp, bottomEnd = 0.dp))
        ) {
            VideoPlayer(
                videoUrl = videoUrl,
                modifier = Modifier.fillMaxSize(),
                autoPlay = true,
                loop = false,
                muted = false,
                useController = true
            )
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp)
                    .background(Color.Black.copy(alpha = 0.45f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text("Intro Video", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ModelProfileCardMediaBanner(
    model: AppModel,
    profilePhotoOverride: String? = null,
    modifier: Modifier = Modifier
) {
    val introVideo = model.displayIntroVideoUrl()
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
        if (introVideo != null) {
            VideoPlayer(
                videoUrl = introVideo,
                modifier = Modifier.fillMaxSize(),
                autoPlay = true,
                loop = true,
                muted = true,
                useController = false
            )
        } else {
            AsyncImage(
                model = profilePhoto,
                contentDescription = "Profile photo",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        Box(modifier = Modifier.fillMaxSize().background(gradient))
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp)
                .background(PinkPrimary.copy(alpha = 0.85f), RoundedCornerShape(8.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                text = if (introVideo != null) "▶ Intro" else "Profile",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
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
fun ModelMediaUploadSection(
    model: AppModel,
    cardBg: Color,
    borderColor: Color,
    textColor: Color,
    secondaryTextColor: Color,
    onAddPhoto: () -> Unit,
    onRemovePhoto: (Int) -> Unit,
    onUploadVideo: () -> Unit,
    onRemoveVideo: () -> Unit,
    onPhotoClick: ((String, Int) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val photos = model.storedImageUrls()
    val introVideo = model.displayIntroVideoUrl()
    val photoSlots: List<String?> = photos.map { it as String? } +
        if (photos.size < 5) listOf(null) else emptyList()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(cardBg)
            .border(1.dp, borderColor, RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Text(
            "Portfolio Media",
            color = textColor,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            "Upload up to 5 photos and one intro video for your public profile.",
            color = secondaryTextColor,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        Text(
            "Introductory Video",
            color = textColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (introVideo != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, borderColor, RoundedCornerShape(14.dp))
            ) {
                VideoPlayer(
                    videoUrl = introVideo,
                    modifier = Modifier.fillMaxSize(),
                    autoPlay = false,
                    loop = false,
                    muted = false,
                    useController = true
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick = onUploadVideo,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PinkPrimary)
                ) {
                    Icon(Icons.Default.UploadFile, contentDescription = null, tint = PinkPrimary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Change Video", color = PinkPrimary, fontSize = 13.sp)
                }
                OutlinedButton(
                    onClick = onRemoveVideo,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF4D4D))
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFFF4D4D), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Remove", color = Color(0xFFFF4D4D), fontSize = 13.sp)
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, borderColor, RoundedCornerShape(14.dp))
                    .background(textColor.copy(alpha = 0.04f))
                    .clickable { onUploadVideo() },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.UploadFile, contentDescription = null, tint = PinkPrimary, modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Upload Intro Video", color = textColor, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Text("MP4 recommended", color = secondaryTextColor, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        HorizontalDivider(color = borderColor)
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Profile Photos (${photos.size}/5)",
                color = textColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            if (photos.size < 5) {
                TextButton(onClick = onAddPhoto) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = PinkPrimary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Photo", color = PinkPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (photos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, borderColor, RoundedCornerShape(14.dp))
                    .background(textColor.copy(alpha = 0.04f))
                    .clickable { onAddPhoto() },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = PinkPrimary, modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Add your first photo", color = secondaryTextColor, fontSize = 13.sp)
                }
            }
        } else {
            val columns = 3
            photoSlots.chunked(columns).forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowItems.forEach { item ->
                        if (item == null) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(1.dp, PinkPrimary.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                    .background(PinkPrimary.copy(alpha = 0.08f))
                                    .clickable { onAddPhoto() },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Add photo", tint = PinkPrimary, modifier = Modifier.size(28.dp))
                            }
                        } else {
                            val index = photos.indexOf(item)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                                    .clickable { onPhotoClick?.invoke(item, index) }
                            ) {
                                AsyncImage(
                                    model = item,
                                    contentDescription = "Photo ${index + 1}",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                IconButton(
                                    onClick = { onRemovePhoto(index) },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(32.dp)
                                        .background(Color.Black.copy(alpha = 0.55f), RoundedCornerShape(8.dp))
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Remove photo",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                    repeat(columns - rowItems.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
