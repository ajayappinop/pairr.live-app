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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.CallMade
import androidx.compose.material.icons.filled.CallMissed
import androidx.compose.material.icons.filled.CallReceived
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Clear
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.CallBooking
import com.example.MainViewModel
import com.example.ui.components.PostCallRatingModal
import com.example.ui.theme.AppFilterChip
import com.example.ui.theme.AppSegmentedTabs
import com.example.ui.theme.OrangeSecondary
import com.example.ui.theme.PinkPrimary
import com.example.ui.theme.SoftScreenBackground
import com.example.ui.theme.appErrorColor
import com.example.ui.theme.appMutedText
import com.example.ui.theme.appSuccessColor
import com.example.ui.theme.appSurfaceCard
import com.example.ui.theme.AppBorderWeight

@Composable
fun CallDashboardScreen(viewModel: MainViewModel) {
    var selectedTab by remember { mutableStateOf(0) }
    var ratingModalVisible by remember { mutableStateOf(false) }
    var modalTargetName by remember { mutableStateOf("") }
    var modalTargetAvatar by remember { mutableStateOf("") }
    
    val bg = MaterialTheme.colorScheme.background
    val cardBg = MaterialTheme.colorScheme.surface
    val borderColor = MaterialTheme.colorScheme.outline
    val textColor = MaterialTheme.colorScheme.onSurface

    SoftScreenBackground {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Calls",
                color = textColor,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Tabs
        AppSegmentedTabs(
            tabs = listOf("Scheduled", "History"),
            selectedIndex = selectedTab,
            onTabSelected = { selectedTab = it },
            modifier = Modifier.padding(horizontal = 16.dp),
            fontSize = 13.sp,
            verticalPadding = 10.dp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Content
        when (selectedTab) {
            0 -> {
                ScheduledCallsSection(viewModel = viewModel)
            }
            else -> {
                CallHistorySection(
                    onRateCall = { name, avatarUrl ->
                        modalTargetName = name
                        modalTargetAvatar = avatarUrl
                        ratingModalVisible = true
                    }
                )
            }
        }
    }
    
    if (ratingModalVisible) {
        PostCallRatingModal(
            modelName = modalTargetName,
            modelAvatarUrl = modalTargetAvatar,
            onDismiss = { ratingModalVisible = false },
            onSubmit = { rating, feedback -> 
                // In a real app, handle submitting the rating to DB here
                ratingModalVisible = false 
            }
        )
    }
    }
}

@Composable
fun ScheduledCallsSection(viewModel: MainViewModel) {
    val bookings by viewModel.bookings.collectAsState()
    val context = LocalContext.current
    val cardBg = MaterialTheme.colorScheme.surface
    val borderColor = MaterialTheme.colorScheme.outline
    val textColor = MaterialTheme.colorScheme.onSurface

    if (bookings.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "No Scheduled Calls",
                    color = textColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Book a future audio or video call with your favorite models from their details page.",
                    color = textColor.copy(alpha = 0.5f),
                    fontSize = 14.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(bookings) { booking ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .appSurfaceCard(shape = RoundedCornerShape(18.dp))
                        .padding(16.dp)
                ) {
                    // Header: Avatar, Name, Status Badge
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = booking.modelAvatarUrl,
                            contentDescription = "Avatar",
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = booking.modelName,
                                color = textColor,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (booking.isVideo) Icons.Default.Videocam else Icons.Default.Call,
                                    contentDescription = "Call Type",
                                    tint = if (booking.isVideo) appErrorColor() else appSuccessColor(),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (booking.isVideo) "Video Co-host Call" else "Audio Voice Call",
                                    color = appMutedText(),
                                    fontSize = 12.sp
                                )
                            }
                        }

                        // Status Badge
                        val statusColor = when (booking.status) {
                            "Accepted" -> appSuccessColor()
                            "Scheduled" -> OrangeSecondary
                            "Cancelled" -> appErrorColor()
                            else -> appMutedText()
                        }
                        val statusLabel = when (booking.status) {
                            "Scheduled" -> "Awaiting model"
                            "Accepted" -> "Confirmed"
                            else -> booking.status
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(statusColor.copy(alpha = 0.15f))
                                .border(1.dp, statusColor.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = statusLabel,
                                color = statusColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = borderColor)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Date & Time slots details
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Date",
                                tint = PinkPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = booking.date,
                                color = appMutedText(),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1.2f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = "Time",
                                tint = OrangeSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = booking.timeSlot,
                                color = appMutedText(),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Footer actions
                    if (booking.status == "Scheduled" || booking.status == "Accepted") {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Cancel booking text button
                            OutlinedButton(
                                onClick = {
                                    viewModel.cancelBooking(booking.id)
                                    Toast.makeText(context, "Call booking cancelled successfully.", Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = appErrorColor()),
                                border = androidx.compose.foundation.BorderStroke(1.dp, appErrorColor().copy(alpha = 0.3f)),
                                modifier = Modifier.padding(end = 10.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Cancel", modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Cancel", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            // Join / Start call button
                            Button(
                                onClick = {
                                    Toast.makeText(context, "Starting call with ${booking.modelName}... Initiating high fidelity streaming channel.", Toast.LENGTH_LONG).show()
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary)
                            ) {
                                Text("Start Call", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    } else {
                        // Cancelled state info text
                        Text(
                            text = "This booking was cancelled and your reservation has been released.",
                            color = appMutedText(),
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CallRequestsSection(onAccept: (String, String) -> Unit) {
    val requests = listOf(
        CallRequest("Gemini Advanced", "Video Call Request", "2 min ago", "https://api.dicebear.com/7.x/bottts/png?seed=6"),
        CallRequest("DALL-E 3", "Audio Call Request", "5 min ago", "https://api.dicebear.com/7.x/bottts/png?seed=7")
    )

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(requests) { request ->
            CallRequestCard(request, onAccept = { onAccept(request.name, request.avatarUrl) })
        }
    }
}

@Composable
fun CallRequestCard(request: CallRequest, onAccept: () -> Unit) {
    val textColor = MaterialTheme.colorScheme.onSurface
    val cardShape = RoundedCornerShape(16.dp)
    val muted = appMutedText()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .appSurfaceCard(shape = cardShape, borderWeight = AppBorderWeight.Default)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = request.avatarUrl,
            contentDescription = "Avatar",
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = request.name,
                color = textColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (request.type.contains("Video")) Icons.Default.Videocam else Icons.Default.Call,
                    contentDescription = "Type",
                    tint = OrangeSecondary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = request.type,
                    color = muted,
                    fontSize = 12.sp
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = request.time,
                color = muted.copy(alpha = 0.8f),
                fontSize = 10.sp
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                    .clickable { /* Reject */ },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Close, contentDescription = "Decline", tint = textColor)
            }
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(appSuccessColor(), CircleShape)
                    .clickable { onAccept() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Check, contentDescription = "Accept", tint = Color.White)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CallHistorySection(onRateCall: (String, String) -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedMediaType by remember { mutableIntStateOf(0) } // 0 = All, 1 = Audio, 2 = Video
    var selectedDateRange by remember { mutableStateOf("All") } // "All", "Today", "Yesterday", "Last 7 Days", "Older"

    val textColor = MaterialTheme.colorScheme.onSurface
    val cardBg = MaterialTheme.colorScheme.surface
    val borderColor = MaterialTheme.colorScheme.outline
    val muted = appMutedText()

    val datesList = listOf("All", "Today", "Yesterday", "Last 7 Days", "Older")

    // Dynamic, richer fake data for testing search/filtering by model name or date range
    val baseHistory = listOf(
        CallHistoryItem("Aisha Khan", "Outgoing", "10:30 AM", "Today", "Today", "https://api.dicebear.com/7.x/bottts/png?seed=1", false),
        CallHistoryItem("Riya Patel", "Incoming", "08:15 PM", "Yesterday", "Yesterday", "https://api.dicebear.com/7.x/bottts/png?seed=3", false),
        CallHistoryItem("Clara Dupont", "Missed", "04:00 PM", "Yesterday", "Yesterday", "https://api.dicebear.com/7.x/bottts/png?seed=a", false),
        CallHistoryItem("Gemini Advanced", "Outgoing", "11:45 AM", "Today", "Today", "https://api.dicebear.com/7.x/bottts/png?seed=6", true),
        CallHistoryItem("Claude 3.5 Sonnet", "Missed", "09:20 PM", "Yesterday", "Yesterday", "https://api.dicebear.com/7.x/bottts/png?seed=5", true),
        CallHistoryItem("Riya Patel", "Outgoing", "02:15 PM", "Jun 10, 2026", "Last 7 Days", "https://api.dicebear.com/7.x/bottts/png?seed=3", true),
        CallHistoryItem("Agha Begum", "Incoming", "03:45 PM", "Jun 09, 2026", "Last 7 Days", "https://api.dicebear.com/7.x/bottts/png?seed=b", false),
        CallHistoryItem("Sarah Parker", "Outgoing", "11:30 AM", "Jun 03, 2026", "Older", "https://api.dicebear.com/7.x/bottts/png?seed=c", true),
        CallHistoryItem("Yuki Tanaka", "Incoming", "06:10 PM", "May 28, 2026", "Older", "https://api.dicebear.com/7.x/bottts/png?seed=d", false)
    )

    // Apply filtering logically
    val filteredHistory = remember(searchQuery, selectedMediaType, selectedDateRange) {
        baseHistory.filter { item ->
            // Search filter
            val matchesSearch = searchQuery.isEmpty() || item.name.contains(searchQuery, ignoreCase = true)
            
            // Media Type Category tab filter
            val matchesMediaType = when (selectedMediaType) {
                1 -> !item.isVideo // Audio Calls
                2 -> item.isVideo  // Video Calls
                else -> true      // All Calls
            }
            
            // Date category range filter
            val matchesDateRange = selectedDateRange == "All" || item.dateCategory == selectedDateRange
            
            matchesSearch && matchesMediaType && matchesDateRange
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Search text field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search list by model name...", color = muted, fontSize = 13.sp) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search icon",
                    tint = muted,
                    modifier = Modifier.size(18.dp)
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear search",
                            tint = muted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = textColor,
                unfocusedTextColor = textColor,
                focusedContainerColor = cardBg,
                unfocusedContainerColor = cardBg,
                focusedBorderColor = PinkPrimary,
                unfocusedBorderColor = borderColor,
                cursorColor = PinkPrimary,
                focusedPlaceholderColor = muted,
                unfocusedPlaceholderColor = muted
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(12.dp))
        )

        // Filter Rows:
        // 1. MediaType filter cards (All, Audio, Video)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val mediaTabs = listOf("All Calls", "Audio Calls", "Video Calls")
            mediaTabs.forEachIndexed { index, title ->
                AppFilterChip(
                    label = title,
                    selected = selectedMediaType == index,
                    onClick = { selectedMediaType = index },
                    modifier = Modifier.weight(1f),
                    fontSize = 11.sp
                )
            }
        }

        // 2. Date category range filter chips
        Text(
            text = "Filter by date period:",
            color = muted,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 4.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            datesList.forEach { range ->
                val isSelected = selectedDateRange == range
                val countText = when (range) {
                    "All" -> ""
                    else -> {
                        val count = baseHistory.count { 
                            it.dateCategory == range && (selectedMediaType == 0 || (selectedMediaType == 1 && !it.isVideo) || (selectedMediaType == 2 && it.isVideo))
                        }
                        " ($count)"
                    }
                }
                AppFilterChip(
                    label = "$range$countText",
                    selected = isSelected,
                    onClick = { selectedDateRange = range },
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // History items vertical list
        if (filteredHistory.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "No matching interactions",
                        color = muted,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Try clearing search or changing date filter",
                        color = muted.copy(alpha = 0.8f),
                        fontSize = 11.sp
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredHistory) { item ->
                    CallHistoryCard(item, onRateCall = { onRateCall(item.name, item.avatarUrl) })
                }
            }
        }
    }
}

@Composable
fun CallHistoryCard(item: CallHistoryItem, onRateCall: () -> Unit) {
    val textColor = MaterialTheme.colorScheme.onSurface
    val muted = appMutedText()
    val cardShape = RoundedCornerShape(16.dp)

    val callIcon = when (item.type) {
        "Incoming" -> Icons.Default.CallReceived
        "Outgoing" -> Icons.Default.CallMade
        else -> Icons.Default.CallMissed
    }
    val callColor = if (item.type == "Missed") appErrorColor() else appSuccessColor()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .appSurfaceCard(shape = cardShape, borderWeight = AppBorderWeight.Default)
            .clickable { onRateCall() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = item.avatarUrl,
            contentDescription = "Avatar",
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.name,
                color = textColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = callIcon,
                    contentDescription = item.type,
                    tint = callColor,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${item.date}, ${item.time}",
                    color = muted,
                    fontSize = 12.sp
                )
            }
        }

        Box(
            modifier = Modifier
                .size(40.dp)
                .background(PinkPrimary.copy(alpha = 0.15f), CircleShape)
                .clickable { /* Call back */ },
            contentAlignment = Alignment.Center
        ) {
            Icon(if (item.isVideo) Icons.Default.Videocam else Icons.Default.Call, contentDescription = "Call", tint = PinkPrimary)
        }
    }
}

data class CallRequest(val name: String, val type: String, val time: String, val avatarUrl: String)
data class CallHistoryItem(
    val name: String,
    val type: String,
    val time: String,
    val date: String,
    val dateCategory: String,
    val avatarUrl: String,
    val isVideo: Boolean
)
