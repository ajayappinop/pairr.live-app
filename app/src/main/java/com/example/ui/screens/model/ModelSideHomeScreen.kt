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
import com.example.MainViewModel
import com.example.ui.theme.PinkPrimary
import com.example.ui.theme.OrangeSecondary
import kotlinx.coroutines.delay

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ModelSideHomeScreen(viewModel: MainViewModel) {
    val bg = MaterialTheme.colorScheme.background
    val cardBg = MaterialTheme.colorScheme.surface
    val cardBgLighter = MaterialTheme.colorScheme.surfaceVariant
    val borderColor = MaterialTheme.colorScheme.outline
    val pinkGradient = Brush.horizontalGradient(listOf(PinkPrimary, OrangeSecondary))
    val textColor = MaterialTheme.colorScheme.onSurface
    val secondaryTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    
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
    
    var incomingCallSimulate by remember { mutableStateOf(false) }
    var selectedChartBar by remember { mutableStateOf<String?>(null) }
    
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

    Box(modifier = Modifier.fillMaxSize().background(bg)) {
        // Gradient atmosphere background
        Box(modifier = Modifier.fillMaxSize().background(radialBgGradient))
        
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
                        text = "Good day, Alessia! ✨",
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
                                        else -> Color.Gray
                                    }
                                )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Status: $availability",
                            color = textColor.copy(alpha = 0.5f),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
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
                        model = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?ixlib=rb-4.0.3&auto=format&fit=crop&w=150&q=80",
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
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Live Status Card Overview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(pinkGradient)
                    .padding(1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(23.dp))
                        .background(cardBg)
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "LIFETIME RECHARGE VALUE",
                            color = textColor.copy(alpha = 0.5f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "$lifetimeEarn",
                                color = textColor,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Tokens",
                                color = PinkPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(PinkPrimary.copy(alpha = 0.15f))
                            .border(1.dp, PinkPrimary.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("DIAMOND RANK 💎", color = PinkPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Availability controls
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
                    Triple("Offline", Color.Gray, Icons.Default.Cancel)
                )
                
                statuses.forEach { (status, color, icon) ->
                    val isSelected = availability == status
                    Surface(
                        color = if (isSelected) color.copy(alpha = 0.12f) else Color.Transparent,
                        contentColor = if (isSelected) color else textColor.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.setModelAvailability(status) }
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
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
            
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
                        Text("Active Requests", color = textColor.copy(alpha = 0.5f), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        Text("3 Pending", color = textColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            // --- PENDING REQUESTS SECTION ---
            Text("Pending Call Requests", color = textColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            
            val pendingRequests = listOf(
                Pair("Rahul Sharma", "150 Tokens/min"),
                Pair("Vikram Singh", "180 Tokens/min"),
                Pair("Ananya K.", "200 Tokens/min")
            )
            
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                pendingRequests.forEach { (name, rate) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(cardBg, RoundedCornerShape(16.dp))
                            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(cardBgLighter),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(name.take(1), color = PinkPrimary, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(name, color = textColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(rate, color = textColor.copy(alpha = 0.5f), fontSize = 12.sp)
                        }
                        Button(
                            onClick = { incomingCallSimulate = true },
                            colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("Accept", fontSize = 12.sp, color = Color.White)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            // Analytics / Performance Overview Card
            Text("Token Analytics", color = textColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(cardBg, RoundedCornerShape(24.dp))
                    .border(1.dp, borderColor, RoundedCornerShape(24.dp))
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Earnings Pulse Tracker", color = textColor, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("Compare daily, weekly and monthly intake", color = textColor.copy(alpha = 0.5f), fontSize = 12.sp)
                    }
                    IconButton(
                        onClick = { selectedChartBar = null },
                        colors = IconButtonDefaults.iconButtonColors(containerColor = cardBgLighter)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reset Chart", tint = textColor.copy(alpha = 0.5f))
                    }
                }
                
                Spacer(modifier = Modifier.height(28.dp))
                
                val maxVal = maxOf(dailyEarn, weeklyEarn, monthlyEarn, 1).toFloat()
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    // Daily Card/Bar
                    val isDailySelected = selectedChartBar == "Daily"
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { selectedChartBar = "Daily" }
                            .background(if (isDailySelected) cardBgLighter else Color.Transparent)
                            .padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = "${dailyEarn} Tokens", 
                            color = if (isDailySelected) textColor else textColor.copy(alpha = 0.5f), 
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .width(36.dp)
                                .fillMaxHeight(dailyEarn / maxVal)
                                .background(
                                    brush = Brush.verticalGradient(listOf(Color(0xFFFFD54F), Color(0xFFFF8F00))),
                                    shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
                                )
                                .border(
                                    width = if (isDailySelected) 2.dp else 0.dp,
                                    color = textColor,
                                    shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
                                )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Daily", 
                            color = if (isDailySelected) PinkPrimary else textColor.copy(alpha = 0.4f), 
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    // Weekly Card/Bar
                    val isWeeklySelected = selectedChartBar == "Weekly"
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { selectedChartBar = "Weekly" }
                            .background(if (isWeeklySelected) cardBgLighter else Color.Transparent)
                            .padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = "${weeklyEarn} Tokens", 
                            color = if (isWeeklySelected) textColor else textColor.copy(alpha = 0.5f), 
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .width(36.dp)
                                .fillMaxHeight(weeklyEarn / maxVal)
                                .background(
                                    brush = Brush.verticalGradient(listOf(PinkPrimary, OrangeSecondary)),
                                    shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
                                )
                                .border(
                                    width = if (isWeeklySelected) 2.dp else 0.dp,
                                    color = textColor,
                                    shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
                                )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Weekly", 
                            color = if (isWeeklySelected) PinkPrimary else textColor.copy(alpha = 0.4f), 
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    // Monthly Card/Bar
                    val isMonthlySelected = selectedChartBar == "Monthly"
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { selectedChartBar = "Monthly" }
                            .background(if (isMonthlySelected) cardBgLighter else Color.Transparent)
                            .padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = "${monthlyEarn} Tokens", 
                            color = if (isMonthlySelected) textColor else textColor.copy(alpha = 0.5f), 
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .width(36.dp)
                                .fillMaxHeight(monthlyEarn / maxVal)
                                .background(
                                    brush = Brush.verticalGradient(listOf(Color(0xFF81C784), Color(0xFF2E7D32))),
                                    shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
                                )
                                .border(
                                    width = if (isMonthlySelected) 2.dp else 0.dp,
                                    color = textColor,
                                    shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
                                )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Monthly", 
                            color = if (isMonthlySelected) Color.Green else textColor.copy(alpha = 0.4f), 
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                // Explanatory breakdown text context
                AnimatedVisibility(
                    visible = selectedChartBar != null,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    val label = selectedChartBar ?: ""
                    val amount = when (label) {
                        "Daily" -> dailyEarn
                        "Weekly" -> weeklyEarn
                        "Monthly" -> monthlyEarn
                        else -> 0
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                            .background(cardBgLighter, RoundedCornerShape(14.dp))
                            .padding(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.TrendingUp, contentDescription = null, tint = PinkPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("$label Activity Statement", color = textColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Your intake is highly positive! Average rating generated from $amount tokens is 4.98 stars. Well done!",
                            color = textColor.copy(alpha = 0.5f),
                            fontSize = 12.sp
                        )
                    }
                }
            }
            
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
                                model = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?ixlib=rb-4.0.3&auto=format&fit=crop&w=300&q=80",
                                contentDescription = "Ramesh Caller Avatar",
                                modifier = Modifier
                                    .size(110.dp)
                                    .clip(CircleShape)
                                    .border(4.dp, PinkPrimary, CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(28.dp))
                        
                        Text(
                            text = "Incoming Video Session...", 
                            color = Color.LightGray.copy(alpha = 0.8f), 
                            fontSize = 16.sp, 
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Ramesh K.", 
                            color = Color.White, 
                            fontSize = 32.sp, 
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(30.dp))
                                .background(PinkPrimary.copy(alpha = 0.15f))
                                .border(1.dp, PinkPrimary.copy(alpha = 0.4f), RoundedCornerShape(30.dp))
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text("150 Tokens / min", color = PinkPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        
                        Spacer(modifier = Modifier.height(64.dp))
                        
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(40.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Decline session
                            IconButton(
                                onClick = { incomingCallSimulate = false },
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
                                text = "Ramesh K.", 
                                color = Color.White, 
                                fontSize = 26.sp, 
                                fontWeight = FontWeight.Bold
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
                                    onClick = { incomingCallSimulate = false },
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
    }
}
