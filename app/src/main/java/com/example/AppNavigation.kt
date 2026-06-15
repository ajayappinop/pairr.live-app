package com.example

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.theme.appTopSafeArea
import com.example.ui.screens.*
import com.example.ui.screens.model.ModelCallEarningsScreen

@Composable
fun PairrApp(viewModel: MainViewModel) {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = "login",
        modifier = Modifier.appTopSafeArea()
    ) {
        composable("login") {
            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = {
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onSignUpClick = {
                    navController.navigate("signup")
                }
            )
        }
        composable("signup") {
            SignUpScreen(
                viewModel = viewModel,
                onSignUpSuccess = {
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        composable("main") {
            MainScreen(
                viewModel = viewModel,
                onModelClick = { modelId ->
                    navController.navigate("model_detail/$modelId")
                },
                onViewMoreTransactions = {
                    navController.navigate("transactions")
                },
                onViewPackages = { initialFilter ->
                    navController.navigate("packages/$initialFilter")
                },
                onViewAll = {
                    navController.navigate("view_all")
                },
                onLogout = {
                    viewModel.logout()
                    navController.navigate("login") {
                        popUpTo("main") { inclusive = true }
                    }
                },
                onUserClick = { userId ->
                    navController.navigate("user_detail/${Uri.encode(userId)}")
                },
                onRandomPeerCall = { userId ->
                    navController.navigate("peer_call/${Uri.encode(userId)}")
                },
                onViewAllCallEarnings = {
                    navController.navigate("model_call_earnings")
                }
            )
        }
        composable("view_all") {
            ViewAllModelsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onModelClick = { modelId ->
                    navController.navigate("model_detail/$modelId")
                }
            )
        }
        composable("transactions") {
            TransactionsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable("model_call_earnings") {
            ModelCallEarningsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onUserClick = { userId ->
                    navController.navigate("user_detail/${Uri.encode(userId)}")
                }
            )
        }
        composable("packages/{initialFilter}") { backStackEntry ->
            val initialFilter = backStackEntry.arguments?.getString("initialFilter") ?: "All"
            PackagesScreen(
                viewModel = viewModel,
                initialFilter = initialFilter,
                onBack = { navController.popBackStack() }
            )
        }
        composable("user_detail/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")?.let { Uri.decode(it) }
            UserDetailScreen(
                userId = userId,
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onChat = { navController.popBackStack() }
            )
        }
        composable("model_detail/{modelId}") { backStackEntry ->
            val modelId = backStackEntry.arguments?.getString("modelId")
            ModelDetailScreen(
                modelId = modelId,
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onCall = { id, isVideo ->
                    navController.navigate("call/$id/$isVideo")
                },
                onChat = { id ->
                    navController.navigate("chat/$id/false")
                },
                onViewPackages = { initialFilter ->
                    navController.navigate("packages/$initialFilter")
                }
            )
        }
        composable("call/{modelId}/{isVideo}") { backStackEntry ->
            val modelId = backStackEntry.arguments?.getString("modelId")
            val isVideo = backStackEntry.arguments?.getString("isVideo")?.toBoolean() ?: false
            CallScreen(
                modelId = modelId,
                isVideo = isVideo,
                viewModel = viewModel,
                onEndCall = { navController.popBackStack() }
            )
        }
        composable("peer_call/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")?.let { Uri.decode(it) }
            PeerVideoCallScreen(
                peerUserId = userId,
                viewModel = viewModel,
                onEndCall = { navController.popBackStack() }
            )
        }
        composable("chat/{modelId}/{isModelSide}") { backStackEntry ->
            val modelId = backStackEntry.arguments?.getString("modelId") ?: ""
            val isModelSide = backStackEntry.arguments?.getString("isModelSide")?.toBoolean() ?: false
            val models by viewModel.models.collectAsStateWithLifecycle()
            val model = models.find { it.id == modelId }
            val modelName = model?.name ?: "Chat"
            val avatarUrl = "https://i.pravatar.cc/150?u=$modelId"
            var threadId by remember(modelId) { mutableStateOf("") }

            LaunchedEffect(modelId) {
                if (modelId.isNotBlank()) {
                    threadId = viewModel.ensureChatThread(
                        modelId = modelId,
                        modelName = modelName,
                        modelAvatarUrl = avatarUrl,
                        isOnline = model?.isOnline == true
                    )
                    viewModel.markThreadAsRead(threadId)
                }
            }

            if (threadId.isNotBlank()) {
                ChatScreen(
                    threadId = threadId,
                    participantName = modelName,
                    isModelSide = isModelSide,
                    showBackButton = true,
                    onBack = { navController.popBackStack() },
                    viewModel = viewModel,
                    participantAvatarUrl = avatarUrl
                )
            }
        }
    }
}
