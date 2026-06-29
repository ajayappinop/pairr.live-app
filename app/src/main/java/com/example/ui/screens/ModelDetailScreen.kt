package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Topic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.MainViewModel
import com.example.ModelReview
import com.example.data.canTalkNow
import com.example.data.displayProfilePhotoUrl
import com.example.data.mockModels
import com.example.data.publicUsername
import com.example.ui.components.BlockUserDialog
import com.example.ui.components.ReportDialog
import com.example.ui.components.ReportType
import com.example.ui.components.ModelRatePerMinuteRow
import com.example.ui.theme.PinkPrimary
import com.example.ui.theme.SoftScreenBackground
import com.example.ui.theme.appStarColor
import com.example.ui.theme.appSuccessColor
import com.example.ui.theme.isCompactWidth

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ModelDetailScreen(
    modelId: String?,
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onCall: (String, Boolean) -> Unit,
    onChat: (String) -> Unit,
    onViewPackages: (String) -> Unit
) {
    val models by viewModel.models.collectAsState()
    var modelState by remember { mutableStateOf<com.example.data.AppModel?>(null) }
    
    LaunchedEffect(modelId) {
        if (modelId != null) {
            viewModel.getModelProfile(modelId) { loadedModel ->
                modelState = loadedModel
            }
        }
    }
    
    val model = modelState ?: models.find { it.id == modelId }
    val favorites by viewModel.favorites.collectAsState()
    val blockedUsers by viewModel.blockedUsers.collectAsState()
    var showAllReviewsDialog by remember { mutableStateOf(false) }
    var showBlockDialog by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }
    var showTopMenu by remember { mutableStateOf(false) }
    val context = LocalContext.current
    
    if (model == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Model Not Found")
        }
        return
    }

    val isBlocked = blockedUsers.any { it.id == model.id }
    val modelUsername = model.publicUsername()
    val canCall = model.canTalkNow()

    if (showBlockDialog) {
        BlockUserDialog(
            userName = modelUsername,
            onConfirm = {
                viewModel.blockUser(
                    userId = model.id,
                    name = modelUsername,
                    avatarUrl = model.displayProfilePhotoUrl()
                )
                Toast.makeText(context, "${modelUsername} blocked", Toast.LENGTH_SHORT).show()
                onBack()
            },
            onDismiss = { showBlockDialog = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(modelUsername, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onSurface)
                    }
                },
                actions = {
                    val isFavorite = favorites.contains(model.id)
                    Box {
                        IconButton(onClick = { showTopMenu = true }) {
                            Icon(
                                Icons.Default.MoreVert,
                                contentDescription = "More options",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        DropdownMenu(
                            expanded = showTopMenu,
                            onDismissRequest = { showTopMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Chat") },
                                onClick = {
                                    showTopMenu = false
                                    onChat(model.id)
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.ChatBubbleOutline, contentDescription = null)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(if (isFavorite) "Remove from Favorites" else "Add to Favorites") },
                                onClick = {
                                    showTopMenu = false
                                    viewModel.toggleFavorite(model.id)
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                        contentDescription = null,
                                        tint = if (isFavorite) PinkPrimary else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Report") },
                                onClick = {
                                    showTopMenu = false
                                    showReportDialog = true
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.ReportProblem, contentDescription = null)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(if (isBlocked) "Unblock" else "Block") },
                                onClick = {
                                    showTopMenu = false
                                    if (isBlocked) {
                                        viewModel.unblockUser(model.id)
                                        Toast.makeText(context, "${modelUsername} unblocked", Toast.LENGTH_SHORT).show()
                                    } else {
                                        showBlockDialog = true
                                    }
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Block,
                                        contentDescription = null,
                                        tint = if (isBlocked) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            val compact = isCompactWidth()
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = if (compact) 8.dp else 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { onCall(model.id, false) },
                        enabled = canCall,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = if (compact) 4.dp else 8.dp)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        contentPadding = PaddingValues(horizontal = if (compact) 6.dp else 12.dp)
                    ) {
                        Icon(Icons.Default.Call, contentDescription = "Audio Call", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(if (compact) 18.dp else 24.dp))
                        if (!compact) Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (compact) "Audio · ${model.audioPrice}/m" else "Audio (${model.audioPrice} Rupees/m)",
                            fontSize = if (compact) 10.sp else 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Button(
                        onClick = { onCall(model.id, true) },
                        enabled = canCall,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = if (compact) 4.dp else 8.dp)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary),
                        contentPadding = PaddingValues(horizontal = if (compact) 6.dp else 12.dp)
                    ) {
                        Icon(Icons.Default.Videocam, contentDescription = "Video Call", tint = Color.White, modifier = Modifier.size(if (compact) 18.dp else 24.dp))
                        if (!compact) Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (compact) "Video · ${model.videoPrice}/m" else "Video (${model.videoPrice} Rupees/m)",
                            fontSize = if (compact) 10.sp else 12.sp,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            AsyncImage(
                model = model.displayProfilePhotoUrl(),
                contentDescription = modelUsername,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modelUsername,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    AvailabilityBadge(status = model.status)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Translate, contentDescription = "Languages", tint = PinkPrimary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        model.languages.joinToString(", "),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.weight(1f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (model.categories.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Topic,
                            contentDescription = "Topics",
                            tint = PinkPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Topics",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        model.categories.forEach { topic ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(PinkPrimary.copy(alpha = 0.12f))
                                    .border(
                                        1.dp,
                                        PinkPrimary.copy(alpha = 0.35f),
                                        RoundedCornerShape(20.dp)
                                    )
                                    .padding(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = topic,
                                    color = PinkPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                ModelRatePerMinuteRow(
                    audioPrice = model.audioPrice,
                    videoPrice = model.videoPrice
                )

                Spacer(modifier = Modifier.height(20.dp))
                
                Text("About Me", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(model.bio, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text("Ratings & Reviews", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showAllReviewsDialog = true }
                ) {
                    Icon(Icons.Default.Star, contentDescription = "Rating", tint = appStarColor(), modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = String.format("%.1f", model.rating), 
                        style = MaterialTheme.typography.titleLarge, 
                        color = MaterialTheme.colorScheme.onSurface, 
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "\u00B7  ${model.reviewsCount} reviews", 
                        style = MaterialTheme.typography.bodyLarge, 
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                
                val reviews by viewModel.reviews.collectAsState()
                
                if (showAllReviewsDialog) {
                    AllReviewsDialog(
                        modelName = modelUsername,
                        reviews = reviews.filter { it.modelId == model.id },
                        onDismiss = { showAllReviewsDialog = false }
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                val modelReviews = reviews.filter { it.modelId == model.id }
                
                if (modelReviews.isEmpty()) {
                    Text("No written reviews yet.", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), fontSize = 14.sp)
                } else {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        modelReviews.take(4).forEach { rev ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                                    .padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(rev.reviewerName, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(rev.date, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), fontSize = 11.sp)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Row {
                                        (1..5).forEach { star ->
                                            Icon(
                                                imageVector = Icons.Default.Star,
                                                contentDescription = null,
                                                tint = if (star <= rev.rating) appStarColor() else MaterialTheme.colorScheme.outline,
                                                modifier = Modifier.size(12.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(if (rev.isVideo) "Video call \uD83C\uDFA5" else "Audio call \uD83D\uDCDE", color = PinkPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(rev.reviewText, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f), fontSize = 13.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (showReportDialog) {
        ReportDialog(
            reportedName = modelUsername,
            reportType = ReportType.Profile,
            onDismiss = { showReportDialog = false }
        )
    }
}

@Composable
fun AllReviewsDialog(
    modelName: String,
    reviews: List<ModelReview>,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Reviews for $modelName") },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(reviews) { rev ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(rev.reviewerName, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(rev.date, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), fontSize = 11.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Row {
                                (1..5).forEach { star ->
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = if (star <= rev.rating) appStarColor() else MaterialTheme.colorScheme.outline,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (rev.isVideo) "Video call \uD83C\uDFA5" else "Audio call \uD83D\uDCDE", color = PinkPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(rev.reviewText, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f), fontSize = 13.sp)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
