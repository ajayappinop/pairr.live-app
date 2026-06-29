package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.MainViewModel
import com.example.data.publicUsername
import com.example.ui.components.FullScreenImageDialog
import com.example.ui.components.BlockUserDialog
import com.example.ui.components.ReportDialog
import com.example.ui.components.ReportType
import com.example.ui.theme.PinkPrimary
import com.example.ui.theme.SoftScreenBackground
import com.example.ui.theme.appMutedText
import com.example.ui.theme.appSuccessColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailScreen(
    userId: String?,
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onChat: (String) -> Unit
) {
    val context = LocalContext.current
    val blockedUsers by viewModel.blockedUsers.collectAsStateWithLifecycle()
    val chatThreads by viewModel.chatThreads.collectAsStateWithLifecycle()
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()

    val user = remember(userId) {
        userId?.let { viewModel.getUserProfile(it) }
    }
    var showBlockDialog by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }
    var showFullImage by remember { mutableStateOf(false) }

    val textColor = MaterialTheme.colorScheme.onSurface
    val secondaryText = textColor.copy(alpha = 0.6f)
    val cardBg = MaterialTheme.colorScheme.surface

    if (user == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("User not found", color = textColor)
        }
        return
    }

    val isBlocked = blockedUsers.any { it.id == user.id }
    val publicName = user.publicUsername()
    val canChat = remember(user.id, chatThreads, chatMessages) {
        viewModel.canModelChatWithUser(user.id)
    }

    if (showFullImage) {
        FullScreenImageDialog(
            imageUrl = user.avatarUrl,
            imageIndex = 0,
            totalImages = 1,
            onDismiss = { showFullImage = false }
        )
    }

    if (showBlockDialog) {
        BlockUserDialog(
            userName = publicName,
            onConfirm = {
                viewModel.blockUser(user.id, publicName, user.avatarUrl)
                Toast.makeText(context, "$publicName blocked", Toast.LENGTH_SHORT).show()
                onBack()
            },
            onDismiss = { showBlockDialog = false }
        )
    }

    if (showReportDialog) {
        ReportDialog(
            reportedName = publicName,
            reportType = ReportType.Profile,
            onDismiss = { showReportDialog = false }
        )
    }

    SoftScreenBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(publicName, fontWeight = FontWeight.Bold, color = textColor)
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = textColor
                            )
                        }
                    },
                    actions = {
                        if (canChat) {
                            IconButton(onClick = { onChat(user.id) }) {
                                Icon(
                                    Icons.Default.ChatBubbleOutline,
                                    contentDescription = "Chat",
                                    tint = textColor
                                )
                            }
                        }
                        IconButton(onClick = { showReportDialog = true }) {
                            Icon(
                                Icons.Default.ReportProblem,
                                contentDescription = "Report",
                                tint = textColor
                            )
                        }
                        IconButton(
                            onClick = {
                                if (isBlocked) {
                                    viewModel.unblockUser(user.id)
                                    Toast.makeText(context, "$publicName unblocked", Toast.LENGTH_SHORT).show()
                                } else {
                                    showBlockDialog = true
                                }
                            }
                        ) {
                            Icon(
                                Icons.Default.Block,
                                contentDescription = if (isBlocked) "Unblock" else "Block",
                                tint = if (isBlocked) MaterialTheme.colorScheme.error else textColor
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = cardBg)
                )
            },
            bottomBar = {
                if (canChat) {
                    Surface(color = cardBg, shadowElevation = 8.dp) {
                        Button(
                            onClick = { onChat(user.id) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .navigationBarsPadding()
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary)
                        ) {
                            Icon(Icons.Default.ChatBubbleOutline, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Chat", color = Color.White)
                        }
                    }
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                AsyncImage(
                    model = user.avatarUrl,
                    contentDescription = publicName,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .clickable { showFullImage = true },
                    contentScale = ContentScale.Crop
                )

                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = publicName,
                            style = MaterialTheme.typography.headlineMedium,
                            color = textColor,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        UserOnlineBadge(isOnline = user.isOnline)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        UserStatCard(
                            icon = Icons.Default.Phone,
                            label = "Total Calls",
                            value = user.totalCalls.toString(),
                            textColor = textColor,
                            modifier = Modifier.weight(1f)
                        )
                        UserStatCard(
                            icon = Icons.Default.DateRange,
                            label = "Member Since",
                            value = user.memberSince,
                            textColor = textColor,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "About",
                        style = MaterialTheme.typography.titleLarge,
                        color = textColor,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = user.bio,
                        style = MaterialTheme.typography.bodyLarge,
                        color = secondaryText,
                        lineHeight = 22.sp
                    )

                    if (!canChat) {
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Chat unlocks after this user sends you the first message.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = appMutedText(),
                            lineHeight = 20.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun UserOnlineBadge(isOnline: Boolean) {
    val label = if (isOnline) "Online" else "Offline"
    val color = if (isOnline) appSuccessColor() else appMutedText()
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = label, color = color, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun UserStatCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    val cardBg = MaterialTheme.colorScheme.surface
    val borderColor = MaterialTheme.colorScheme.outline

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(cardBg)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = null, tint = PinkPrimary, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = value, color = textColor, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(
            text = label,
            color = appMutedText(),
            fontSize = 12.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}
