package com.example.ui.screens.model

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.CallBooking
import com.example.MainViewModel
import com.example.data.displayProfilePhotoUrl
import com.example.ui.components.NotificationsSheet
import com.example.ui.components.ReportDialog
import com.example.ui.components.ReportType
import com.example.ui.theme.OrangeSecondary
import com.example.ui.theme.PinkPrimary
import com.example.ui.theme.SoftScreenBackground
import com.example.ui.theme.appMutedText
import com.example.ui.theme.appSuccessColor
import com.example.ui.theme.appSurfaceCard
import com.example.ui.theme.isCompactWidth
import kotlinx.coroutines.delay

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ModelSideHomeScreen(
    viewModel: MainViewModel,
    onUserClick: (String) -> Unit = {}
) {
    val bg = MaterialTheme.colorScheme.background
    val cardBg = MaterialTheme.colorScheme.surface
    val cardBgLighter = MaterialTheme.colorScheme.surfaceVariant
    val borderColor = MaterialTheme.colorScheme.outline
    val pinkGradient = Brush.horizontalGradient(listOf(PinkPrimary, OrangeSecondary))
    val textColor = MaterialTheme.colorScheme.onSurface
    val secondaryTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    val offlineColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)
    
    val greenGlow = Color(0xFF00E676)
    val amberGlow = Color(0xFFFFB300)
    
    val radialBgGradient = Brush.radialGradient(
        colors = listOf(PinkPrimary.copy(alpha = 0.12f), Color.Transparent),
        radius = 800f
    )
    
    val availability by viewModel.modelAvailability.collectAsStateWithLifecycle()
    val dailyEarn by viewModel.modelDailyEarnings.collectAsStateWithLifecycle()
    val weeklyEarn by viewModel.modelWeeklyEarnings.collectAsStateWithLifecycle()
    val monthlyEarn by viewModel.modelMonthlyEarnings.collectAsStateWithLifecycle()
    val lifetimeEarn by viewModel.modelLifetimeEarnings.collectAsStateWithLifecycle()
    val models by viewModel.models.collectAsStateWithLifecycle()
    val currentModel = models.find { it.id == viewModel.getCurrentModelId() }
    val profilePhotoUrl = currentModel?.displayProfilePhotoUrl()
        ?: "https://i.pravatar.cc/300?u=${viewModel.getCurrentModelId() ?: "model"}"
    val modelUsername by viewModel.modelProfileUsername.collectAsStateWithLifecycle()
    val bookings by viewModel.bookings.collectAsStateWithLifecycle()
    val currentModelId = viewModel.getCurrentModelId()
    val scheduledCalls = remember(bookings, currentModelId) {
        if (currentModelId.isNullOrBlank()) emptyList()
        else bookings.filter {
            it.modelId == currentModelId && (it.status == "Scheduled" || it.status == "Accepted")
        }
    }
    val awaitingAcceptanceCount = scheduledCalls.count { it.status == "Scheduled" }
    
    var incomingCallSimulate by remember { mutableStateOf(false) }
    var activeScheduledBooking by remember { mutableStateOf<CallBooking?>(null) }
    var showCallReportDialog by remember { mutableStateOf(false) }
    var showNotificationsSheet by remember { mutableStateOf(false) }
    val modelNotifications by viewModel.modelNotifications.collectAsStateWithLifecycle()
    val unreadNotificationCount = modelNotifications.count { !it.isRead }
    val demoCallerName = "Ramesh K."
    val demoCallerUserId = remember { viewModel.resolveUserIdByDisplayName(demoCallerName) }
    val activeCallerName = activeScheduledBooking?.userName?.takeIf { it.isNotBlank() } ?: demoCallerName
    val activeCallerUserId = activeScheduledBooking?.userId?.takeIf { it.isNotBlank() }
        ?: demoCallerUserId
    val activeCallerAvatar = activeScheduledBooking?.userAvatarUrl?.takeIf { it.isNotBlank() }
        ?: "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?ixlib=rb-4.0.3&auto=format&fit=crop&w=300&q=80"
    val activeCallIsVideo = activeScheduledBooking?.isVideo ?: true
    val activeCallCost = activeScheduledBooking?.cost
    val openCallerProfile = {
        if (activeCallerUserId.isNotBlank()) onUserClick(activeCallerUserId)
    }
    
    // Breathing scale for active online simulation button
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "button_scale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    SoftScreenBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            
            // Header Section: Elegant Greeting
            Row(
                verticalAlignment = Alignment.CenterVertically, 
                horizontalArrangement = Arrangement.SpaceBetween, 
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Good day, $modelUsername! ✨",
                        color = textColor,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.5).sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(
                                    when (availability) {
                                        "Online" -> greenGlow
                                        "Busy" -> amberGlow
                                        else -> offlineColor
                                    }
                                )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Status: $availability",
                            color = appMutedText(),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier.clickable { showNotificationsSheet = true }
                    ) {
                        Icon(
                            Icons.Outlined.Notifications,
                            contentDescription = "Notifications",
                            tint = textColor,
                            modifier = Modifier.size(26.dp)
                        )
                        if (unreadNotificationCount > 0) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(PinkPrimary, CircleShape)
                                    .border(1.5.dp, bg, CircleShape)
                                    .align(Alignment.TopEnd)
                            )
                        }
                    }

                    // Advanced bordered avatar with pulsing ring
                    Box(contentAlignment = Alignment.Center) {
                    if (availability == "Online") {
                        Box(
                            modifier = Modifier
                                .size(58.dp)
                                .clip(CircleShape)
                                .background(greenGlow.copy(alpha = pulseAlpha))
                        )
                    }
                    AsyncImage(
                        model = profilePhotoUrl,
                        contentDescription = "Profile Avatar",
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .border(2.dp, PinkPrimary, CircleShape)
                            .clickable {
                                // Navigate or display info dialog
                            },
                        contentScale = ContentScale.Crop
                    )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            LifetimeRechargeCard(
                lifetimeEarn = lifetimeEarn,
                monthlyEarn = monthlyEarn,
                pinkGradient = pinkGradient
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            ModelActiveModeSection(
                availability = availability,
                textColor = textColor,
                borderColor = borderColor,
                cardBg = cardBg,
                greenGlow = greenGlow,
                amberGlow = amberGlow,
                offlineColor = offlineColor,
                onModeSelected = { viewModel.setModelAvailability(it) }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // --- TODAY'S QUICK STATS ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Today's Earnings Card
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(cardBg)
                        .border(1.dp, borderColor, RoundedCornerShape(20.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = PinkPrimary, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Today's Earnings", color = textColor.copy(alpha = 0.5f), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        Text("$dailyEarn Tokens", color = textColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
                
                // Active Requests Card
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(cardBg)
                        .border(1.dp, borderColor, RoundedCornerShape(20.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Icon(Icons.Default.FlashOn, contentDescription = null, tint = Color.Yellow, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Scheduled Calls", color = textColor.copy(alpha = 0.5f), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        Text(
                            text = if (scheduledCalls.isEmpty()) {
                                "None"
                            } else if (awaitingAcceptanceCount > 0) {
                                "$awaitingAcceptanceCount Scheduled"
                            } else {
                                "${scheduledCalls.size} Scheduled"
                            },
                            color = textColor,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            // --- SCHEDULED CALLS SECTION ---
            Text("Scheduled Call Requests", color = textColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            
            if (scheduledCalls.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .appSurfaceCard(shape = RoundedCornerShape(16.dp))
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Outlined.Schedule,
                            contentDescription = null,
                            tint = secondaryTextColor,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "No scheduled requests yet",
                            color = textColor,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            "Users who book a session with you will appear here.",
                            color = secondaryTextColor,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    scheduledCalls.forEach { booking ->
                        ScheduledCallRequestCard(
                            booking = booking,
                            textColor = textColor,
                            secondaryTextColor = secondaryTextColor,
                            cardBgLighter = cardBgLighter,
                            onUserClick = onUserClick,
                            onAccept = { viewModel.acceptBooking(booking.id) },
                            onCall = {
                                activeScheduledBooking = booking
                                incomingCallSimulate = true
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            TokenAnalyticsSection(
                dailyEarn = dailyEarn,
                weeklyEarn = weeklyEarn,
                monthlyEarn = monthlyEarn,
                textColor = textColor,
                borderColor = borderColor,
                cardBgLighter = cardBgLighter
            )
            
            Spacer(modifier = Modifier.height(28.dp))
            
            // Simulation Widget
            Text("Client Simulators", color = textColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            
            val isOnline = availability == "Online"
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .scale(if (isOnline) pulseScale else 1.0f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        if (isOnline) {
                            pinkGradient
                        } else {
                            Brush.linearGradient(listOf(borderColor, borderColor))
                        }
                    )
                    .padding(1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(23.dp))
                        .background(cardBg)
                        .clickable(enabled = isOnline) {
                            incomingCallSimulate = true
                        }
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = if (isOnline) Icons.Default.PhoneCallback else Icons.Default.PhoneDisabled, 
                        contentDescription = null, 
                        tint = if (isOnline) PinkPrimary else textColor.copy(alpha = 0.3f),
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (isOnline) "Simulate Incoming Video Call" else "Go Online to Simulate Calls",
                        color = if (isOnline) textColor else textColor.copy(alpha = 0.5f),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isOnline) "Accept high-paying clients to experience dynamic call sessions." else "Set state to ONLINE inside home screen toggles to allow calls.",
                        color = textColor.copy(alpha = 0.5f),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(100.dp))
        }
        
        // Detailed client incoming call overlay simulator
        AnimatedVisibility(
            visible = incomingCallSimulate,
            enter = fadeIn() + scaleIn(initialScale = 0.9f),
            exit = fadeOut() + scaleOut(targetScale = 0.9f)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.94f))
                    .clickable(enabled = false) {},
                contentAlignment = Alignment.Center
            ) {
                var callAccepted by remember { mutableStateOf(false) }
                
                if (!callAccepted) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Caller Identity Pulsing Ring
                        Box(contentAlignment = Alignment.Center) {
                            Box(
                                modifier = Modifier
                                    .size(150.dp)
                                    .clip(CircleShape)
                                    .background(PinkPrimary.copy(alpha = pulseAlpha))
                            )
                            Box(
                                modifier = Modifier
                                    .size(130.dp)
                                    .clip(CircleShape)
                                    .background(PinkPrimary.copy(alpha = 0.25f))
                            )
                            AsyncImage(
                                model = activeCallerAvatar,
                                contentDescription = "$activeCallerName avatar",
                                modifier = Modifier
                                    .size(110.dp)
                                    .clip(CircleShape)
                                    .border(4.dp, PinkPrimary, CircleShape)
                                    .clickable(onClick = openCallerProfile),
                                contentScale = ContentScale.Crop
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(28.dp))
                        
                        Text(
                            text = if (activeCallIsVideo) "Incoming Video Session..." else "Incoming Audio Session...",
                            color = Color.LightGray.copy(alpha = 0.8f), 
                            fontSize = 16.sp, 
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = activeCallerName, 
                            color = Color.White, 
                            fontSize = 32.sp, 
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.clickable(onClick = openCallerProfile)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(30.dp))
                                .background(PinkPrimary.copy(alpha = 0.15f))
                                .border(1.dp, PinkPrimary.copy(alpha = 0.4f), RoundedCornerShape(30.dp))
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = activeCallCost?.let { "$it Tokens reserved" } ?: "150 Tokens / min",
                                color = PinkPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(64.dp))
                        
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(40.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Decline session
                            IconButton(
                                onClick = {
                                    incomingCallSimulate = false
                                    activeScheduledBooking = null
                                },
                                modifier = Modifier
                                    .size(72.dp)
                                    .background(Color.White.copy(alpha = 0.1f), CircleShape)
                                    .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CallEnd, 
                                    contentDescription = "Reject", 
                                    tint = Color(0xFFFF5252), 
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            
                            // Accept video session
                            IconButton(
                                onClick = { callAccepted = true },
                                modifier = Modifier
                                    .scale(pulseScale)
                                    .size(80.dp)
                                    .clip(CircleShape)
                                    .background(pinkGradient)
                                    .border(2.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Call, 
                                    contentDescription = "Accept", 
                                    tint = Color.White, 
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }
                    }
                } else {
                    // Immersive Interactive Call Screen
                    var seconds by remember { mutableIntStateOf(0) }
                    var isMuted by remember { mutableStateOf(false) }
                    var isCameraOff by remember { mutableStateOf(false) }
                    
                    LaunchedEffect(Unit) {
                        while (true) {
                            delay(1000)
                            seconds++
                        }
                    }
                    val timeString = String.format("%02d:%02d", seconds / 60, seconds % 60)
                    
                    val callerName = demoCallerName
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Background full video representation
                        AsyncImage(
                            model = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80",
                            contentDescription = "Primary Client Stream",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        
                        // Film overlay contrast
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            Color.Black.copy(alpha = 0.7f),
                                            Color.Transparent,
                                            Color.Black.copy(alpha = 0.8f)
                                        )
                                    )
                                )
                        )
                        
                        // Floating Picture in picture (Model stream) with custom frame
                        if (!isCameraOff) {
                            AsyncImage(
                                model = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?ixlib=rb-4.0.3&auto=format&fit=crop&w=200&q=80",
                                contentDescription = "Model Preview PIP",
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(top = 40.dp, end = 16.dp)
                                    .size(width = 110.dp, height = 165.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .border(2.dp, PinkPrimary, RoundedCornerShape(16.dp))
                                    .border(4.dp, Color.Black.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(top = 40.dp, end = 16.dp)
                                    .size(width = 110.dp, height = 165.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(cardBg)
                                    .border(1.dp, borderColor, RoundedCornerShape(16.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.VideocamOff, contentDescription = "Cam Off", tint = Color.Gray)
                            }
                        }
                        
                        // Top Right: Report call
                        IconButton(
                            onClick = { showCallReportDialog = true },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 40.dp, end = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ReportProblem,
                                contentDescription = "Report Call",
                                tint = Color.White.copy(alpha = 0.85f)
                            )
                        }

                        // Top Left: Client Status & Live Session Analytics
                        Column(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFFF5252))
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("LIVE SESSION", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Black)
                            }
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = callerName, 
                                color = Color.White, 
                                fontSize = 26.sp, 
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable(onClick = openCallerProfile)
                            )
                            
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Timer, contentDescription = "Active Timer", tint = Color.LightGray, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(timeString, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.MonetizationOn, contentDescription = "Earned Tokens", tint = Color(0xFFFFD54F), modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "+ ${(seconds / 60) * 150} Tokens", 
                                    color = Color(0xFFFFD54F), 
                                    fontSize = 18.sp, 
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                        
                        // Interactive action row at bottom
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                                .navigationBarsPadding()
                                .padding(bottom = 48.dp, start = 16.dp, end = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier
                                    .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(40.dp))
                                    .padding(horizontal = 20.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.spacedBy(20.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Camera toggle
                                IconButton(
                                    onClick = { isCameraOff = !isCameraOff },
                                    modifier = Modifier
                                        .size(54.dp)
                                        .background(if (isCameraOff) Color.White.copy(alpha = 0.2f) else Color.Transparent, CircleShape)
                                        .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape)
                                ) {
                                    Icon(
                                        imageVector = if (isCameraOff) Icons.Default.VideocamOff else Icons.Default.Videocam, 
                                        contentDescription = "Camera Toggle", 
                                        tint = if (isCameraOff) PinkPrimary else Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                
                                // End Call
                                IconButton(
                                    onClick = {
                                        incomingCallSimulate = false
                                        activeScheduledBooking = null
                                    },
                                    modifier = Modifier
                                        .size(72.dp)
                                        .clip(CircleShape)
                                        .background(pinkGradient)
                                        .border(2.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CallEnd, 
                                        contentDescription = "Hang up", 
                                        tint = Color.White, 
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                                
                                // Mic mute toggle
                                IconButton(
                                    onClick = { isMuted = !isMuted },
                                    modifier = Modifier
                                        .size(54.dp)
                                        .background(if (isMuted) Color.White.copy(alpha = 0.2f) else Color.Transparent, CircleShape)
                                        .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape)
                                ) {
                                    Icon(
                                        imageVector = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic, 
                                        contentDescription = "Mute Toggle", 
                                        tint = if (isMuted) PinkPrimary else Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showCallReportDialog) {
            ReportDialog(
                reportedName = demoCallerName,
                reportType = ReportType.Call,
                callIsVideo = true,
                onDismiss = { showCallReportDialog = false }
            )
        }

        if (showNotificationsSheet) {
            NotificationsSheet(
                viewModel = viewModel,
                onDismiss = { showNotificationsSheet = false },
                forModel = true
            )
        }
    }
}

@Composable
private fun ModelActiveModeSection(
    availability: String,
    textColor: Color,
    borderColor: Color,
    cardBg: Color,
    greenGlow: Color,
    amberGlow: Color,
    offlineColor: Color,
    onModeSelected: (String) -> Unit
) {
    Text("Set Active Mode", color = textColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(12.dp))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(cardBg, RoundedCornerShape(18.dp))
            .border(1.dp, borderColor, RoundedCornerShape(18.dp))
            .padding(6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        val statuses = listOf(
            Triple("Online", greenGlow, Icons.Default.Circle),
            Triple("Busy", amberGlow, Icons.Default.PauseCircle),
            Triple("Offline", offlineColor, Icons.Default.Cancel)
        )

        statuses.forEach { (status, color, icon) ->
            val isSelected = availability == status
            Surface(
                color = if (isSelected) color.copy(alpha = 0.12f) else Color.Transparent,
                contentColor = if (isSelected) color else textColor.copy(alpha = 0.5f),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .weight(1f)
                    .clickable { onModeSelected(status) }
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isSelected) color else textColor.copy(alpha = 0.3f),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = status,
                        color = if (isSelected) textColor else textColor.copy(alpha = 0.5f),
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun LifetimeRechargeCard(
    lifetimeEarn: Int,
    monthlyEarn: Int,
    pinkGradient: Brush
) {
    val monthlyShare = if (lifetimeEarn > 0) {
        (monthlyEarn.toFloat() / lifetimeEarn).coerceIn(0f, 1f)
    } else {
        0f
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(pinkGradient)
            .padding(18.dp)
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color.White.copy(alpha = 0.18f), Color.Transparent),
                        radius = 420f
                    )
                )
        )

        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Diamond,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            "Lifetime Recharge Value",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            "Total tokens earned all-time",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 11.sp
                        )
                    }
                }
                Surface(
                    color = Color.White.copy(alpha = 0.18f),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.28f))
                ) {
                    Text(
                        "Diamond 💎",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = String.format("%,d", lifetimeEarn),
                    color = Color.White,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-1).sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Tokens",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                LifetimeRechargeStatChip(
                    label = "This Month",
                    value = String.format("%,d", monthlyEarn),
                    modifier = Modifier.weight(1f)
                )
                LifetimeRechargeStatChip(
                    label = "Lifetime Share",
                    value = "${(monthlyShare * 100).toInt()}%",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Monthly contribution",
                        color = Color.White.copy(alpha = 0.75f),
                        fontSize = 11.sp
                    )
                    Text(
                        "${(monthlyShare * 100).toInt()}%",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { monthlyShare },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = Color.White,
                    trackColor = Color.White.copy(alpha = 0.22f)
                )
            }
        }
    }
}

@Composable
private fun LifetimeRechargeStatChip(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White.copy(alpha = 0.14f))
            .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Text(label, color = Color.White.copy(alpha = 0.75f), fontSize = 10.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Text(value, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun TokenAnalyticsSection(
    dailyEarn: Int,
    weeklyEarn: Int,
    monthlyEarn: Int,
    textColor: Color,
    borderColor: Color,
    cardBgLighter: Color
) {
    var selectedPeriod by remember { mutableStateOf("Weekly") }
    val maxVal = maxOf(dailyEarn, weeklyEarn, monthlyEarn, 1).toFloat()

    val periods = listOf(
        Triple("Daily", dailyEarn, Icons.Outlined.Today to Color(0xFFFFB300)),
        Triple("Weekly", weeklyEarn, Icons.Outlined.DateRange to PinkPrimary),
        Triple("Monthly", monthlyEarn, Icons.Outlined.CalendarMonth to appSuccessColor())
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Token Analytics", color = textColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(
                    "Track how your earnings grow over time",
                    color = textColor.copy(alpha = 0.55f),
                    fontSize = 12.sp
                )
            }
            Icon(
                Icons.Outlined.Insights,
                contentDescription = null,
                tint = PinkPrimary,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .appSurfaceCard(shape = RoundedCornerShape(22.dp))
                .padding(16.dp)
        ) {
            periods.forEachIndexed { index, (label, amount, iconTint) ->
                val (icon, accent) = iconTint
                val isSelected = selectedPeriod == label
                val progress = amount / maxVal

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) cardBgLighter else Color.Transparent)
                        .clickable { selectedPeriod = label }
                        .padding(horizontal = 10.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(accent.copy(alpha = 0.14f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                label,
                                color = if (isSelected) textColor else textColor.copy(alpha = 0.7f),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                String.format("%,d", amount),
                                color = if (isSelected) accent else textColor.copy(alpha = 0.6f),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(7.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(borderColor.copy(alpha = 0.35f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(progress.coerceIn(0.08f, 1f))
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(accent.copy(alpha = 0.65f), accent)
                                        )
                                    )
                            )
                        }
                    }
                }

                if (index < periods.lastIndex) {
                    HorizontalDivider(
                        color = borderColor.copy(alpha = 0.45f),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            val selectedAmount = when (selectedPeriod) {
                "Daily" -> dailyEarn
                "Weekly" -> weeklyEarn
                else -> monthlyEarn
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(PinkPrimary.copy(alpha = 0.08f))
                    .border(1.dp, PinkPrimary.copy(alpha = 0.18f), RoundedCornerShape(14.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.TrendingUp, contentDescription = null, tint = PinkPrimary, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "$selectedPeriod intake: ${String.format("%,d", selectedAmount)} tokens · Strong performance",
                    color = textColor.copy(alpha = 0.75f),
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
private fun ScheduledCallRequestCard(
    booking: CallBooking,
    textColor: Color,
    secondaryTextColor: Color,
    cardBgLighter: Color,
    onUserClick: (String) -> Unit,
    onAccept: () -> Unit,
    onCall: () -> Unit
) {
    val isAccepted = booking.status == "Accepted"
    val callTypeLabel = if (booking.isVideo) "Video" else "Audio"
    val callTypeColor = if (booking.isVideo) PinkPrimary else appSuccessColor()
    val compact = isCompactWidth()
    val userId = booking.userId
    val avatarUrl = booking.userAvatarUrl.ifBlank {
        "https://i.pravatar.cc/300?u=${userId.ifBlank { booking.userName }}"
    }
    val displayName = booking.userName.ifBlank { "User" }
    val timeSummary = booking.timeSlot.substringBefore(" -").trim()

    val cardModifier = Modifier
        .fillMaxWidth()
        .appSurfaceCard(shape = RoundedCornerShape(16.dp))
        .padding(12.dp)

    if (compact) {
        Column(modifier = cardModifier) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = avatarUrl,
                    contentDescription = displayName,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(cardBgLighter)
                        .clickable(enabled = userId.isNotBlank()) {
                            if (userId.isNotBlank()) onUserClick(userId)
                        },
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(enabled = userId.isNotBlank()) {
                            if (userId.isNotBlank()) onUserClick(userId)
                        }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = displayName,
                            color = textColor,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        if (isAccepted) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Confirmed",
                                color = appSuccessColor(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(appSuccessColor().copy(alpha = 0.12f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (booking.isVideo) Icons.Default.Videocam else Icons.Default.Call,
                            contentDescription = null,
                            tint = callTypeColor,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$callTypeLabel · ${booking.date} · $timeSummary",
                            color = secondaryTextColor,
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Text(
                        text = "${booking.cost} tokens paid",
                        color = callTypeColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = if (isAccepted) onCall else onAccept,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isAccepted) appSuccessColor() else PinkPrimary
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().height(36.dp)
            ) {
                Text(
                    text = if (isAccepted) "Call" else "Accept",
                    fontSize = 12.sp,
                    color = Color.White
                )
            }
        }
        return
    }

    Row(
        modifier = cardModifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = avatarUrl,
            contentDescription = displayName,
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(cardBgLighter)
                .clickable(enabled = userId.isNotBlank()) {
                    if (userId.isNotBlank()) onUserClick(userId)
                },
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            modifier = Modifier
                .weight(1f)
                .clickable(enabled = userId.isNotBlank()) {
                    if (userId.isNotBlank()) onUserClick(userId)
                }
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = displayName,
                    color = textColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                if (isAccepted) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Confirmed",
                        color = appSuccessColor(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(appSuccessColor().copy(alpha = 0.12f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (booking.isVideo) Icons.Default.Videocam else Icons.Default.Call,
                    contentDescription = null,
                    tint = callTypeColor,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "$callTypeLabel · ${booking.date} · $timeSummary",
                    color = secondaryTextColor,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                text = "${booking.cost} tokens paid",
                color = callTypeColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        Button(
            onClick = if (isAccepted) onCall else onAccept,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isAccepted) appSuccessColor() else PinkPrimary
            ),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
            modifier = Modifier.height(32.dp)
        ) {
            Text(
                text = if (isAccepted) "Call" else "Accept",
                fontSize = 12.sp,
                color = Color.White
            )
        }
    }
}
