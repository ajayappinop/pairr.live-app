package com.example.ui.screens.model

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.MainViewModel
import com.example.ModelCallEarning
import com.example.ui.theme.AppFilterChip
import com.example.ui.theme.OrangeSecondary
import com.example.ui.theme.PinkPrimary
import com.example.ui.theme.SoftScreenBackground
import com.example.ui.theme.appMutedText
import com.example.ui.theme.appSuccessColor
import com.example.ui.theme.appSurfaceCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelCallEarningsScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onUserClick: (String) -> Unit = {}
) {
    val textColor = MaterialTheme.colorScheme.onSurface
    val secondaryTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    val cardBg = MaterialTheme.colorScheme.surface
    val borderColor = MaterialTheme.colorScheme.outline
    val pinkGradient = Brush.horizontalGradient(listOf(PinkPrimary, OrangeSecondary))

    val callEarnings = remember { viewModel.getEarningsForCurrentModel() }
    val totalEarned = remember(callEarnings) { callEarnings.sumOf { it.amountEarned } }
    val videoTotal = remember(callEarnings) { callEarnings.filter { it.isVideo }.sumOf { it.amountEarned } }
    val audioTotal = remember(callEarnings) { callEarnings.filter { !it.isVideo }.sumOf { it.amountEarned } }

    var selectedFilter by remember { mutableStateOf("All") }
    var selectedEarning by remember { mutableStateOf<ModelCallEarning?>(null) }

    val filteredEarnings = remember(callEarnings, selectedFilter) {
        when (selectedFilter) {
            "Video" -> callEarnings.filter { it.isVideo }
            "Audio" -> callEarnings.filter { !it.isVideo }
            else -> callEarnings
        }
    }

    SoftScreenBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = textColor
                    )
                }
                Text(
                    text = "Call Earnings",
                    color = textColor,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(pinkGradient)
                            .padding(20.dp)
                    ) {
                        Column {
                            Text(
                                "Total Earned",
                                color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.85f),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                "$totalEarned Rupees",
                                color = androidx.compose.ui.graphics.Color.White,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                EarningsSummaryChip(
                                    label = "Calls",
                                    value = "${callEarnings.size}",
                                    modifier = Modifier.weight(1f)
                                )
                                EarningsSummaryChip(
                                    label = "Video",
                                    value = "$videoTotal",
                                    modifier = Modifier.weight(1f)
                                )
                                EarningsSummaryChip(
                                    label = "Audio",
                                    value = "$audioTotal",
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("All", "Video", "Audio").forEach { filter ->
                            AppFilterChip(
                                label = filter,
                                selected = selectedFilter == filter,
                                onClick = { selectedFilter = filter },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                if (filteredEarnings.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .appSurfaceCard(shape = RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "No earnings in this category yet.",
                                color = secondaryTextColor,
                                fontSize = 14.sp
                            )
                        }
                    }
                } else {
                    items(filteredEarnings, key = { it.id }) { earning ->
                        CallEarningListItem(
                            earning = earning,
                            textColor = textColor,
                            secondaryTextColor = secondaryTextColor,
                            borderColor = borderColor,
                            onClick = { selectedEarning = earning },
                            onCallerClick = {
                                val userId = viewModel.resolveUserIdByDisplayName(earning.callerName)
                                if (userId.isNotBlank()) onUserClick(userId)
                            }
                        )
                    }
                }
            }
        }
    }

    selectedEarning?.let { earning ->
        val callerUserId = viewModel.resolveUserIdByDisplayName(earning.callerName)
        ModelEarningDetailsDialog(
            earning = earning,
            cardBg = cardBg,
            textColor = textColor,
            secondaryTextColor = secondaryTextColor,
            borderColor = borderColor,
            onViewCaller = {
                if (callerUserId.isNotBlank()) {
                    selectedEarning = null
                    onUserClick(callerUserId)
                }
            },
            onDismiss = { selectedEarning = null }
        )
    }
}

@Composable
private fun EarningsSummaryChip(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(androidx.compose.ui.graphics.Color.White.copy(alpha = 0.18f))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            value,
            color = androidx.compose.ui.graphics.Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            label,
            color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.8f),
            fontSize = 11.sp
        )
    }
}

@Composable
private fun CallEarningListItem(
    earning: ModelCallEarning,
    textColor: androidx.compose.ui.graphics.Color,
    secondaryTextColor: androidx.compose.ui.graphics.Color,
    borderColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
    onCallerClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .appSurfaceCard(shape = RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(
                    if (earning.isVideo) PinkPrimary.copy(alpha = 0.12f)
                    else appSuccessColor().copy(alpha = 0.12f),
                    CircleShape
                )
                .clickable(onClick = onCallerClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                if (earning.isVideo) Icons.Default.Videocam else Icons.Default.Phone,
                contentDescription = null,
                tint = if (earning.isVideo) PinkPrimary else appSuccessColor(),
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            modifier = Modifier
                .weight(1f)
                .clickable(onClick = onCallerClick)
        ) {
            Text(
                "${if (earning.isVideo) "Video" else "Audio"} call — ${earning.callerName}",
                color = textColor,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                "${earning.duration} • ${earning.date}",
                color = secondaryTextColor,
                fontSize = 12.sp
            )
            Text(
                earning.status,
                color = appSuccessColor(),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                "+${earning.amountEarned}",
                color = appSuccessColor(),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text("Rupees", color = appMutedText(), fontSize = 10.sp)
        }
    }
}
