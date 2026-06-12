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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.MainViewModel
import com.example.data.UserProfile
import com.example.ui.components.FullScreenImageDialog
import com.example.ui.theme.OrangeSecondary
import com.example.ui.theme.PinkPrimary

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun UserDetailScreen(
    userId: String?,
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onChat: (String) -> Unit
) {
    val context = LocalContext.current
    val blockedUsers by viewModel.blockedUsers.collectAsState()
    val user = remember(userId) {
        userId?.let { viewModel.getUserProfile(it) }
    }
    var showBlockDialog by remember { mutableStateOf(false) }
    var showFullImage by remember { mutableStateOf(false) }

    val bg = MaterialTheme.colorScheme.background
    val cardBg = MaterialTheme.colorScheme.surface
    val textColor = MaterialTheme.colorScheme.onSurface
    val secondaryText = textColor.copy(alpha = 0.6f)
    val borderColor = MaterialTheme.colorScheme.outline
    val pinkGradient = Brush.horizontalGradient(listOf(PinkPrimary, OrangeSecondary))

    if (user == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("User not found", color = textColor)
        }
        return
    }

    val isBlocked = blockedUsers.any { it.id == user.id }

    if (showFullImage) {
        FullScreenImageDialog(
            imageUrl = user.avatarUrl,
            imageIndex = 0,
            totalImages = 1,
            onDismiss = { showFullImage = false }
        )
    }

    if (showBlockDialog) {
        AlertDialog(
            onDismissRequest = { showBlockDialog = false },
            title = { Text("Block ${user.name}?") },
            text = { Text("They won't be able to message or call you. You can unblock them anytime from Settings.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.blockUser(user.id, user.name, user.avatarUrl)
                    showBlockDialog = false
                    Toast.makeText(context, "${user.name} blocked", Toast.LENGTH_SHORT).show()
                    onBack()
                }) {
                    Text("Block", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showBlockDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        containerColor = bg,
        topBar = {
            TopAppBar(
                title = {
                    Text(user.name, fontWeight = FontWeight.Bold, color = textColor)
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
                    IconButton(onClick = { onChat(user.id) }) {
                        Icon(
                            Icons.Default.ChatBubbleOutline,
                            contentDescription = "Chat",
                            tint = textColor
                        )
                    }
                    IconButton(
                        onClick = {
                            if (isBlocked) {
                                viewModel.unblockUser(user.id)
                                Toast.makeText(context, "${user.name} unblocked", Toast.LENGTH_SHORT).show()
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
            Surface(color = cardBg, shadowElevation = 8.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { onChat(user.id) },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary)
                    ) {
                        Icon(Icons.Default.ChatBubbleOutline, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Chat", color = Color.White)
                    }
                    OutlinedButton(
                        onClick = {
                            Toast.makeText(context, "Audio call with ${user.name} coming soon", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Icon(Icons.Default.Call, contentDescription = null, tint = PinkPrimary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Audio", color = PinkPrimary, fontSize = 13.sp)
                    }
                    OutlinedButton(
                        onClick = {
                            Toast.makeText(context, "Video call with ${user.name} coming soon", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Icon(Icons.Default.Videocam, contentDescription = null, tint = PinkPrimary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Video", color = PinkPrimary, fontSize = 13.sp)
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
            ) {
                AsyncImage(
                    model = user.avatarUrl,
                    contentDescription = user.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { showFullImage = true },
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, bg.copy(alpha = 0.95f))
                            )
                        )
                )
            }

            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = user.name,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                        modifier = Modifier.weight(1f)
                    )
                    UserOnlineBadge(isOnline = user.isOnline)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = PinkPrimary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${user.age} • ${user.gender}",
                        color = secondaryText,
                        fontSize = 15.sp
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = PinkPrimary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = user.location, color = secondaryText, fontSize = 15.sp)
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    UserStatCard(
                        icon = Icons.Default.Phone,
                        label = "Total Calls",
                        value = user.totalCalls.toString(),
                        cardBg = cardBg,
                        borderColor = borderColor,
                        textColor = textColor,
                        modifier = Modifier.weight(1f)
                    )
                    UserStatCard(
                        icon = Icons.Default.DateRange,
                        label = "Member Since",
                        value = user.memberSince,
                        cardBg = cardBg,
                        borderColor = borderColor,
                        textColor = textColor,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "About",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = user.bio,
                    color = secondaryText,
                    fontSize = 15.sp,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Interests",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                Spacer(modifier = Modifier.height(10.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    user.interests.forEach { interest ->
                        SuggestionChip(
                            onClick = { },
                            label = { Text(interest, color = PinkPrimary, fontSize = 13.sp) },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = cardBg,
                                labelColor = PinkPrimary
                            ),
                            border = SuggestionChipDefaults.suggestionChipBorder(
                                enabled = true,
                                borderColor = borderColor
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(pinkGradient)
                        .padding(16.dp)
                ) {
                    Column {
                        Text(
                            text = "User ID",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 12.sp
                        )
                        Text(
                            text = user.id,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun UserOnlineBadge(isOnline: Boolean) {
    val label = if (isOnline) "Online" else "Offline"
    val color = if (isOnline) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
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
        Icon(icon, contentDescription = null, tint = PinkPrimary, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = value, color = textColor, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(
            text = label,
            color = textColor.copy(alpha = 0.55f),
            fontSize = 12.sp
        )
    }
}
