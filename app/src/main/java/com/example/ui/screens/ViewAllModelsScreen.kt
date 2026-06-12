package com.example.ui.screens

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.MainViewModel
import com.example.data.AppModel
import com.example.ui.components.ModelCardMedia
import com.example.ui.theme.OrangeSecondary
import com.example.ui.theme.PinkPrimary
import com.example.ui.theme.SoftScreenBackground
import com.example.ui.theme.appBorderColor
import com.example.ui.theme.appCaptionText
import com.example.ui.theme.appMutedText
import com.example.ui.theme.appOutlinedFieldColors
import com.example.ui.theme.appSecondaryText
import com.example.ui.theme.appSoftShadow
import com.example.ui.theme.appSurfaceCard
import com.example.ui.theme.appTitleText
import com.example.ui.theme.AppBorderWeight
import com.example.ui.theme.isAppDarkTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewAllModelsScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onModelClick: (String) -> Unit = {}
) {
    val allModels by viewModel.models.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val blockedUsers by viewModel.blockedUsers.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }

    val visibleModels = remember(allModels, blockedUsers) {
        allModels.filter { model -> blockedUsers.none { it.id == model.id } }
    }

    val categories = remember(visibleModels) {
        listOf("All") + visibleModels.flatMap { it.categories }.distinct().sorted()
    }

    val filteredModels = remember(visibleModels, searchQuery, selectedFilter) {
        visibleModels
            .filter { model ->
                val matchesSearch = searchQuery.isBlank() ||
                    model.name.contains(searchQuery, ignoreCase = true) ||
                    model.categories.any { it.contains(searchQuery, ignoreCase = true) } ||
                    model.bio.contains(searchQuery, ignoreCase = true)
                val matchesFilter = when (selectedFilter) {
                    "All" -> true
                    "Online" -> model.status == "Online"
                    "Featured" -> model.isFeatured
                    else -> model.categories.contains(selectedFilter)
                }
                matchesSearch && matchesFilter
            }
            .sortedWith(
                compareByDescending<AppModel> { it.isFeatured }
                    .thenByDescending { it.status == "Online" }
                    .thenByDescending { it.rating }
            )
    }

    val textColor = appTitleText()
    val secondaryText = appSecondaryText()
    val isLight = !isAppDarkTheme()
    
    SoftScreenBackground {
    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Browse Creators",
                            color = textColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            "${filteredModels.size} available",
                            color = secondaryText,
                            fontSize = 12.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = textColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                    Column(modifier = Modifier.padding(horizontal = 18.dp)) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .appSoftShadow(RoundedCornerShape(16.dp), elevation = if (isLight) 6.dp else 2.dp),
                            placeholder = {
                                Text("Search by name, category, or bio…", color = appCaptionText())
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Search, contentDescription = "Search", tint = appMutedText())
                            },
                            shape = RoundedCornerShape(16.dp),
                            colors = appOutlinedFieldColors(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            val filters = buildList {
                                add("All")
                                add("Online")
                                add("Featured")
                                addAll(categories.filter { it != "All" })
                            }
                            items(filters.distinct()) { filter ->
                                FilterChip(
                                    selected = selectedFilter == filter,
                                    onClick = { selectedFilter = filter },
                                    label = {
                                        Text(
                                            filter,
                                            fontSize = 13.sp,
                                            fontWeight = if (selectedFilter == filter) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = PinkPrimary,
                                        selectedLabelColor = Color.White,
                                        containerColor = MaterialTheme.colorScheme.surface,
                                        labelColor = appSecondaryText()
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        enabled = true,
                                        selected = selectedFilter == filter,
                                        borderColor = appBorderColor(AppBorderWeight.Default),
                                        selectedBorderColor = appBorderColor(AppBorderWeight.Focus)
                                    )
                                )
                            }
                        }
                    }
                }

                if (filteredModels.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 18.dp, vertical = 48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    "No creators found",
                                    color = textColor,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Try a different search or filter",
                                    color = secondaryText,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                } else {
                    items(filteredModels, key = { it.id }) { model ->
                        ViewAllModelCard(
                            model = model,
                            isFavorite = favorites.contains(model.id),
                            onFavoriteToggle = { viewModel.toggleFavorite(model.id) },
                            onClick = { onModelClick(model.id) }
                        )
                    }
                }
            }
        }
    }
    }
}

@Composable
private fun ViewAllModelCard(
    model: AppModel,
    isFavorite: Boolean,
    onFavoriteToggle: () -> Unit,
    onClick: () -> Unit
) {
    val cardShape = RoundedCornerShape(20.dp)
    val pinkGradient = Brush.horizontalGradient(listOf(PinkPrimary, OrangeSecondary))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp)
            .appSurfaceCard(shape = cardShape, borderWeight = AppBorderWeight.Default)
            .clickable(onClick = onClick)
    ) {
        if (model.isFeatured) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(10.dp)
                    .background(PinkPrimary, RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text("★ Featured", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(RoundedCornerShape(16.dp))
        ) {
            ModelCardMedia(
                model = model,
                modifier = Modifier.fillMaxSize(),
                useProfilePhotoOnly = true
            )
            AvailabilityBadge(
                status = model.status,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(6.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))
                        
                        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    model.name,
                    color = appTitleText(),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                IconButton(
                    onClick = onFavoriteToggle,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) PinkPrimary else appMutedText(),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    String.format("%.1f", model.rating),
                    color = appTitleText(),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    " • ${model.reviewsCount} reviews",
                    color = appCaptionText(),
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                model.bio,
                color = appSecondaryText(),
                fontSize = 12.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 16.sp
            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
            Text(
                model.categories.joinToString(" • "),
                color = PinkPrimary.copy(alpha = 0.85f),
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RatePill(
                    icon = Icons.Default.Call,
                    label = "${model.audioPrice}/min",
                    tint = Color(0xFF3DDC84),
                    bg = Color(0xFF3DDC84).copy(alpha = 0.12f)
                )
                RatePill(
                    icon = Icons.Default.Videocam,
                    label = "${model.videoPrice}/min",
                    tint = PinkPrimary,
                    bg = PinkPrimary.copy(alpha = 0.12f)
                )
                Spacer(modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(pinkGradient)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("View", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        }
    }
}

@Composable
private fun RatePill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    tint: Color,
    bg: Color
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(12.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(label, color = tint, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
    }
}
