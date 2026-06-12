package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.MainViewModel
import com.example.data.ChatMessage
import com.example.ui.theme.OrangeSecondary
import com.example.ui.theme.PinkPrimary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    threadId: String,
    participantName: String,
    isModelSide: Boolean,
    showBackButton: Boolean,
    onBack: () -> Unit,
    viewModel: MainViewModel,
    participantAvatarUrl: String? = null,
    onViewProfile: (() -> Unit)? = null
) {
    val allMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val threads by viewModel.chatThreads.collectAsStateWithLifecycle()
    val messages = allMessages[threadId] ?: emptyList()

    val thread = threads.find { it.id == threadId }
    val avatarUrl = participantAvatarUrl
        ?: thread?.participantAvatarUrl
        ?: "https://i.pravatar.cc/150?u=$threadId"
    val isOnline = thread?.isOnline == true

    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    DisposableEffect(threadId) {
        viewModel.setActiveThread(threadId)
        viewModel.listenToThreadMessages(threadId)
        viewModel.markThreadAsRead(threadId)
        onDispose {
            viewModel.setActiveThread(null)
            viewModel.stopListeningToMessages()
        }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            coroutineScope.launch {
                listState.animateScrollToItem(messages.size - 1)
            }
        }
    }

    val bg = MaterialTheme.colorScheme.background
    val cardBg = MaterialTheme.colorScheme.surface
    val textColor = MaterialTheme.colorScheme.onSurface
    val pinkGradient = Brush.horizontalGradient(listOf(PinkPrimary, OrangeSecondary))

    Scaffold(
        containerColor = bg,
        topBar = {
            Surface(
                color = cardBg,
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (showBackButton) {
                        IconButton(onClick = onBack) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = textColor
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    Box {
                        AsyncImage(
                            model = avatarUrl,
                            contentDescription = participantName,
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        if (isOnline) {
                            Icon(
                                Icons.Default.Circle,
                                contentDescription = "Online",
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier
                                    .size(12.dp)
                                    .align(Alignment.BottomEnd)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .then(
                                if (onViewProfile != null) {
                                    Modifier.clickable(onClick = onViewProfile)
                                } else {
                                    Modifier
                                }
                            )
                    ) {
                        Text(
                            text = participantName,
                            color = textColor,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isOnline) "Online" else "Offline",
                            color = if (isOnline) Color(0xFF4CAF50) else textColor.copy(alpha = 0.5f),
                            fontSize = 12.sp
                        )
                    }
                    if (onViewProfile != null) {
                        TextButton(onClick = onViewProfile) {
                            Text("Profile", color = PinkPrimary, fontSize = 13.sp)
                        }
                    }
                }
            }
        },
        bottomBar = {
            Surface(
                color = cardBg,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = {
                            Text("Type a message…", color = textColor.copy(alpha = 0.45f))
                        },
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PinkPrimary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            focusedTextColor = textColor,
                            unfocusedTextColor = textColor
                        ),
                        maxLines = 4
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    IconButton(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                viewModel.sendMessage(threadId, inputText, !isModelSide)
                                inputText = ""
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .background(pinkGradient, CircleShape)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    ) { padding ->
        if (messages.isEmpty()) {
            ConversationEmptyState(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                participantName = participantName
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                state = listState,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(messages, key = { it.id }) { message ->
                    ChatBubble(message = message, isModelSide = isModelSide)
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage, isModelSide: Boolean) {
    val isOwnMessage = if (isModelSide) !message.isFromUser else message.isFromUser
    val pinkGradient = Brush.horizontalGradient(listOf(PinkPrimary, OrangeSecondary))
    val textColor = MaterialTheme.colorScheme.onSurface

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isOwnMessage) Alignment.End else Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 18.dp,
                        topEnd = 18.dp,
                        bottomStart = if (isOwnMessage) 18.dp else 4.dp,
                        bottomEnd = if (isOwnMessage) 4.dp else 18.dp
                    )
                )
                .then(
                    if (isOwnMessage) {
                        Modifier.background(pinkGradient)
                    } else {
                        Modifier.background(MaterialTheme.colorScheme.surfaceVariant)
                    }
                )
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(
                text = message.content,
                color = if (isOwnMessage) Color.White else textColor,
                fontSize = 15.sp,
                lineHeight = 21.sp
            )
        }
        Text(
            text = message.timestamp,
            fontSize = 10.sp,
            color = textColor.copy(alpha = 0.45f),
            modifier = Modifier.padding(top = 4.dp, start = 4.dp, end = 4.dp)
        )
    }
}

@Composable
private fun ConversationEmptyState(modifier: Modifier, participantName: String) {
    val textColor = MaterialTheme.colorScheme.onSurface

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Say hello to $participantName 👋",
            color = textColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Your messages are private and secure",
            color = textColor.copy(alpha = 0.5f),
            fontSize = 13.sp
        )
    }
}
