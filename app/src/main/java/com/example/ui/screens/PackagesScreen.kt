package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.MainViewModel
import com.example.ui.theme.AppFilterChip
import com.example.ui.theme.OrangeSecondary
import com.example.ui.theme.PinkPrimary
import com.example.ui.theme.SoftScreenBackground
import com.example.ui.theme.appErrorColor
import com.example.ui.theme.appErrorContainer
import com.example.ui.theme.appMutedText
import com.example.ui.theme.appSuccessColor
import com.example.ui.theme.appSuccessContainer
import com.example.ui.theme.appSurfaceCard
import com.example.ui.theme.appVideoAccentColor
import com.example.ui.theme.appVideoAccentContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PackagesScreen(
    viewModel: MainViewModel,
    initialFilter: String = "All",
    onBack: () -> Unit
) {
    val bg = MaterialTheme.colorScheme.background
    val cardBg = MaterialTheme.colorScheme.surface
    val borderColor = MaterialTheme.colorScheme.outline
    val pinkGradient = Brush.horizontalGradient(listOf(PinkPrimary, OrangeSecondary))
    val textColor = MaterialTheme.colorScheme.onSurface
    
    val walletState by viewModel.walletState.collectAsState()
    var selectedFilter by remember { mutableStateOf(initialFilter) } // "All", "Audio", "Video"
    var showPurchaseConfirmDialog by remember { mutableStateOf<TokenPlan?>(null) }
    val context = LocalContext.current

    // Base Token packages
    val tokenPlans = listOf(
        TokenPlan("Starter Audio Pack", 50, "$5.00", "Perfect for a quick audio call", isVideo = false),
        TokenPlan("Standard Audio Pack", 250, "$20.00", "Save 10% on audio calls", isVideo = false),
        TokenPlan("Premium Audio Pack", 600, "$45.00", "Save 20% + Extra Minutes", isVideo = false),
        TokenPlan("Unlimited Audio VIP", 1500, "$99.00", "Save 30% + VIP Priority", isVideo = false),
        
        TokenPlan("Starter Video Pack", 50, "$10.00", "Try out high-quality streams", isVideo = true),
        TokenPlan("Standard Video Pack", 250, "$40.00", "Save 15% on video calls", isVideo = true),
        TokenPlan("Premium Video Pack", 600, "$80.00", "Save 25% + HD support", isVideo = true),
        TokenPlan("Supreme Video VIP", 1500, "$180.00", "Save 35% + Maximum savings", isVideo = true)
    )

    val filteredPlans = remember(selectedFilter, tokenPlans) {
        when (selectedFilter) {
            "Audio" -> tokenPlans.filter { !it.isVideo }
            "Video" -> tokenPlans.filter { it.isVideo }
            else -> tokenPlans
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Token Store",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        SoftScreenBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            
            // Stats & Balances Box summary (Matches Transactions style but for token store)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Audio Balance
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .appSurfaceCard(shape = RoundedCornerShape(18.dp))
                        .clickable { selectedFilter = "Audio" }
                        .padding(14.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(appSuccessContainer()),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Phone,
                                    contentDescription = "Audio Balance",
                                    tint = appSuccessColor(),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Audio Balance", color = appMutedText(), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "${walletState.audioBalance} Audio Tokens",
                            color = appSuccessColor(),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Video Balance
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .appSurfaceCard(shape = RoundedCornerShape(18.dp))
                        .clickable { selectedFilter = "Video" }
                        .padding(14.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(appVideoAccentContainer()),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Videocam,
                                    contentDescription = "Video Balance",
                                    tint = appVideoAccentColor(),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Video Balance", color = appMutedText(), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "${walletState.videoBalance} Video Tokens",
                            color = appVideoAccentColor(),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Filter Chips Bar (just like Transactions filter chips)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = "Filter",
                    tint = appMutedText(),
                    modifier = Modifier.size(18.dp)
                )
                
                val planFilters = listOf("All", "Audio", "Video")
                planFilters.forEach { filter ->
                    val isSelected = selectedFilter == filter
                    val filterLabel = when(filter) {
                        "Audio" -> "Audio Packs 📞"
                        "Video" -> "Video Packs 🎥"
                        else -> "All Packages 📦"
                    }
                    AppFilterChip(
                        label = filterLabel,
                        selected = isSelected,
                        onClick = { selectedFilter = filter }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // LazyColumn to Display Plans fully (same as Transactions Screen style)
            if (filteredPlans.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No packages found under this filter.", color = appMutedText())
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredPlans) { plan ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .appSurfaceCard(shape = RoundedCornerShape(18.dp))
                                .clickable { showPurchaseConfirmDialog = plan }
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(if (plan.isVideo) appVideoAccentContainer() else appSuccessContainer()),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (plan.isVideo) Icons.Default.Videocam else Icons.Default.Phone,
                                        contentDescription = if (plan.isVideo) "Video Pack" else "Audio Pack",
                                        tint = if (plan.isVideo) appVideoAccentColor() else appSuccessColor(),
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(14.dp))
                                Column {
                                    Text(
                                        plan.title,
                                        color = textColor,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${plan.tokens} Tokens • ${plan.bonus}",
                                        color = appMutedText(),
                                        fontSize = 12.sp
                                    )
                                }
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = plan.price,
                                    color = if (plan.isVideo) appVideoAccentColor() else appSuccessColor(),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Select Pack", color = PinkPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
        }
    }

    // Purchase confirmation dialog
    showPurchaseConfirmDialog?.let { plan ->
        AlertDialog(
            onDismissRequest = { showPurchaseConfirmDialog = null },
            title = {
                Text("Confirm Purchase", color = textColor, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    text = "Would you like to buy '${plan.title}' for ${plan.price}? This will immediately credit ${plan.tokens} ${if (plan.isVideo) "Video" else "Audio"} Tokens to your available balance.",
                    color = textColor.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.rechargeTokens(plan.tokens, plan.isVideo)
                        Toast.makeText(context, "Successfully purchased ${plan.tokens} ${if (plan.isVideo) "Video" else "Audio"} Tokens!", Toast.LENGTH_LONG).show()
                        showPurchaseConfirmDialog = null
                    }
                ) {
                    Text("Confirm", color = PinkPrimary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPurchaseConfirmDialog = null }) {
                    Text("Cancel", color = textColor.copy(alpha = 0.5f))
                }
            },
            containerColor = cardBg
        )
    }
}
