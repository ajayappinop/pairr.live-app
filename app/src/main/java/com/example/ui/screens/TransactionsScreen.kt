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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MonetizationOn
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
import com.example.WalletTransaction
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
import com.example.ui.theme.AppBorderWeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val bg = MaterialTheme.colorScheme.background
    val cardBg = MaterialTheme.colorScheme.surface
    val borderColor = MaterialTheme.colorScheme.outline
    val pinkGradient = Brush.horizontalGradient(listOf(PinkPrimary, OrangeSecondary))
    val textColor = MaterialTheme.colorScheme.onSurface

    val transactionsList by viewModel.transactions.collectAsState()
    var selectedFilter by remember { mutableStateOf("All") } // "All", "Recharges", "Spends"
    
    var showTxDetailsDialog by remember { mutableStateOf<WalletTransaction?>(null) }
    val context = LocalContext.current

    // Filter transactions based on selection
    val filteredTransactions = remember(transactionsList, selectedFilter) {
        when (selectedFilter) {
            "Recharges" -> transactionsList.filter { it.isPositive }
            "Spends" -> transactionsList.filter { !it.isPositive }
            else -> transactionsList
        }
    }

    // Calculates sums for UI stats
    val totalRecharged = remember(transactionsList) {
        transactionsList.filter { it.isPositive }.sumOf {
            it.amount.replace("[^0-9]".toRegex(), "").toIntOrNull() ?: 0
        }
    }

    val totalSpends = remember(transactionsList) {
        transactionsList.filter { !it.isPositive }.sumOf {
            it.amount.replace("[^0-9]".toRegex(), "").toIntOrNull() ?: 0
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Transaction History",
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
            
            // Statistics Cards Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Recharges summary
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .appSurfaceCard(shape = RoundedCornerShape(18.dp))
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
                                    imageVector = Icons.Default.ArrowUpward,
                                    contentDescription = "Received",
                                    tint = appSuccessColor(),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Total Recharged", color = appMutedText(), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = String.format("+%,d Tokens", totalRecharged),
                            color = appSuccessColor(),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Spends summary
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .appSurfaceCard(shape = RoundedCornerShape(18.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(appErrorContainer()),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowDownward,
                                    contentDescription = "Spent",
                                    tint = appErrorColor(),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Total Spent", color = appMutedText(), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = String.format("%,d Tokens", totalSpends),
                            color = appErrorColor(),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Filter Chips Bar
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
                
                val filters = listOf("All", "Recharges", "Spends")
                filters.forEach { filter ->
                    AppFilterChip(
                        label = filter,
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Transactions History list
            if (filteredTransactions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No matching transactions found.", color = appMutedText())
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredTransactions) { tx ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .appSurfaceCard(shape = RoundedCornerShape(18.dp))
                                .clickable { showTxDetailsDialog = tx }
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(if (tx.isPositive) appSuccessContainer() else appErrorContainer()),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MonetizationOn,
                                        contentDescription = "Token Tx",
                                        tint = if (tx.isPositive) appSuccessColor() else appErrorColor(),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        tx.title,
                                        color = textColor,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(tx.date, color = appMutedText(), fontSize = 12.sp)
                                }
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = tx.amount,
                                    color = if (tx.isPositive) appSuccessColor() else appErrorColor(),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Success", color = appSuccessColor(), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
        }
    }

    // Comprehensive Transaction Details Dialog
    showTxDetailsDialog?.let { tx ->
        AlertDialog(
            onDismissRequest = { showTxDetailsDialog = null },
            title = {
                Text("Receipt Details", color = textColor, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Description:", color = textColor.copy(alpha = 0.5f), fontSize = 14.sp)
                        Text(tx.title, color = textColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Status:", color = textColor.copy(alpha = 0.5f), fontSize = 14.sp)
                        Text("COMPLETED", color = appSuccessColor(), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Timestamp:", color = textColor.copy(alpha = 0.5f), fontSize = 14.sp)
                        Text(tx.date, color = textColor, fontSize = 14.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Amount Modified:", color = textColor.copy(alpha = 0.5f), fontSize = 14.sp)
                        Text(
                            text = tx.amount,
                            color = if (tx.isPositive) appSuccessColor() else appErrorColor(),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Transaction ID:", color = textColor.copy(alpha = 0.5f), fontSize = 14.sp)
                        Text(tx.id, color = textColor.copy(alpha = 0.5f), fontSize = 12.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Billing Cycle:", color = textColor.copy(alpha = 0.5f), fontSize = 14.sp)
                        Text("Instant Top Up", color = textColor, fontSize = 14.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Method:", color = textColor.copy(alpha = 0.5f), fontSize = 14.sp)
                        Text("Visa ending *8492", color = textColor, fontSize = 14.sp)
                    }
                    HorizontalDivider(color = borderColor)
                    Text(
                        text = "Note: If you have any questions or require support regarding this transaction, contact billing support using Live Chat referencing Ref ID above.",
                        color = textColor.copy(alpha = 0.5f),
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showTxDetailsDialog = null }) {
                    Text("Dismiss", color = PinkPrimary)
                }
            },
            containerColor = cardBg
        )
    }
}
