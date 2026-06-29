package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.MainViewModel
import com.example.ui.components.ReportDialog
import com.example.ui.components.ReportType
import com.example.ui.theme.OrangeSecondary
import com.example.ui.theme.PinkPrimary
import com.example.ui.theme.appAccentColor
import com.example.ui.theme.appCaptionText
import com.example.ui.theme.appMutedText
import com.example.ui.theme.appOutlinedFieldColors
import com.example.ui.theme.appSecondaryText
import com.example.ui.theme.appStarColor
import com.example.ui.theme.appSubtleFill
import kotlinx.coroutines.delay

@Composable
fun CallScreen(
    modelId: String?,
    isVideo: Boolean,
    viewModel: MainViewModel,
    onEndCall: () -> Unit
) {
    val models by viewModel.models.collectAsStateWithLifecycle()
    val model = models.find { it.id == modelId } ?: return
    val wallet by viewModel.walletState.collectAsStateWithLifecycle()
    
    var timeElapsed by remember { mutableStateOf(0) }
    var tokensSpent by remember { mutableStateOf(0) }
    var isMuted by remember { mutableStateOf(false) }
    var isCameraOff by remember { mutableStateOf(false) }
    var showRatingPrompt by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }
    
    val pinkGradient = Brush.horizontalGradient(listOf(PinkPrimary, OrangeSecondary))
    
    val pricePerMinute = if (isVideo) model.videoPrice else model.audioPrice
    
    LaunchedEffect(showRatingPrompt) {
        if (showRatingPrompt) return@LaunchedEffect
        while(true) {
            delay(1000)
            timeElapsed++
            if (timeElapsed % 60 == 0) {
                val success = viewModel.deductTokens(pricePerMinute, isVideo)
                if (success) {
                    tokensSpent += pricePerMinute
                } else {
                    showRatingPrompt = true
                }
            }
        }
    }
    
    val formattedTime = String.format("%02d:%02d", timeElapsed / 60, timeElapsed % 60)

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        if (!showRatingPrompt) {
            IconButton(
                onClick = { showReportDialog = true },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 48.dp, end = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ReportProblem,
                    contentDescription = "Report Call",
                    tint = Color.White.copy(alpha = 0.85f)
                )
            }
        }

        if (isVideo) {
            AsyncImage(
                model = "https://i.pravatar.cc/800?u=${model.id}",
                contentDescription = "Video Stream",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 48.dp, top = 64.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                if (!isVideo) {
                    Box(contentAlignment = Alignment.Center) {
                        Box(
                            modifier = Modifier
                                .size(160.dp)
                                .clip(CircleShape)
                                .background(PinkPrimary.copy(alpha = 0.2f))
                        )
                        AsyncImage(
                            model = "https://i.pravatar.cc/300?u=${model.id}",
                            contentDescription = "Profile",
                            modifier = Modifier
                                .size(140.dp)
                                .clip(CircleShape)
                                .border(3.dp, PinkPrimary, CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                } else if (!isCameraOff) {
                     // Picture in picture for self
                    Box(
                        modifier = Modifier
                            .align(Alignment.End)
                            .size(width = 100.dp, height = 150.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.DarkGray.copy(alpha = 0.4f))
                            .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                    ) {
                        Icon(Icons.Default.Cameraswitch, contentDescription = null, tint = Color.White.copy(alpha = 0.5f), modifier = Modifier.align(Alignment.Center))
                    }
                }
                
                Text(
                    text = model.name, 
                    color = Color.White, 
                    style = MaterialTheme.typography.headlineLarge, 
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                         modifier = Modifier
                             .size(8.dp)
                             .clip(CircleShape)
                             .background(Color(0xFFFF5252))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = formattedTime, 
                        color = Color.White, 
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Surface(
                    color = Color.Black.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.CurrencyRupee, 
                            contentDescription = null, 
                            tint = Color(0xFFFFD700), 
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        val typeBalance = if (isVideo) wallet.videoBalance else wallet.audioBalance
                        Text(
                            text = "$typeBalance Rupees", 
                            color = Color.White, 
                            fontSize = 14.sp, 
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                if ((if (isVideo) wallet.videoBalance else wallet.audioBalance) < pricePerMinute * 2) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Low Balance Warning!", color = Color(0xFFFF5252), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
            
            // Call Controls Action Row
            Row(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(40.dp))
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mute toggle
                IconButton(
                    onClick = { isMuted = !isMuted },
                    modifier = Modifier
                        .size(54.dp)
                        .background(if (isMuted) Color.White.copy(alpha = 0.2f) else Color.Transparent, CircleShape)
                        .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape)
                ) {
                    Icon(
                        imageVector = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic, 
                        contentDescription = "Mute", 
                        tint = if (isMuted) PinkPrimary else Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                // End Call
                IconButton(
                    onClick = { showRatingPrompt = true },
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(pinkGradient)
                        .border(2.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.CallEnd, 
                        contentDescription = "End Call", 
                        tint = Color.White, 
                        modifier = Modifier.size(32.dp)
                    )
                }
                
                // Video/Cam switch or generic toggle
                IconButton(
                    onClick = { if (isVideo) isCameraOff = !isCameraOff },
                    modifier = Modifier
                        .size(54.dp)
                        .background(if (isCameraOff) Color.White.copy(alpha = 0.2f) else Color.Transparent, CircleShape)
                        .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape)
                ) {
                    Icon(
                        imageVector = if (isVideo && isCameraOff) Icons.Default.Cameraswitch else if (isVideo) Icons.Default.Cameraswitch else Icons.Default.Cameraswitch, 
                        contentDescription = "Switch", 
                        tint = if (isCameraOff) PinkPrimary else Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        if (showRatingPrompt) {
            val dialogBg = MaterialTheme.colorScheme.surface
            val dialogText = MaterialTheme.colorScheme.onSurface
            val fieldColors = appOutlinedFieldColors()
            AlertDialog(
                onDismissRequest = onEndCall,
                title = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        AsyncImage(
                            model = "https://i.pravatar.cc/150?u=${model.id}",
                            contentDescription = "Model Avatar",
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Rate your Session",
                            style = MaterialTheme.typography.titleLarge,
                            color = dialogText,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "with ${model.name}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = appSecondaryText()
                        )
                    }
                },
                text = {
                    var ratingInput by remember { mutableStateOf(5) }
                    var feedbackText by remember { mutableStateOf("") }
                    val verticalScroll = rememberScrollState()
                    val horizontalScroll = rememberScrollState()
                    val tokenTypeLabel = if (isVideo) "Video" else "Audio"
                    
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 420.dp)
                            .verticalScroll(verticalScroll),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(12.dp))

                        CallSessionSummaryCard(
                            durationLabel = formattedTime,
                            tokensSpent = tokensSpent,
                            tokenTypeLabel = tokenTypeLabel,
                            textColor = dialogText
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Star rating selectors
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            (1..5).forEach { star ->
                                IconButton(
                                    onClick = { ratingInput = star },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = if (star <= ratingInput) Icons.Default.Star else Icons.Default.StarBorder,
                                        contentDescription = "$star Stars",
                                        tint = if (star <= ratingInput) appStarColor() else appMutedText(),
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Quick feedback tag suggestions
                        val quickTags = listOf("Excellent ✅", "Friendly ❤️", "Helpful 💡", "Clear Voice 🔊", "Lovely Chat 💬")
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(horizontalScroll),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            quickTags.forEach { tag ->
                                val isSelected = feedbackText.contains(tag)
                                Button(
                                    onClick = {
                                        feedbackText = if (isSelected) {
                                            feedbackText.replace("$tag", "").trim()
                                        } else {
                                            if (feedbackText.isEmpty()) tag else "$feedbackText, $tag"
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isSelected) appAccentColor() else appSubtleFill(),
                                        contentColor = if (isSelected) Color.White else dialogText
                                    ),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(tag, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        OutlinedTextField(
                            value = feedbackText,
                            onValueChange = { feedbackText = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(90.dp),
                            placeholder = { Text("Write an optional review...", color = appCaptionText(), fontSize = 13.sp) },
                            colors = fieldColors,
                            shape = RoundedCornerShape(12.dp)
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = onEndCall,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = appSecondaryText()
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Skip", fontWeight = FontWeight.Bold)
                            }
                            
                            Button(
                                onClick = {
                                    val finalReview = if (feedbackText.isBlank()) "Excellent call!" else feedbackText
                                    viewModel.addModelReview(model.id, ratingInput, finalReview, isVideo)
                                    onEndCall()
                                },
                                modifier = Modifier.weight(1.5f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = appAccentColor()
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Submit", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                },
                confirmButton = {},
                containerColor = dialogBg
            )
        }

        if (showReportDialog) {
            ReportDialog(
                reportedName = model.name,
                reportType = ReportType.Call,
                callIsVideo = isVideo,
                onDismiss = { showReportDialog = false }
            )
        }
    }
}

@Composable
private fun CallSessionSummaryCard(
    durationLabel: String,
    tokensSpent: Int,
    tokenTypeLabel: String,
    textColor: Color
) {
    Surface(
        color = appSubtleFill(),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Call Duration",
                    color = appCaptionText(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = durationLabel,
                    color = textColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            VerticalDivider(
                modifier = Modifier.height(40.dp),
                color = appMutedText().copy(alpha = 0.3f)
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Rupees Spent",
                    color = appCaptionText(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = tokensSpent.toString(),
                    color = PinkPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$tokenTypeLabel Rupees",
                    color = appSecondaryText(),
                    fontSize = 11.sp
                )
            }
        }
    }
}
