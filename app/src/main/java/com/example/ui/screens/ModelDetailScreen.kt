package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
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
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Star
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
import com.example.CallBooking
import com.example.MainViewModel
import com.example.ModelReview
import com.example.data.displayImageUrls
import com.example.data.displayProfilePhotoUrl
import com.example.data.displayIntroVideoUrl
import com.example.data.mockModels
import com.example.data.publicUsername
import com.example.ui.components.BlockUserDialog
import com.example.ui.components.FullScreenImageDialog
import com.example.ui.components.ReportDialog
import com.example.ui.components.ReportType
import com.example.ui.components.ModelImageGridGallery
import com.example.ui.components.ModelIntroVideoSection
import com.example.ui.components.ModelRatePerMinuteRow
import com.example.ui.theme.PinkPrimary
import com.example.ui.theme.OrangeSecondary
import com.example.ui.theme.SoftScreenBackground
import com.example.ui.theme.appErrorColor
import com.example.ui.theme.appErrorContainer
import com.example.ui.theme.appSecondaryText
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
    var showBookingDialog by remember { mutableStateOf(false) }
    var showAllReviewsDialog by remember { mutableStateOf(false) }
    var showBlockDialog by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }
    var selectedGalleryImage by remember { mutableStateOf<Pair<String, Int>?>(null) }
    val context = LocalContext.current

    val galleryImages = model?.displayImageUrls() ?: emptyList()
    val introVideo = model?.displayIntroVideoUrl()
    
    if (model == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Model Not Found")
        }
        return
    }

    val isBlocked = blockedUsers.any { it.id == model.id }
    val modelUsername = model.publicUsername()

    selectedGalleryImage?.let { (url, index) ->
        FullScreenImageDialog(
            imageUrl = url,
            imageIndex = index,
            totalImages = galleryImages.size,
            onDismiss = { selectedGalleryImage = null }
        )
    }

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
                    IconButton(onClick = { onChat(model.id) }) {
                         Icon(Icons.Default.ChatBubbleOutline, contentDescription = "Chat", tint = MaterialTheme.colorScheme.onSurface)
                    }
                    IconButton(onClick = { viewModel.toggleFavorite(model.id) }) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) PinkPrimary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(
                        onClick = { showReportDialog = true }
                    ) {
                        Icon(
                            Icons.Default.ReportProblem,
                            contentDescription = "Report",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(
                        onClick = {
                            if (isBlocked) {
                                viewModel.unblockUser(model.id)
                                Toast.makeText(context, "${modelUsername} unblocked", Toast.LENGTH_SHORT).show()
                            } else {
                                showBlockDialog = true
                            }
                        }
                    ) {
                        Icon(
                            Icons.Default.Block,
                            contentDescription = if (isBlocked) "Unblock" else "Block",
                            tint = if (isBlocked) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                        )
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
                            text = if (compact) "Audio · ${model.audioPrice}/m" else "Audio (${model.audioPrice} Tokens/m)",
                            fontSize = if (compact) 10.sp else 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Button(
                        onClick = { onCall(model.id, true) },
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
                            text = if (compact) "Video · ${model.videoPrice}/m" else "Video (${model.videoPrice} Tokens/m)",
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
            if (introVideo != null) {
                ModelIntroVideoSection(
                    videoUrl = introVideo,
                    height = 300.dp
                )
            } else {
                val profilePhoto = model.displayProfilePhotoUrl()
                AsyncImage(
                    model = profilePhoto,
                    contentDescription = modelUsername,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .clickable {
                            val index = galleryImages.indexOf(profilePhoto).coerceAtLeast(0)
                            selectedGalleryImage = profilePhoto to index
                        },
                    contentScale = ContentScale.Crop
                )
            }

            Column(modifier = Modifier.padding(16.dp)) {
                ModelRatePerMinuteRow(
                    audioPrice = model.audioPrice,
                    videoPrice = model.videoPrice
                )

                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(modelUsername, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
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
                
                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    "Photo Gallery",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "${galleryImages.size} of 5 photos • Tap to view full size",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.height(10.dp))
                ModelImageGridGallery(
                    imageUrls = galleryImages,
                    onImageClick = { url, index -> selectedGalleryImage = url to index }
                )

                Spacer(modifier = Modifier.height(20.dp))
                
                Text("About Me", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(model.bio, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text("Categories", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    model.categories.forEach { cat ->
                        SuggestionChip(
                            onClick = { },
                            label = { Text(cat, color = PinkPrimary) },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                labelColor = PinkPrimary
                            )
                        )
                    }
                }

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
                        text = "•  ${model.reviewsCount} reviews", 
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
                                    Text(if (rev.isVideo) "Video call 🎥" else "Audio call 📞", color = PinkPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(rev.reviewText, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f), fontSize = 13.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                
                // Beautiful promotional booking section
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(Brush.horizontalGradient(listOf(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.surface)))
                        .border(1.dp, PinkPrimary.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Schedule",
                                tint = PinkPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Schedule a Future Call",
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Avoid wait times by reserving a private 30-min premium voice or video session with ${modelUsername}. Book now!",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 20.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { showBookingDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                        ) {
                            Text("Book Appointment Slot 🗓️", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
    }

    if (showBookingDialog) {
        BookingDialog(
            model = model,
            viewModel = viewModel,
            onDismiss = { showBookingDialog = false },
            onViewPackages = onViewPackages
        )
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
                            Text(if (rev.isVideo) "Video call 🎥" else "Audio call 📞", color = PinkPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
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

@Composable
fun BookingDialog(
    model: com.example.data.AppModel,
    viewModel: MainViewModel,
    onDismiss: () -> Unit,
    onViewPackages: (String) -> Unit
) {
    val modelUsername = model.publicUsername()
    val walletState by viewModel.walletState.collectAsState()
    var isVideo by remember { mutableStateOf(false) }
    
    val dates = listOf(
        "Tomorrow, Jun 12",
        "Sat, Jun 13",
        "Sun, Jun 14",
        "Mon, Jun 15",
        "Tue, Jun 16"
    )
    var selectedDate by remember { mutableStateOf(dates[0]) }
    
    val slots = listOf(
        "09:30 AM - 10:00 AM",
        "11:30 AM - 12:00 PM",
        "02:00 PM - 02:30 PM",
        "04:30 PM - 05:00 PM",
        "06:45 PM - 07:15 PM",
        "08:30 PM - 09:00 PM"
    )
    var selectedSlot by remember { mutableStateOf(slots[1]) }
    
    val rate = if (isVideo) model.videoPrice else model.audioPrice
    val totalCost = rate * 30
    
    val balance = if (isVideo) walletState.videoBalance else walletState.audioBalance
    val hasEnoughBalance = balance >= totalCost
    
    val cardBg = MaterialTheme.colorScheme.surface
    val borderColor = MaterialTheme.colorScheme.outline
    val textColor = MaterialTheme.colorScheme.onSurface
    val context = LocalContext.current
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Schedule Session with ${modelUsername}",
                color = textColor,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Call Type Selector
                Text("Select Service Type", color = textColor.copy(alpha = 0.5f), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Audio Choice
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (!isVideo) PinkPrimary.copy(alpha = 0.2f) else cardBg)
                            .border(1.dp, if (!isVideo) PinkPrimary else borderColor, RoundedCornerShape(12.dp))
                            .clickable { isVideo = false }
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Phone, contentDescription = "Audio", tint = if (!isVideo) PinkPrimary else textColor.copy(alpha = 0.5f))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Audio Call", color = textColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("${model.audioPrice} Tokens/min", color = textColor.copy(alpha = 0.5f), fontSize = 10.sp)
                        }
                    }

                    // Video Choice
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isVideo) PinkPrimary.copy(alpha = 0.2f) else cardBg)
                            .border(1.dp, if (isVideo) PinkPrimary else borderColor, RoundedCornerShape(12.dp))
                            .clickable { isVideo = true }
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Videocam, contentDescription = "Video", tint = if (isVideo) PinkPrimary else textColor.copy(alpha = 0.5f))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Video Call", color = textColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("${model.videoPrice} Tokens/min", color = textColor.copy(alpha = 0.5f), fontSize = 10.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Date Selector
                Text("Select Appointment Date", color = textColor.copy(alpha = 0.5f), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    dates.forEach { date ->
                        val isSelected = selectedDate == date
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) PinkPrimary else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { selectedDate = date }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = date,
                                color = if (isSelected) Color.White else textColor.copy(alpha = 0.6f),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Time Slots Grid Selector
                Text("Available Daily Time Slots (30 mins)", color = textColor.copy(alpha = 0.5f), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (i in slots.indices step 2) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            for (j in i..i + 1) {
                                if (j < slots.size) {
                                    val slot = slots[j]
                                    val isSelected = selectedSlot == slot
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (isSelected) PinkPrimary else MaterialTheme.colorScheme.surfaceVariant)
                                            .clickable { selectedSlot = slot }
                                            .padding(vertical = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = slot,
                                            color = if (isSelected) Color.White else textColor.copy(alpha = 0.6f),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = borderColor)
                Spacer(modifier = Modifier.height(12.dp))

                // Cost Summary
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Reservation Cost (30m)", color = textColor.copy(alpha = 0.5f), fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "$totalCost ${if (isVideo) "Video" else "Audio"} Tokens",
                            color = if (isVideo) appErrorColor() else appSuccessColor(),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text("Your Balance", color = textColor.copy(alpha = 0.5f), fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "$balance Tokens",
                            color = textColor,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (!hasEnoughBalance) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(appErrorContainer())
                            .border(1.dp, appErrorColor().copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(
                                text = "⚠️ Insufficient Tokens",
                                color = appErrorColor(),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "You require ${totalCost - balance} more ${if (isVideo) "Video" else "Audio"} token credits to book this session.",
                                color = appSecondaryText(),
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (hasEnoughBalance) {
                Button(
                    onClick = {
                        val booking = CallBooking(
                            id = "b_${System.currentTimeMillis()}",
                            modelId = model.id,
                            modelName = modelUsername,
                            modelAvatarUrl = "https://api.dicebear.com/7.x/bottts/png?seed=${model.id}",
                            isVideo = isVideo,
                            date = selectedDate,
                            timeSlot = selectedSlot,
                            cost = totalCost
                        )
                        if (viewModel.scheduleCall(booking)) {
                            Toast.makeText(
                                context,
                                "Tokens paid. Session scheduled — waiting for model acceptance.",
                                Toast.LENGTH_LONG
                            ).show()
                            onDismiss()
                        } else {
                            Toast.makeText(
                                context,
                                "Payment failed. Please verify your token balance.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary)
                ) {
                    Text("Confirm & Schedule", color = Color.White, fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = {
                        onDismiss()
                        onViewPackages(if (isVideo) "Video" else "Audio")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary)
                ) {
                    Text("Buy Token Packs 💳", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = textColor.copy(alpha = 0.5f))
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    )
}
