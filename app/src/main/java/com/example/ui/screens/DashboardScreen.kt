package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.MainViewModel
import com.example.R
import com.example.data.publicUsername
import com.example.data.formattedBalanceCompact
import com.example.data.Wallet
import com.example.ui.components.ModelCardMedia
import com.example.ui.components.NotificationsSheet
import com.example.ui.components.ModelStoryCircle
import com.example.ui.theme.OrangeSecondary
import com.example.ui.theme.PinkPrimary
import com.example.ui.theme.SoftScreenBackground
import com.example.ui.theme.DarkPromoGradients
import com.example.ui.theme.accentHorizontalGradient
import com.example.ui.theme.appBorderColor
import com.example.ui.theme.appSuccessColor
import com.example.ui.theme.appStarColor
import com.example.ui.theme.appCaptionText
import com.example.ui.theme.appMutedText
import com.example.ui.theme.appOutlinedFieldColors
import com.example.ui.theme.appSecondaryText
import com.example.ui.theme.appSoftShadow
import com.example.ui.theme.appSurfaceCard
import com.example.ui.theme.appTitleText
import com.example.ui.theme.AppBorderWeight
import com.example.ui.theme.isAppDarkTheme
import com.example.ui.theme.isCompactWidth
import com.example.ui.theme.selectedChipBrush

import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay

@Composable
fun DashboardScreen(
    viewModel: MainViewModel,
    onModelClick: (String) -> Unit,
    onViewAll: () -> Unit,
    onViewPackages: (String) -> Unit = {},
    onRandomPeerCall: (String) -> Unit = {}
) {
    val favorites by viewModel.favorites.collectAsState()
    val allModels by viewModel.models.collectAsState()
    val blockedUsers by viewModel.blockedUsers.collectAsState()
    
    var selectedCategory by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    
    val models = remember(allModels, selectedCategory, searchQuery, blockedUsers) {
        var filtered = allModels.filter { model -> blockedUsers.none { it.id == model.id } }
        if (selectedCategory != "All") {
            filtered = filtered.filter { it.categories.contains(selectedCategory) }
        }
        if (searchQuery.isNotEmpty()) {
            filtered = filtered.filter { it.publicUsername().contains(searchQuery, ignoreCase = true) }
        }
        filtered
    }
    
    var showTutorialDialog by remember { mutableStateOf(false) }
    var showNotificationsSheet by remember { mutableStateOf(false) }
    var showRandomMatching by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val unreadNotificationCount by viewModel.notifications.collectAsState()
    val unreadCount = unreadNotificationCount.count { !it.isRead }
    val wallet by viewModel.walletState.collectAsStateWithLifecycle()

    SoftScreenBackground {
        Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 110.dp)
        ) {
            item {
                TopBarSection(
                    searchQuery = searchQuery,
                    onSearchQueryChanged = { searchQuery = it },
                    unreadNotificationCount = unreadCount,
                    onNotificationsClick = { showNotificationsSheet = true },
                    totalTokenBalance = wallet.formattedBalanceCompact()
                )

                Spacer(modifier = Modifier.height(4.dp))
                HomePromoBannerCarousel(
                    onViewPackages = onViewPackages,
                    onShowTutorial = { showTutorialDialog = true }
                )

                Spacer(modifier = Modifier.height(12.dp))
                RandomCallHighlightCard(
                    onClick = { showRandomMatching = true },
                    modifier = Modifier.padding(horizontal = 18.dp)
                )

                // Welcome Message
                Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp)) {
                    Text(
                        text = "Discover Amazing People",
                        color = appTitleText(),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = "Connect with top-rated models instantly",
                        color = appSecondaryText(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                CategoriesSection(selectedCategory = selectedCategory, onCategorySelected = { selectedCategory = it })
                
                SectionHeader("Live Now ✨", "Jump into active sessions", onViewAll = onViewAll)
                LiveNowSection(models = models.filter { it.status == "Online" }, onModelClick = onModelClick)
                
                Spacer(modifier = Modifier.height(24.dp))
                SectionHeader("Recommended For You ❤️", "Based on your interests", onViewAll = onViewAll)
                PeopleYouMayLikeSection(
                    models = models,
                    favorites = favorites,
                    onFavoriteToggle = { viewModel.toggleFavorite(it) },
                    onModelClick = onModelClick
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                SectionHeader("Popular Communities 🌎", "Join the conversation", onViewAll = onViewAll)
                PopularRoomsSection()
                
                Spacer(modifier = Modifier.height(20.dp))
            }
        }

            if (showRandomMatching) {
                RandomCallMatchingOverlay(
                    viewModel = viewModel,
                    onMatched = { peerUserId ->
                        showRandomMatching = false
                        onRandomPeerCall(peerUserId)
                    },
                    onDismiss = {
                        showRandomMatching = false
                        Toast.makeText(context, "No users available right now. Try again soon.", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }

    if (showTutorialDialog) {
        HomeTutorialDialog(onDismiss = { showTutorialDialog = false })
    }

    if (showNotificationsSheet) {
        NotificationsSheet(
            viewModel = viewModel,
            onDismiss = { showNotificationsSheet = false }
        )
    }

}

@Composable
fun TopBarSection(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    unreadNotificationCount: Int = 0,
    onNotificationsClick: () -> Unit = {},
    totalTokenBalance: String = Wallet().formattedBalanceCompact()
) {
    val textColor = appTitleText()
    val isLight = !isAppDarkTheme()
    val compact = isCompactWidth()
    val searchShape = RoundedCornerShape(16.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = if (compact) 12.dp else 18.dp, vertical = if (compact) 14.dp else 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChanged,
            modifier = Modifier
                .weight(1f)
                .height(if (compact) 46.dp else 50.dp)
                .appSoftShadow(searchShape, elevation = if (isLight) 6.dp else 2.dp),
            placeholder = { Text("Search models...", color = appCaptionText()) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = appMutedText()) },
            shape = searchShape,
            colors = appOutlinedFieldColors(),
            singleLine = true
        )
        
        Spacer(modifier = Modifier.width(if (compact) 8.dp else 16.dp))
        
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(if (compact) 8.dp else 14.dp)) {
            // Coin status
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .appSoftShadow(RoundedCornerShape(12.dp), elevation = if (isLight) 4.dp else 2.dp)
                    .background(accentHorizontalGradient())
                    .padding(horizontal = if (compact) 8.dp else 10.dp, vertical = if (compact) 5.dp else 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.MonetizationOn, contentDescription = "Tokens", tint = Color.White, modifier = Modifier.size(if (compact) 14.dp else 16.dp))
                Spacer(modifier = Modifier.width(if (compact) 4.dp else 6.dp))
                Text(
                    totalTokenBalance,
                    color = Color.White,
                    fontSize = if (compact) 11.sp else 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }

            // Notification
            Box(
                modifier = Modifier.clickable(onClick = onNotificationsClick)
            ) {
                Icon(Icons.Outlined.Notifications, contentDescription = "Notifications", tint = textColor, modifier = Modifier.size(if (compact) 22.dp else 26.dp))
                if (unreadNotificationCount > 0) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(PinkPrimary, CircleShape)
                            .border(1.5.dp, MaterialTheme.colorScheme.background, CircleShape)
                            .align(Alignment.TopEnd)
                    )
                }
            }
        }
    }
}

@Composable
fun CategoriesSection(selectedCategory: String, onCategorySelected: (String) -> Unit) {
    val categories = listOf("All", "Top Rated", "Available", "Popular", "New", "Gaming")
    val isLight = !isAppDarkTheme()
    val chipShape = RoundedCornerShape(14.dp)
    
    LazyRow(
        contentPadding = PaddingValues(horizontal = 18.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(categories) { category ->
            val isSelected = category == selectedCategory
            Box(
                modifier = Modifier
                    .then(
                        if (isSelected && isLight) {
                            Modifier.appSoftShadow(chipShape, elevation = 4.dp)
                        } else if (!isSelected && isLight) {
                            Modifier.appSoftShadow(chipShape, elevation = 2.dp)
                        } else Modifier
                    )
                    .clip(chipShape)
                    .then(
                        if (isSelected) {
                            Modifier.background(accentHorizontalGradient())
                        } else {
                            Modifier
                                .background(MaterialTheme.colorScheme.surface)
                                .border(1.dp, appBorderColor(AppBorderWeight.Default), chipShape)
                        }
                    )
                    .clickable { onCategorySelected(category) }
            ) {
                Text(
                    text = category,
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        isSelected -> Color.White
                        else -> appMutedText()
                    }
                )
            }
        }
    }
}

@Composable
fun HomePromoBannerCarousel(
    onViewPackages: (String) -> Unit,
    onShowTutorial: () -> Unit
) {
    val banners = remember {
        listOf(
            HomePromoBanner(
                id = "tutorial",
                badge = "GET STARTED",
                title = "How Pairr Works",
                subtitle = "Learn to browse models, chat, and start audio or video calls in minutes.",
                cta = "Watch Tutorial",
                icon = Icons.Default.School,
                gradient = DarkPromoGradients.tutorial,
                action = HomePromoAction.Tutorial
            ),
            HomePromoBanner(
                id = "welcome_tokens",
                badge = "WELCOME OFFER",
                title = "250 Bonus Tokens",
                subtitle = "Get extra tokens free on your first token purchase. Limited time only!",
                cta = "Claim Offer",
                icon = Icons.Default.LocalOffer,
                gradient = DarkPromoGradients.welcome,
                action = HomePromoAction.TokenPackages("All")
            ),
            HomePromoBanner(
                id = "audio_scheme",
                badge = "AUDIO PACKS",
                title = "Save 20% on Audio",
                subtitle = "Buy audio token packs and enjoy longer conversations at lower rates.",
                cta = "Buy Audio Tokens",
                icon = Icons.Default.MonetizationOn,
                gradient = DarkPromoGradients.audio,
                action = HomePromoAction.TokenPackages("Audio")
            ),
            HomePromoBanner(
                id = "video_scheme",
                badge = "VIDEO PACKS",
                title = "Premium Video Deals",
                subtitle = "HD video call packs with up to 35% savings on Supreme VIP plans.",
                cta = "Buy Video Tokens",
                icon = Icons.Default.PlayCircle,
                gradient = DarkPromoGradients.video,
                action = HomePromoAction.TokenPackages("Video")
            ),
            HomePromoBanner(
                id = "token_guide",
                badge = "TOKEN GUIDE",
                title = "Understand Tokens",
                subtitle = "See how audio and video tokens work, pricing, and the best packs for you.",
                cta = "Read Guide",
                icon = Icons.Default.School,
                gradient = DarkPromoGradients.guide,
                action = HomePromoAction.Tutorial
            )
        )
    }

    val pagerState = rememberPagerState(pageCount = { banners.size })

    LaunchedEffect(pagerState) {
        while (true) {
            delay(4500)
            val nextPage = (pagerState.currentPage + 1) % banners.size
            pagerState.animateScrollToPage(nextPage)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(168.dp),
            contentPadding = PaddingValues(horizontal = 18.dp),
            pageSpacing = 12.dp
        ) { page ->
            val banner = banners[page]
            HomePromoBannerCard(
                banner = banner,
                onClick = {
                    when (banner.action) {
                        HomePromoAction.Tutorial -> onShowTutorial()
                        is HomePromoAction.TokenPackages -> onViewPackages(banner.action.filter)
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            banners.indices.forEach { index ->
                val isSelected = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .height(6.dp)
                        .width(if (isSelected) 22.dp else 6.dp)
                        .clip(RoundedCornerShape(50))
                        .background(
                            if (isSelected) PinkPrimary
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                        )
                )
            }
        }
    }
}

private data class HomePromoBanner(
    val id: String,
    val badge: String,
    val title: String,
    val subtitle: String,
    val cta: String,
    val icon: ImageVector,
    val gradient: List<Color>,
    val action: HomePromoAction
)

private sealed class HomePromoAction {
    data object Tutorial : HomePromoAction()
    data class TokenPackages(val filter: String) : HomePromoAction()
}

@Composable
private fun HomePromoBannerCard(
    banner: HomePromoBanner,
    onClick: () -> Unit
) {
    val brush = Brush.linearGradient(banner.gradient)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(168.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(brush)
            .clickable { onClick() }
            .padding(18.dp)
    ) {
        Icon(
            imageVector = banner.icon,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.18f),
            modifier = Modifier
                .size(88.dp)
                .align(Alignment.CenterEnd)
                .offset(x = 12.dp, y = 8.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(end = 72.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White.copy(alpha = 0.22f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        banner.badge,
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.8.sp
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    banner.title,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    lineHeight = 24.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    banner.subtitle,
                    color = Color.White.copy(alpha = 0.88f),
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    banner.cta,
                    color = PinkPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun HomeTutorialDialog(onDismiss: () -> Unit) {
    val textColor = MaterialTheme.colorScheme.onSurface
    val secondaryText = MaterialTheme.colorScheme.onSurfaceVariant
    val cardBg = MaterialTheme.colorScheme.surface

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        containerColor = cardBg,
        title = {
            Text("Pairr Quick Guide", color = textColor, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                TutorialStep(number = "1", title = "Browse Models", body = "Explore live and recommended models from the home feed or View All.")
                TutorialStep(number = "2", title = "Buy Tokens", body = "Purchase audio or video token packs from the Token Store to start calls.")
                TutorialStep(number = "3", title = "Chat & Connect", body = "Message a model first or jump straight into an audio/video call.")
                TutorialStep(number = "4", title = "Manage Wallet", body = "Track your balance, transactions, and bonus offers from your profile.")
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Got it", color = PinkPrimary, fontWeight = FontWeight.Bold)
            }
        },
        tonalElevation = 6.dp
    )
}

@Composable
private fun TutorialStep(number: String, title: String, body: String) {
    val textColor = MaterialTheme.colorScheme.onSurface
    val secondaryText = MaterialTheme.colorScheme.onSurfaceVariant

    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(PinkPrimary.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text(number, color = PinkPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(title, color = textColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(body, color = secondaryText, fontSize = 13.sp, lineHeight = 18.sp)
        }
    }
}

@Composable
fun HeroBanner() {
    val pinkGradient = Brush.horizontalGradient(listOf(PinkPrimary, OrangeSecondary))
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp)
            .height(200.dp)
            .clip(RoundedCornerShape(30.dp))
    ) {
        AsyncImage(
            model = "https://images.unsplash.com/photo-1529626455594-4ff0802cfb7e?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80",
            contentDescription = "Hero Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))))
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.Black.copy(alpha = 0.4f))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFF00E676)))
                Spacer(modifier = Modifier.width(6.dp))
                Text("HOT NOW", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Black)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Star of the Week: Alessia", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black)
            Text("Join her live session for exclusive rewards", color = Color.White.copy(alpha = 0.75f), fontSize = 13.sp)
        }
        
        // Join Button floating inside
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
        ) {
            Button(
                onClick = { /* TODO: Implement Watch Live navigation */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.height(44.dp).width(110.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize().background(pinkGradient, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Watch Live", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, subtitle: String? = null, onViewAll: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Column {
            Text(title, color = appTitleText(), fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
            if (subtitle != null) {
                Text(subtitle, color = appCaptionText(), fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
        }
        Text(
            text = "View All", 
            color = PinkPrimary, 
            fontSize = 14.sp, 
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { onViewAll() }
        )
    }
}

@Composable
fun AvailabilityBadge(status: String, modifier: Modifier = Modifier) {
    val bgColor = when (status) {
        "Online" -> appSuccessColor()
        "Busy" -> Color(0xFFF57C00)
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val textColor = if (status == "Offline") MaterialTheme.colorScheme.onSurfaceVariant else Color.White
    val label = when (status) {
        "Online" -> "● Online"
        "Busy" -> "■ Busy"
        else -> "○ Offline"
    }
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(bgColor.copy(alpha = 0.85f))
            .padding(horizontal = 6.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun LiveNowSection(models: List<com.example.data.AppModel>, onModelClick: (String) -> Unit) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 18.dp),
        horizontalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        items(models) { model ->
            ModelStoryCircle(
                model = model,
                onClick = { onModelClick(model.id) },
                showLiveBadge = true
            )
        }
    }
}

@Composable
fun PeopleYouMayLikeSection(
    models: List<com.example.data.AppModel>,
    favorites: Set<String>,
    onFavoriteToggle: (String) -> Unit,
    onModelClick: (String) -> Unit
) {
    val cardShape = RoundedCornerShape(24.dp)
    
    LazyRow(
        contentPadding = PaddingValues(horizontal = 18.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(models.shuffled()) { model ->
            Column(
                modifier = Modifier
                    .width(180.dp)
                    .appSurfaceCard(shape = cardShape, borderWeight = AppBorderWeight.Default)
                    .clickable { onModelClick(model.id) }
            ) {
                Box {
                    ModelCardMedia(
                        model = model,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
                        useProfilePhotoOnly = true
                    )
                    
                    val isFavorite = favorites.contains(model.id)
                    IconButton(
                        onClick = { onFavoriteToggle(model.id) },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .size(32.dp)
                            .background(Color.Black.copy(alpha = 0.3f), CircleShape)
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) PinkPrimary else Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    
                    // Rating overlay
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(10.dp)
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(10.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(String.format("%.1f", model.rating), color = MaterialTheme.colorScheme.onSurface, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(model.publicUsername(), color = appTitleText(), fontSize = 16.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                    Text(
                        "${model.status} • ${model.categories.firstOrNull()}",
                        color = appCaptionText(),
                        fontSize = 12.sp,
                        maxLines = 1
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Button(
                        onClick = { onModelClick(model.id) },
                        modifier = Modifier.fillMaxWidth().height(36.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            "View Profile",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PopularRoomsSection() {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(4) { index ->
            Box(
                modifier = Modifier
                    .width(150.dp)
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
            ) {
                AsyncImage(
                    model = "https://images.unsplash.com/photo-1542382156909-9ae37b3f56fd?ixlib=rb-4.0.3&auto=format&fit=crop&w=300&q=80", // using a generic room image
                    contentDescription = "Room",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                
                 // Overlay gradient with slightly purple tint
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0x884A00E0)) // Purple tint
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.verticalGradient(
                            colors = listOf(Color(0x000B0A10), Color(0xEE0B0A10))
                        ))
                )
                
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                ) {
                    val titles = listOf("Chill Vibes 🎧", "Singing Live 🎵", "Game On 🎮", "Chat Room 💬")
                    Text(titles[index], color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Person, contentDescription = "Members", tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("${1.5 + index * 0.3}K", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    }
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    Row {
                        listOf("1", "2", "3").forEachIndexed { avatarIndex, _ ->
                            AsyncImage(
                                model = "https://i.pravatar.cc/100?img=${avatarIndex + index * 3 + 40}",
                                contentDescription = "Avatar",
                                modifier = Modifier
                                    .size(28.dp)
                                    .offset(x = if (avatarIndex > 0) -(avatarIndex * 10).dp else 0.dp)
                                    .clip(CircleShape)
                                    .border(1.5.dp, appBorderColor(), CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RandomCallHighlightCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(20.dp)
    val gradient = Brush.horizontalGradient(listOf(PinkPrimary, OrangeSecondary))

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(gradient)
            .clickable(onClick = onClick)
            .padding(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(gradient),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Shuffle,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Random Call",
                    color = appTitleText(),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Instant video chat with another user — not models",
                    color = appSecondaryText(),
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }

            Icon(
                imageVector = Icons.Default.Videocam,
                contentDescription = null,
                tint = PinkPrimary,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun RandomCallMatchingOverlay(
    viewModel: MainViewModel,
    onMatched: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var statusText by remember { mutableStateOf("Finding someone nearby...") }

    LaunchedEffect(Unit) {
        delay(1200)
        statusText = "Matching you with a user..."
        delay(900)
        val match = viewModel.findRandomPeerUser()
        if (match != null) {
            statusText = "Connected with ${match.name}!"
            delay(400)
            onMatched(match.userId)
        } else {
            onDismiss()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.88f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape)
                    .background(PinkPrimary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = PinkPrimary,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(56.dp)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Random Video Call",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = statusText,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "User-to-user only · Models are not included",
                color = PinkPrimary.copy(alpha = 0.85f),
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
