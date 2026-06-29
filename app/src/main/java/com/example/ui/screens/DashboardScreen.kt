package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import com.example.MainViewModel
import com.example.data.AppModel
import com.example.data.Wallet
import com.example.data.browseLanguageLabel
import com.example.data.browseSortOrder
import com.example.data.canTalkNow
import com.example.data.displayProfilePhotoUrl
import com.example.data.formatReviewCount
import com.example.data.formattedBalanceCompact
import com.example.data.publicUsername
import com.example.ui.components.NotificationsSheet
import com.example.ui.theme.PinkPrimary
import com.example.ui.theme.SoftScreenBackground
import com.example.ui.theme.accentHorizontalGradient
import com.example.ui.theme.appBorderColor
import com.example.ui.theme.appCaptionText
import com.example.ui.theme.appMutedText
import com.example.ui.theme.appSecondaryText
import com.example.ui.theme.appSoftShadow
import com.example.ui.theme.appStarColor
import com.example.ui.theme.appSuccessColor
import com.example.ui.theme.appSurfaceCard
import com.example.ui.theme.appTitleText
import com.example.ui.theme.AppBorderWeight
import com.example.ui.theme.isAppDarkTheme
import com.example.ui.theme.isCompactWidth
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun DashboardScreen(
    viewModel: MainViewModel,
    onModelClick: (String) -> Unit,
    onCall: (String, Boolean) -> Unit
) {
    val favorites by viewModel.favorites.collectAsState()
    val allModels by viewModel.models.collectAsState()
    val blockedUsers by viewModel.blockedUsers.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var showNotificationsSheet by remember { mutableStateOf(false) }
    val unreadNotificationCount by viewModel.notifications.collectAsState()
    val unreadCount = unreadNotificationCount.count { !it.isRead }
    val wallet by viewModel.walletState.collectAsStateWithLifecycle()
    var callOptionsModel by remember { mutableStateOf<AppModel?>(null) }
    var notifyModel by remember { mutableStateOf<AppModel?>(null) }
    val context = LocalContext.current

    val browseModels = remember(allModels, searchQuery, blockedUsers) {
        allModels
            .filter { model -> blockedUsers.none { it.id == model.id } }
            .filter { model ->
                searchQuery.isBlank() ||
                    model.publicUsername().contains(searchQuery, ignoreCase = true) ||
                    model.bio.contains(searchQuery, ignoreCase = true)
            }
            .sortedWith(
                compareBy<AppModel> { it.browseSortOrder() }
                    .thenByDescending { it.rating }
                    .thenByDescending { it.reviewsCount }
            )
    }

    SoftScreenBackground {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 110.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                TopBarSection(
                    searchQuery = searchQuery,
                    onSearchQueryChanged = { searchQuery = it },
                    unreadNotificationCount = unreadCount,
                    onNotificationsClick = { showNotificationsSheet = true },
                    totalTokenBalance = wallet.formattedBalanceCompact()
                )
            }

            item {
                Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp)) {
                    Text(
                        text = "Discover Amazing People",
                        color = appTitleText(),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = "Connect with top-rated models instantly",
                        color = appSecondaryText(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            if (browseModels.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No models found",
                            color = appMutedText(),
                            fontSize = 15.sp
                        )
                    }
                }
            } else {
                items(browseModels, key = { it.id }) { model ->
                    BrowseModelCard(
                        model = model,
                        isFavorite = favorites.contains(model.id),
                        onFavoriteToggle = { viewModel.toggleFavorite(model.id) },
                        onClick = { onModelClick(model.id) },
                        onCallNowClick = { callOptionsModel = model },
                        onNotifyClick = { notifyModel = model },
                        modifier = Modifier.padding(horizontal = 18.dp)
                    )
                }
            }
        }
    }

    if (showNotificationsSheet) {
        NotificationsSheet(
            viewModel = viewModel,
            onDismiss = { showNotificationsSheet = false }
        )
    }

    callOptionsModel?.let { model ->
        BrowseCallOptionsDialog(
            model = model,
            onDismiss = { callOptionsModel = null },
            onAudioCall = {
                onCall(model.id, false)
                callOptionsModel = null
            },
            onVideoCall = {
                onCall(model.id, true)
                callOptionsModel = null
            }
        )
    }

    notifyModel?.let { model ->
        BrowseOfflineNotifyDialog(
            model = model,
            onDismiss = { notifyModel = null },
            onSendMessage = { message, requestType ->
                val threadId = viewModel.ensureChatThread(
                    modelId = model.id,
                    modelName = model.publicUsername(),
                    modelAvatarUrl = model.displayProfilePhotoUrl(),
                    isOnline = model.status == "Online"
                )
                viewModel.sendMessage(threadId, message, isFromUser = true)
                notifyModel = null
                val sentLabel = when (requestType) {
                    OfflineRequestType.Chat -> "Message sent"
                    OfflineRequestType.Call -> "Call request sent"
                    OfflineRequestType.VideoCall -> "Video call request sent"
                }
                Toast.makeText(context, sentLabel, Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
fun BrowseModelCard(
    model: AppModel,
    isFavorite: Boolean,
    onFavoriteToggle: () -> Unit,
    onClick: () -> Unit,
    onCallNowClick: () -> Unit,
    onNotifyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardShape = RoundedCornerShape(18.dp)
    val canTalk = model.canTalkNow()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .appSurfaceCard(shape = cardShape, borderWeight = AppBorderWeight.Default)
            .clickable(onClick = onClick)
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier.size(72.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                AsyncImage(
                    model = model.displayProfilePhotoUrl(),
                    contentDescription = model.publicUsername(),
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .border(2.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f), CircleShape),
                    contentScale = ContentScale.Crop
                )
                BrowseStatusDot(
                    model = model,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = (-2).dp, y = (-2).dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = model.publicUsername(),
                            color = appTitleText(),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        AvailabilityBadge(status = model.status)
                    }
                    IconButton(
                        onClick = onFavoriteToggle,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) PinkPrimary else appMutedText(),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = appStarColor(),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = String.format("%.1f", model.rating),
                        color = appTitleText(),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = " (${formatReviewCount(model.reviewsCount)})",
                        color = appCaptionText(),
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = model.bio,
                    color = appSecondaryText(),
                    fontSize = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = model.languages.browseLanguageLabel(),
                            color = appCaptionText(),
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            BrowseRatePill(
                                icon = Icons.Default.Call,
                                label = "${model.audioPrice}/min",
                                tint = appSuccessColor()
                            )
                            BrowseRatePill(
                                icon = Icons.Default.Videocam,
                                label = "${model.videoPrice}/min",
                                tint = PinkPrimary
                            )
                        }
                    }

                    if (canTalk) {
                        OutlinedButton(
                            onClick = onCallNowClick,
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, appSuccessColor()),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = appSuccessColor()),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text(
                                text = "Call Now",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        IconButton(
                            onClick = onNotifyClick,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFFC107).copy(alpha = 0.18f))
                                    .border(1.dp, Color(0xFFFFC107).copy(alpha = 0.5f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Notifications,
                                    contentDescription = "Notify",
                                    tint = Color(0xFFFFC107),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BrowseStatusDot(model: AppModel, modifier: Modifier = Modifier) {
    if (model.status == "Online") {
        AnimatedOnlineStatusDot(modifier = modifier)
    }
}

@Composable
private fun AnimatedOnlineStatusDot(modifier: Modifier = Modifier) {
    val onlineColor = appSuccessColor()
    val infiniteTransition = rememberInfiniteTransition(label = "online_dot_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "online_ring_scale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.55f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "online_ring_alpha"
    )
    val dotScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "online_dot_scale"
    )

    Box(
        modifier = modifier.size(22.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(18.dp)
                .scale(pulseScale)
                .clip(CircleShape)
                .background(onlineColor.copy(alpha = pulseAlpha))
        )
        Box(
            modifier = Modifier
                .size(18.dp)
                .scale(dotScale)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
                .padding(2.5.dp)
                .clip(CircleShape)
                .background(onlineColor)
                .border(2.5.dp, MaterialTheme.colorScheme.surface, CircleShape)
        )
    }
}

@Composable
private fun BrowseCallOptionsDialog(
    model: AppModel,
    onDismiss: () -> Unit,
    onAudioCall: () -> Unit,
    onVideoCall: () -> Unit
) {
    val cardShape = RoundedCornerShape(22.dp)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.88f)
                .appSoftShadow(cardShape, elevation = 12.dp),
            shape = cardShape,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Call ${model.publicUsername()}",
                    color = appTitleText(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Choose how you want to connect",
                    color = appSecondaryText(),
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.dp))

                BrowseCallOptionRow(
                    icon = Icons.Default.Call,
                    title = "Audio Call",
                    subtitle = "${model.audioPrice} rupees/min",
                    tint = appSuccessColor(),
                    onClick = onAudioCall
                )
                Spacer(modifier = Modifier.height(10.dp))
                BrowseCallOptionRow(
                    icon = Icons.Default.Videocam,
                    title = "Video Call",
                    subtitle = "${model.videoPrice} rupees/min",
                    tint = PinkPrimary,
                    onClick = onVideoCall
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextButton(onClick = onDismiss) {
                    Text("Cancel", color = appMutedText(), fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun BrowseCallOptionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    tint: Color,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(14.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(tint.copy(alpha = 0.1f))
            .border(1.dp, tint.copy(alpha = 0.25f), shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(tint.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = appTitleText(), fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, color = appCaptionText(), fontSize = 12.sp)
        }
        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            tint = appMutedText(),
            modifier = Modifier.size(20.dp)
        )
    }
}

private enum class OfflineRequestType {
    Chat,
    Call,
    VideoCall
}

private fun offlineNotifyMessage(username: String, requestType: OfflineRequestType): String {
    val action = when (requestType) {
        OfflineRequestType.Chat -> "Please Chat with me"
        OfflineRequestType.Call -> "Please Call me"
        OfflineRequestType.VideoCall -> "Please Video Call me"
    }
    return "Hi $username, I want to talk to you. $action 🙂"
}

@Composable
private fun BrowseOfflineNotifyDialog(
    model: AppModel,
    onDismiss: () -> Unit,
    onSendMessage: (String, OfflineRequestType) -> Unit
) {
    val username = model.publicUsername()
    var requestType by remember(model.id) { mutableStateOf(OfflineRequestType.Call) }
    var message by remember(model.id) {
        mutableStateOf(offlineNotifyMessage(username, OfflineRequestType.Call))
    }
    val accentYellow = Color(0xFFFFC107)
    val accentTeal = appSuccessColor()
    val cardShape = RoundedCornerShape(20.dp)
    val messageShape = RoundedCornerShape(14.dp)
    val title = when (model.status) {
        "Busy" -> "Listener is Busy"
        else -> "Listener is Offline"
    }

    LaunchedEffect(requestType) {
        message = offlineNotifyMessage(username, requestType)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .appSoftShadow(cardShape, elevation = 12.dp),
            shape = cardShape,
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.98f),
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    color = accentYellow,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Send message instead?",
                    color = appSecondaryText(),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(18.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 110.dp)
                        .clip(messageShape)
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.65f))
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), messageShape)
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    BasicTextField(
                        value = message,
                        onValueChange = { message = it },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            color = appTitleText(),
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OfflineRequestTypeOption(
                        label = "Chat",
                        selected = requestType == OfflineRequestType.Chat,
                        accentColor = accentTeal,
                        onClick = { requestType = OfflineRequestType.Chat }
                    )
                    OfflineRequestTypeOption(
                        label = "Call",
                        selected = requestType == OfflineRequestType.Call,
                        accentColor = accentTeal,
                        onClick = { requestType = OfflineRequestType.Call }
                    )
                    OfflineRequestTypeOption(
                        label = "Video Call",
                        selected = requestType == OfflineRequestType.VideoCall,
                        accentColor = accentTeal,
                        onClick = { requestType = OfflineRequestType.VideoCall }
                    )
                }

                Spacer(modifier = Modifier.height(22.dp))

                OutlinedButton(
                    onClick = {
                        if (message.isNotBlank()) {
                            onSendMessage(message.trim(), requestType)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, accentYellow),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                        contentColor = accentYellow
                    )
                ) {
                    Text(
                        text = "Send Message",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun OfflineRequestTypeOption(
    label: String,
    selected: Boolean,
    accentColor: Color,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .border(2.dp, accentColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (selected) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(accentColor)
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            color = if (selected) appTitleText() else appCaptionText(),
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun BrowseRatePill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    tint: Color
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(tint.copy(alpha = 0.12f))
            .padding(horizontal = 6.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(11.dp))
        Spacer(modifier = Modifier.width(3.dp))
        Text(label, color = tint, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun TopBarSection(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    unreadNotificationCount: Int = 0,
    onNotificationsClick: () -> Unit = {},
    totalTokenBalance: String = Wallet().formattedBalanceCompact()
) {
    val textColor = appTitleText()
    val isLight = !isAppDarkTheme()
    val compact = isCompactWidth()
    val barShape = RoundedCornerShape(12.dp)
    val barItemHeight = if (compact) 42.dp else 44.dp
    val fieldFontSize = if (compact) 13.sp else 14.sp
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = if (compact) 12.dp else 18.dp, vertical = if (compact) 14.dp else 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val searchContainerColor = if (isLight) {
            MaterialTheme.colorScheme.surfaceContainerHighest
        } else {
            MaterialTheme.colorScheme.surface
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .height(barItemHeight)
                .appSoftShadow(barShape, elevation = if (isLight) 6.dp else 2.dp)
                .clip(barShape)
                .background(searchContainerColor)
                .border(1.dp, appBorderColor(AppBorderWeight.Default), barShape)
                .padding(horizontal = if (compact) 10.dp else 12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Search",
                    tint = appMutedText(),
                    modifier = Modifier.size(if (compact) 18.dp else 20.dp)
                )
                Spacer(modifier = Modifier.width(if (compact) 8.dp else 10.dp))
                Box(modifier = Modifier.weight(1f)) {
                    if (searchQuery.isEmpty()) {
                        Text(
                            text = "Search models...",
                            color = appCaptionText(),
                            fontSize = fieldFontSize,
                            maxLines = 1
                        )
                    }
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChanged,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            color = appTitleText(),
                            fontSize = fieldFontSize,
                            lineHeight = fieldFontSize
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(if (compact) 8.dp else 16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(if (compact) 8.dp else 14.dp)
        ) {
            Row(
                modifier = Modifier
                    .height(barItemHeight)
                    .clip(barShape)
                    .appSoftShadow(barShape, elevation = if (isLight) 4.dp else 2.dp)
                    .background(accentHorizontalGradient())
                    .padding(horizontal = if (compact) 10.dp else 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.CurrencyRupee,
                    contentDescription = "Rupees",
                    tint = Color.White,
                    modifier = Modifier.size(if (compact) 16.dp else 18.dp)
                )
                Spacer(modifier = Modifier.width(if (compact) 4.dp else 6.dp))
                Text(
                    totalTokenBalance,
                    color = Color.White,
                    fontSize = fieldFontSize,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }

            Box(modifier = Modifier.clickable(onClick = onNotificationsClick)) {
                Icon(
                    Icons.Outlined.Notifications,
                    contentDescription = "Notifications",
                    tint = textColor,
                    modifier = Modifier.size(if (compact) 22.dp else 26.dp)
                )
                if (unreadNotificationCount > 0) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(PinkPrimary, CircleShape)
                            .border(1.5.dp, MaterialTheme.colorScheme.background, CircleShape)
                            .align(Alignment.TopEnd)
                    )
                }
            }
        }
    }
}

@Composable
fun AvailabilityBadge(status: String, modifier: Modifier = Modifier) {
    val bgColor = when (status) {
        "Online" -> appSuccessColor()
        "Busy" -> Color(0xFFF57C00)
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val textColor = if (status == "Offline") MaterialTheme.colorScheme.onSurfaceVariant else Color.White
    val label = when (status) {
        "Online" -> "Online"
        "Busy" -> "Busy"
        else -> "Offline"
    }
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(bgColor.copy(alpha = 0.85f))
            .padding(horizontal = 6.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
