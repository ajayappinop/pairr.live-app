package com.example

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
                onModelRegistrationClick = {
                    navController.navigate("model_registration")
                }
            )
        }
        composable("model_registration") {
            ModelRegistrationScreen(
                viewModel = viewModel,
                onRegistrationSuccess = {
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
                onCall = { modelId, isVideo ->
                    navController.navigate("call/$modelId/$isVideo")
                },
                onViewMoreTransactions = {
                    navController.navigate("transactions")
                },
                onViewPackages = { initialFilter ->
                    navController.navigate("packages/$initialFilter")
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
                onViewAllCallEarnings = {
                    navController.navigate("model_call_earnings")
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
                onChat = { chatUserId ->
                    navController.navigate("user_chat/${Uri.encode(chatUserId)}")
                }
            )
        }
        composable("user_chat/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")?.let { Uri.decode(it) } ?: ""
            val thread = viewModel.getThreadWithUser(userId)
            val canChat = userId.isNotBlank() && viewModel.canModelChatWithUser(userId)

            if (thread != null && canChat) {
                val participantName = thread.userName.ifBlank {
                    viewModel.getUserPublicUsername(userId)
                }
                ChatScreen(
                    threadId = thread.id,
                    participantName = participantName,
                    isModelSide = true,
                    showBackButton = true,
                    onBack = { navController.popBackStack() },
                    viewModel = viewModel,
                    participantAvatarUrl = thread.userAvatarUrl
                )
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Chat unavailable", color = MaterialTheme.colorScheme.onSurface)
                }
            }
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
