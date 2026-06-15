package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.MainViewModel
import com.example.data.UserProfile
import com.example.ui.components.ReportDialog
import com.example.ui.components.ReportType
import com.example.ui.theme.PinkPrimary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun PeerVideoCallScreen(
    peerUserId: String?,
    viewModel: MainViewModel,
    onEndCall: () -> Unit
) {
    if (peerUserId.isNullOrBlank()) {
        LaunchedEffect(Unit) { onEndCall() }
        return
    }

    var currentPeerId by remember(peerUserId) { mutableStateOf(peerUserId) }
    var peer by remember(currentPeerId) { mutableStateOf(viewModel.getUserProfile(currentPeerId)) }
    var timeElapsed by remember { mutableIntStateOf(0) }
    var isMuted by remember { mutableStateOf(false) }
    var isCameraOff by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }
    var isSwitching by remember { mutableStateOf(false) }
    var switchMessage by remember { mutableStateOf("Finding someone new...") }

    val swipeOffset = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    val swipeThreshold = 100f

    LaunchedEffect(currentPeerId, isSwitching) {
        if (isSwitching) return@LaunchedEffect
        timeElapsed = 0
        while (true) {
            delay(1000)
            timeElapsed++
        }
    }

    fun switchPeer() {
        if (isSwitching) return
        scope.launch {
            isSwitching = true
            switchMessage = "Finding someone new..."
            swipeOffset.snapTo(0f)
            delay(500)
            val next = viewModel.findRandomPeerUser(excludeUserIds = setOf(currentPeerId))
            if (next != null) {
                switchMessage = "Connected with ${next.name}"
                delay(250)
                currentPeerId = next.userId
                peer = viewModel.getUserProfile(next.userId)
            } else {
                switchMessage = "No one else available right now"
                delay(900)
            }
            isSwitching = false
        }
    }

    val formattedTime = String.format("%02d:%02d", timeElapsed / 60, timeElapsed % 60)

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { translationX = swipeOffset.value }
                .pointerInput(currentPeerId, isSwitching) {
                    if (isSwitching) return@pointerInput
                    detectHorizontalDragGestures(
                        onHorizontalDrag = { _, dragAmount ->
                            scope.launch {
                                swipeOffset.snapTo(swipeOffset.value + dragAmount)
                            }
                        },
                        onDragEnd = {
                            scope.launch {
                                if (abs(swipeOffset.value) >= swipeThreshold) {
                                    val direction = if (swipeOffset.value > 0) 1f else -1f
                                    swipeOffset.animateTo(
                                        targetValue = direction * 900f,
                                        animationSpec = tween(180)
                                    )
                                    switchPeer()
                                } else {
                                    swipeOffset.animateTo(0f, animationSpec = tween(200))
                                }
                            }
                        },
                        onDragCancel = {
                            scope.launch {
                                swipeOffset.animateTo(0f, animationSpec = tween(200))
                            }
                        }
                    )
                }
        ) {
            PeerCallVideoLayer(peer = peer, isCameraOff = isCameraOff)

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.35f))
            )

            PeerCallControls(
                peer = peer,
                formattedTime = formattedTime,
                isMuted = isMuted,
                isCameraOff = isCameraOff,
                onMuteToggle = { isMuted = !isMuted },
                onCameraToggle = { isCameraOff = !isCameraOff },
                onEndCall = onEndCall,
                onReport = { showReportDialog = true }
            )
        }

        if (isSwitching) {
            PeerSwitchingOverlay(message = switchMessage)
        }
    }

    if (showReportDialog) {
        ReportDialog(
            reportedName = peer.name,
            reportType = ReportType.Call,
            callIsVideo = true,
            onDismiss = { showReportDialog = false }
        )
    }
}

@Composable
private fun BoxScope.PeerCallVideoLayer(peer: UserProfile, isCameraOff: Boolean) {
    AsyncImage(
        model = peer.avatarUrl,
        contentDescription = peer.name,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )

    if (!isCameraOff) {
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 100.dp, end = 16.dp)
                .size(width = 100.dp, height = 150.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.DarkGray.copy(alpha = 0.5f))
                .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Cameraswitch,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
private fun BoxScope.PeerCallControls(
    peer: UserProfile,
    formattedTime: String,
    isMuted: Boolean,
    isCameraOff: Boolean,
    onMuteToggle: () -> Unit,
    onCameraToggle: () -> Unit,
    onEndCall: () -> Unit,
    onReport: () -> Unit
) {
    IconButton(
        onClick = onReport,
        modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(top = 48.dp, end = 8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.ReportProblem,
            contentDescription = "Report",
            tint = Color.White.copy(alpha = 0.85f)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 48.dp, top = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                color = PinkPrimary.copy(alpha = 0.2f),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, PinkPrimary.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Videocam,
                        contentDescription = null,
                        tint = PinkPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "Random · User to User",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = peer.name,
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF00E676))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = formattedTime,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                color = Color.Black.copy(alpha = 0.45f),
                shape = RoundedCornerShape(24.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Swipe to change user",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onMuteToggle,
                    modifier = Modifier
                        .size(56.dp)
                        .background(Color.White.copy(alpha = 0.15f), CircleShape)
                ) {
                    Icon(
                        imageVector = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                        contentDescription = "Mute",
                        tint = Color.White
                    )
                }

                IconButton(
                    onClick = onEndCall,
                    modifier = Modifier
                        .size(72.dp)
                        .background(Color(0xFFFF5252), CircleShape)
                ) {
                    Icon(
                        Icons.Default.CallEnd,
                        contentDescription = "End call",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                IconButton(
                    onClick = onCameraToggle,
                    modifier = Modifier
                        .size(56.dp)
                        .background(Color.White.copy(alpha = 0.15f), CircleShape)
                ) {
                    Icon(
                        imageVector = if (isCameraOff) Icons.Default.VideocamOff else Icons.Default.Videocam,
                        contentDescription = "Camera",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun PeerSwitchingOverlay(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.82f)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = PinkPrimary, strokeWidth = 3.dp)
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = message,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }
    }
}
