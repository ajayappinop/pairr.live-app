package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.scale
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.MainViewModel
import com.example.ui.theme.PinkPrimary
import com.example.ui.theme.LightOnSurface
import com.example.ui.theme.bottomBarBrush
import com.example.ui.theme.isAppDarkTheme

@Composable
fun MainScreen(
    viewModel: MainViewModel,
    onModelClick: (String) -> Unit,
    onViewMoreTransactions: () -> Unit,
    onViewPackages: (String) -> Unit,
    onViewAll: () -> Unit,
    onLogout: () -> Unit,
    onUserClick: (String) -> Unit = {}
) {
    val bottomNavController = rememberNavController()
    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val isModelMode = viewModel.isModelMode.collectAsStateWithLifecycle().value
    val chatThreads by viewModel.chatThreads.collectAsStateWithLifecycle()
    val currentUserId by viewModel.currentUserId.collectAsStateWithLifecycle()
    val unreadChatCount = remember(chatThreads, isModelMode, currentUserId) {
        viewModel.getUnreadCount(isModelMode)
    }

    Scaffold(
        bottomBar = {
            CustomBottomNavigation(
                currentRoute = currentRoute,
                unreadChatCount = unreadChatCount
            ) { route ->
                bottomNavController.navigate(route) {
                    popUpTo(bottomNavController.graph.startDestinationId) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = bottomNavController,
            startDestination = "home",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("home") {
                val isModel = viewModel.isModelMode.collectAsStateWithLifecycle().value
                if (isModel) {
                    com.example.ui.screens.model.ModelSideHomeScreen(viewModel)
                } else {
                    DashboardScreen(
                        viewModel = viewModel,
                        onModelClick = onModelClick,
                        onViewAll = onViewAll,
                        onViewPackages = onViewPackages
                    )
                }
            }
            composable("discover") {
                Text(
                    "Discover Screen",
                    modifier = Modifier.fillMaxSize().wrapContentSize(),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            composable("chat") {
                val isModel = viewModel.isModelMode.collectAsStateWithLifecycle().value
                ChatTabScreen(
                    viewModel = viewModel,
                    isModelSide = isModel,
                    onUserClick = onUserClick
                )
            }
            composable("profile") {
                val isModel = viewModel.isModelMode.collectAsStateWithLifecycle().value
                if (isModel) {
                    com.example.ui.screens.model.ModelSideProfileScreen(viewModel, onLogout = onLogout)
                } else {
                    UserProfileScreen(
                        viewModel = viewModel,
                        onViewMoreTransactions = onViewMoreTransactions,
                        onViewPackages = onViewPackages,
                        onLogout = onLogout
                    )
                }
            }
            composable("calls") {
                val isModel = viewModel.isModelMode.collectAsStateWithLifecycle().value
                if (isModel) {
                    com.example.ui.screens.model.ModelSideHistoryScreen()
                } else {
                    CallDashboardScreen(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun CustomBottomNavigation(
    currentRoute: String?,
    unreadChatCount: Int = 0,
    onNavigate: (String) -> Unit
) {
    val isLight = !isAppDarkTheme()
    val barShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .shadow(
                    elevation = if (isLight) 12.dp else 16.dp,
                    shape = barShape,
                    clip = false,
                    ambientColor = if (isLight) PinkPrimary.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.2f)
                )
                .background(bottomBarBrush(), barShape)
                .then(
                    if (isLight) {
                        Modifier.border(
                            width = 1.dp,
                            brush = Brush.horizontalGradient(
                                listOf(
                                    PinkPrimary.copy(alpha = 0.08f),
                                    Color(0xFFFFE8CC).copy(alpha = 0.15f),
                                    PinkPrimary.copy(alpha = 0.08f)
                                )
                            ),
                            shape = barShape
                        )
                    } else Modifier
                )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            // Home
            BottomNavItem(
                icon = Icons.Default.Home,
                label = "Home",
                isSelected = currentRoute == "home",
                onClick = { onNavigate("home") },
                modifier = Modifier.weight(1f)
            )
            
            // Call History
            BottomNavItem(
                icon = Icons.Default.History,
                label = "History",
                isSelected = currentRoute == "calls",
                onClick = { onNavigate("calls") },
                modifier = Modifier.weight(1f)
            )
            
            // Chat
            BottomNavItem(
                icon = Icons.Outlined.ChatBubbleOutline,
                label = "Chat",
                isSelected = currentRoute == "chat",
                onClick = { onNavigate("chat") },
                modifier = Modifier.weight(1f),
                badgeCount = unreadChatCount
            )
            
            // Profile
            BottomNavItem(
                icon = Icons.Default.Person,
                label = "Profile",
                isSelected = currentRoute == "profile",
                onClick = { onNavigate("profile") },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun BottomNavItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    badgeCount: Int = 0
) {
    val isLight = !isAppDarkTheme()
    val unselectedTint = if (isLight) LightOnSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f)
    Box(
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier
                .padding(bottom = 12.dp, top = 24.dp)
                .then(
                    if (isSelected && isLight) {
                        Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(PinkPrimary.copy(alpha = 0.12f))
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    } else {
                        Modifier
                    }
                )
        ) {
            Box {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = if (isSelected) PinkPrimary else unselectedTint,
                    modifier = Modifier.size(26.dp)
                )
                if (badgeCount > 0) {
                    Box(
                        modifier = Modifier
                            .offset(x = 6.dp, y = (-4).dp)
                            .size(16.dp)
                            .background(PinkPrimary, CircleShape)
                            .border(width = 1.5.dp, color = MaterialTheme.colorScheme.surface, shape = CircleShape)
                            .align(Alignment.TopEnd),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = badgeCount.toString(),
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = label,
                color = if (isSelected) PinkPrimary else unselectedTint,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
            )
        }
    }
}
