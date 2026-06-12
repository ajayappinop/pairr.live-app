package com.example.ui.screens.model

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallReceived
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.appMutedText
import com.example.ui.theme.appSurfaceCard
import com.example.ui.theme.appTitleText
import com.example.ui.theme.AppBorderWeight

@Composable
fun ModelSideHistoryScreen() {
    val bg = MaterialTheme.colorScheme.background
    val textColor = MaterialTheme.colorScheme.onSurface
    val cardShape = RoundedCornerShape(20.dp)

    val mockHistory = listOf(
        Triple("Ramesh K.", "20 mins ago", true),
        Triple("Amit P.", "1 hour ago", false),
        Triple("Sagar K.", "Yesterday", true),
        Triple("Vikram D.", "Yesterday", false),
        Triple("Karan W.", "2 days ago", true)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Call History", color = appTitleText(), fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(mockHistory) { info ->
                val isVideo = info.third
                val amount = if (isVideo) "+150 Tokens" else "+60 Tokens"
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .appSurfaceCard(shape = cardShape, borderWeight = AppBorderWeight.Default)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(
                                if (isVideo) Color(0xFFFFEAEA) else Color(0xFFE8F8EF)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            if (isVideo) Icons.Default.Videocam else Icons.Default.Call,
                            contentDescription = null,
                            tint = if (isVideo) Color(0xFFFF4D4D) else Color(0xFF3DDC84)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(info.first, color = textColor, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.CallReceived,
                                contentDescription = "Incoming",
                                tint = appMutedText(),
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(info.second, color = appMutedText(), fontSize = 12.sp)
                        }
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(amount, color = Color(0xFFFFB800), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(if (isVideo) "Video Co." else "Audio Co.", color = appMutedText(), fontSize = 11.sp)
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(100.dp)) }
        }
    }
}
