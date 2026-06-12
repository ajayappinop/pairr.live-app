package com.example.ui.screens.model

import androidx.compose.runtime.Composable
import com.example.MainViewModel
import com.example.ui.screens.ChatTabScreen

@Composable
fun ModelSideChatScreen(viewModel: MainViewModel) {
    ChatTabScreen(
        viewModel = viewModel,
        isModelSide = true
    )
}
