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
import com.example.data.displayProfilePhotoUrl
import com.example.ui.components.NotificationsSheet
import com.example.ui.theme.OrangeSecondary
import com.example.ui.theme.PinkPrimary
import com.example.ui.theme.SoftScreenBackground
import com.example.ui.theme.appMutedText
import com.example.ui.theme.appSuccessColor
import com.example.ui.theme.appSurfaceCard
import com.example.ui.theme.isCompactWidth


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
    
    var showNotificationsSheet by remember { mutableStateOf(false) }
    val modelNotifications by viewModel.modelNotifications.collectAsStateWithLifecycle()
    val unreadNotificationCount = modelNotifications.count { !it.isRead }
    
    // Breathing scale for online status indicator
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
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
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
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
                                .border(2.dp, PinkPrimary, CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Column(modifier = Modifier.weight(1f, fill = false)) {
                        Text(
                            text = "Hi, $modelUsername! \u2728",
                            color = textColor,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-0.5).sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
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
                }

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
                        Text("$dailyEarn Rupees", color = textColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
                
                // Weekly Earnings Card
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(cardBg)
                        .border(1.dp, borderColor, RoundedCornerShape(20.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Icon(Icons.Default.TrendingUp, contentDescription = null, tint = Color.Yellow, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Weekly Earnings", color = textColor.copy(alpha = 0.5f), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        Text("$weeklyEarn Rupees", color = textColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
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
            
            Spacer(modifier = Modifier.height(100.dp))
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
                            Icons.Default.AccountBalanceWallet,
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
                            "Total rupees earned all-time",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 11.sp
                        )
                    }
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
                    "Rupees",
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
                Text("Rupee Analytics", color = textColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
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
                    "$selectedPeriod intake: ${String.format("%,d", selectedAmount)} rupees · Strong performance",
                    color = textColor.copy(alpha = 0.75f),
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }
        }
    }
}
