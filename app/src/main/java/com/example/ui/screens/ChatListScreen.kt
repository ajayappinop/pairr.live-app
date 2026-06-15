package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.ChatBubbleOutline
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.MainViewModel
import com.example.data.ChatThread
import com.example.ui.theme.OrangeSecondary
import com.example.ui.theme.PinkPrimary
import com.example.ui.theme.SoftScreenBackground
import com.example.ui.theme.appBorderColor
import com.example.ui.theme.appBodyText
import com.example.ui.theme.appCaptionText
import com.example.ui.theme.appMutedText
import com.example.ui.theme.appOutlinedFieldColors
import com.example.ui.theme.appSecondaryText
import com.example.ui.theme.appSoftShadow
import com.example.ui.theme.appSurfaceCard
import com.example.ui.theme.appSuccessColor
import com.example.ui.theme.appTitleText
import com.example.ui.theme.AppBorderWeight
import com.example.ui.theme.isAppDarkTheme

@Composable
fun ChatTabScreen(
    viewModel: MainViewModel,
    isModelSide: Boolean,
    onUserClick: ((String) -> Unit)? = null
) {
    var selectedThreadId by remember { mutableStateOf<String?>(null) }
    var selectedThreadName by remember { mutableStateOf("") }
    var selectedThreadUserId by remember { mutableStateOf<String?>(null) }

    val chatThreads by viewModel.chatThreads.collectAsStateWithLifecycle()
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val currentUserId by viewModel.currentUserId.collectAsStateWithLifecycle()
    val isModelMode by viewModel.isModelMode.collectAsStateWithLifecycle()

    LaunchedEffect(currentUserId, isModelSide, isModelMode) {
        if (currentUserId != null) {
            viewModel.startChatSync()
        }
    }

    val filteredThreads = viewModel.getThreadsForMode(isModelSide)

    if (selectedThreadId != null) {
        ChatScreen(
            threadId = selectedThreadId!!,
            participantName = selectedThreadName,
            isModelSide = isModelSide,
            showBackButton = true,
            onBack = {
                selectedThreadId = null
                selectedThreadName = ""
                selectedThreadUserId = null
            },
            viewModel = viewModel,
            onViewProfile = if (isModelSide && onUserClick != null) {
                {
                    selectedThreadUserId?.let { onUserClick.invoke(it) }
                }
            } else null
        )
    } else {
        ChatListScreen(
            threads = filteredThreads,
            isModelSide = isModelSide,
            onThreadClick = { thread ->
                viewModel.markThreadAsRead(thread.id)
                selectedThreadId = thread.id
                selectedThreadName = thread.participantName
                selectedThreadUserId = if (isModelSide) thread.userId else null
            },
            onUserProfileClick = if (isModelSide && onUserClick != null) {
                { thread ->
                    if (thread.userId.isNotBlank()) onUserClick.invoke(thread.userId)
                }
            } else null
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    threads: List<ChatThread>,
    isModelSide: Boolean,
    onThreadClick: (ChatThread) -> Unit,
    onUserProfileClick: ((ChatThread) -> Unit)? = null
) {
    var searchQuery by remember { mutableStateOf("") }
    val cardShape = RoundedCornerShape(20.dp)
    val isLight = !isAppDarkTheme()
    val pinkGradient = Brush.horizontalGradient(listOf(PinkPrimary, OrangeSecondary))

    val displayedThreads = remember(threads, searchQuery) {
        if (searchQuery.isBlank()) threads
        else threads.filter {
            it.participantName.contains(searchQuery, ignoreCase = true) ||
                it.lastMessage.contains(searchQuery, ignoreCase = true)
        }
    }

    SoftScreenBackground {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    if (isLight) {
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f),
                                Color.Transparent
                            )
                        )
                    } else {
                        Brush.verticalGradient(
                            colors = listOf(
                                PinkPrimary.copy(alpha = 0.12f),
                                Color.Transparent
                            )
                        )
                    }
                )
                .padding(horizontal = 18.dp, vertical = 20.dp)
        ) {
            Column {
                Text(
                    text = if (isModelSide) "Messages" else "Chats",
                    color = appTitleText(),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = if (isModelSide) "Conversations with your fans" else "Stay connected with models",
                    color = appSecondaryText(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Search
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
                .height(50.dp)
                .appSoftShadow(RoundedCornerShape(16.dp), elevation = if (isLight) 6.dp else 2.dp),
            placeholder = { Text("Search conversations…", color = appCaptionText()) },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Search", tint = appMutedText())
            },
            shape = RoundedCornerShape(16.dp),
            colors = appOutlinedFieldColors(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (displayedThreads.isEmpty()) {
            ChatEmptyState(isModelSide = isModelSide, isSearching = searchQuery.isNotBlank())
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(displayedThreads, key = { it.id }) { thread ->
                    ChatThreadItem(
                        thread = thread,
                        pinkGradient = pinkGradient,
                        onClick = { onThreadClick(thread) },
                        onAvatarClick = onUserProfileClick?.let { callback -> { callback(thread) } }
                    )
                }
                item { Spacer(modifier = Modifier.height(100.dp)) }
            }
        }
    }
    }
}

@Composable
private fun ChatThreadItem(
    thread: ChatThread,
    pinkGradient: Brush,
    onClick: () -> Unit,
    onAvatarClick: (() -> Unit)? = null
) {
    val itemShape = RoundedCornerShape(20.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .appSurfaceCard(shape = itemShape, borderWeight = AppBorderWeight.Default)
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = if (onAvatarClick != null) {
                Modifier.clickable(onClick = onAvatarClick)
            } else {
                Modifier
            }
        ) {
            AsyncImage(
                model = thread.participantAvatarUrl,
                contentDescription = thread.participantName,
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .border(2.dp, appBorderColor(AppBorderWeight.Subtle), CircleShape),
                contentScale = ContentScale.Crop
            )
            if (thread.isOnline) {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .align(Alignment.BottomEnd)
                        .offset(x = 2.dp, y = 2.dp)
                        .background(appSuccessColor(), CircleShape)
                        .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                )
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = thread.participantName,
                    color = appTitleText(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .then(
                            if (onAvatarClick != null) {
                                Modifier.clickable(onClick = onAvatarClick)
                            } else {
                                Modifier
                            }
                        )
                )
                if (thread.lastMessageTime.isNotBlank()) {
                    Text(
                        text = thread.lastMessageTime,
                        color = if (thread.unreadCount > 0) PinkPrimary else appCaptionText(),
                        fontSize = 12.sp,
                        fontWeight = if (thread.unreadCount > 0) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = thread.lastMessage,
                color = if (thread.unreadCount > 0) appBodyText() else appMutedText(),
                fontSize = 14.sp,
                fontWeight = if (thread.unreadCount > 0) FontWeight.SemiBold else FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (thread.unreadCount > 0) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .background(pinkGradient, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (thread.unreadCount > 9) "9+" else thread.unreadCount.toString(),
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun ChatEmptyState(isModelSide: Boolean, isSearching: Boolean) {
    val textColor = appTitleText()
    val secondaryText = appSecondaryText()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(PinkPrimary.copy(alpha = 0.12f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Outlined.ChatBubbleOutline,
                contentDescription = null,
                tint = PinkPrimary,
                modifier = Modifier.size(36.dp)
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = when {
                isSearching -> "No conversations found"
                isModelSide -> "No fan messages yet"
                else -> "No chats yet"
            },
            color = textColor,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = when {
                isSearching -> "Try a different search term"
                isModelSide -> "When users message you, they'll appear here"
                else -> "Tap a model's profile to start chatting"
            },
            color = secondaryText,
            fontSize = 14.sp
        )
    }
}
