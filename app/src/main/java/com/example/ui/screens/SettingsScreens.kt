package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.PersonOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.BlockedUser
import com.example.MainViewModel
import com.example.ui.theme.OrangeSecondary
import com.example.ui.theme.PinkPrimary
import com.example.ui.theme.SoftScreenBackground
import com.example.ui.theme.appSuccessColor
import com.example.ui.theme.appBorderColor
import com.example.ui.theme.appCaptionText
import com.example.ui.theme.appSecondaryText
import com.example.ui.theme.appSurfaceCard
import com.example.ui.theme.appTitleText
import com.example.ui.theme.AppBorderWeight

enum class SettingsSubScreen {
    Main,
    BlockedUsers,
    Faq,
    Terms,
    ContactSupport,
    ReportProblem
}

@Composable
fun SettingsSubScreenScaffold(
    title: String,
    onBack: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    val bg = MaterialTheme.colorScheme.background
    val textColor = MaterialTheme.colorScheme.onSurface

    SoftScreenBackground {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = textColor)
            }
            Text(title, color = textColor, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp),
            content = content
        )
    }
    }
}

@Composable
fun SuccessDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit
) {
    val textColor = MaterialTheme.colorScheme.onSurface
    val secondaryText = MaterialTheme.colorScheme.onSurfaceVariant
    val cardBg = MaterialTheme.colorScheme.surface

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        containerColor = cardBg,
        icon = {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = appSuccessColor(), modifier = Modifier.size(40.dp))
        },
        title = {
            Text(title, color = textColor, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        },
        text = {
            Text(message, color = secondaryText, fontSize = 14.sp, lineHeight = 22.sp)
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done", color = PinkPrimary, fontWeight = FontWeight.Bold)
            }
        },
        tonalElevation = 6.dp
    )
}

@Composable
fun BlockedUsersScreen(
    viewModel: MainViewModel?,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val blockedUsers by (viewModel?.blockedUsers?.collectAsStateWithLifecycle()
        ?: remember { mutableStateOf(emptyList()) })
    val cardBg = MaterialTheme.colorScheme.surface
    val borderColor = MaterialTheme.colorScheme.outline
    val textColor = MaterialTheme.colorScheme.onSurface
    val secondaryText = MaterialTheme.colorScheme.onSurfaceVariant

    SettingsSubScreenScaffold(title = "Blocked Accounts", onBack = onBack) {
        Text(
            "Blocked users and models cannot message or call you. Unblock them here at any time.",
            color = appSecondaryText(),
            fontSize = 14.sp,
            lineHeight = 20.sp
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (blockedUsers.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .appSurfaceCard(shape = RoundedCornerShape(20.dp))
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.PersonOff, contentDescription = null, tint = secondaryText, modifier = Modifier.size(48.dp))
                Spacer(modifier = Modifier.height(12.dp))
                Text("No blocked accounts", color = textColor, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "When you block someone from their profile page, they will appear here.",
                    color = secondaryText,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
        } else {
            blockedUsers.forEach { user ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                        .appSurfaceCard(shape = RoundedCornerShape(16.dp), borderWeight = AppBorderWeight.Default)
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = user.avatarUrl,
                        contentDescription = user.name,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(user.name, color = appTitleText(), fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                        Text("Blocked ${user.blockedAt}", color = appCaptionText(), fontSize = 12.sp)
                    }
                    TextButton(onClick = {
                        viewModel?.unblockUser(user.id)
                        Toast.makeText(context, "${user.name} unblocked", Toast.LENGTH_SHORT).show()
                    }) {
                        Text("Unblock", color = PinkPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun FaqScreen(onBack: () -> Unit) {
    val faqItems = remember {
        listOf(
            "How do I edit my profile?" to "Open the Profile tab, go to the Personal Info section, update your details, and tap Save Changes.",
            "How do rupees work?" to "Audio and video calls use separate rupee balances. Buy packs from the Rupee Store or your profile wallet section.",
            "How do I start a chat?" to "Open a model's profile and tap Chat, or go to the Chat tab to continue existing conversations.",
            "How do I block someone?" to "Open a user's or model's profile page and tap the block icon. Manage blocked accounts from Settings → Blocked Accounts.",
            "How do referrals work?" to "Share your invite code from Settings. When a friend signs up with it, you both receive 100 bonus rupees.",
            "How do I report a problem?" to "Go to Settings → Report A Problem, pick a category, describe the issue, and submit. Our team will review it."
        )
    }
    val expanded = remember { mutableStateListOf<Boolean>().apply { repeat(faqItems.size) { add(false) } } }
    val cardBg = MaterialTheme.colorScheme.surface
    val borderColor = MaterialTheme.colorScheme.outline
    val textColor = MaterialTheme.colorScheme.onSurface
    val secondaryText = MaterialTheme.colorScheme.onSurfaceVariant

    SettingsSubScreenScaffold(title = "FAQ", onBack = onBack) {
        Text(
            "Frequently asked questions about Pairr.",
            color = secondaryText,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(16.dp))

        faqItems.forEachIndexed { index, (question, answer) ->
            val isOpen = expanded[index]
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
                    .appSurfaceCard(shape = RoundedCornerShape(16.dp))
                    .clickable { expanded[index] = !isOpen }
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        question,
                        color = textColor,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        if (isOpen) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = PinkPrimary
                    )
                }
                if (isOpen) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(answer, color = appSecondaryText(), fontSize = 13.sp, lineHeight = 20.sp)
                }
            }
        }
    }
}

@Composable
fun TermsOfServiceScreen(onBack: () -> Unit) {
    val secondaryText = MaterialTheme.colorScheme.onSurfaceVariant
    val sections = remember {
        listOf(
            "1. Acceptance of Terms" to "By using Pairr, you agree to these terms and our community guidelines. If you do not agree, please discontinue use of the app.",
            "2. User Accounts" to "You are responsible for maintaining the confidentiality of your login credentials and for all activity under your account.",
            "3. Rupees & Payments" to "Rupees are used for audio and video sessions. Purchases are final unless required by applicable law. Bonus rupees may expire.",
            "4. Model Conduct" to "Models must provide accurate profile information, respect community standards, and honor published rates for calls.",
            "5. Privacy" to "We collect and use data as described in our privacy practices to operate chat, calls, payments, and safety features.",
            "6. Termination" to "We may suspend or terminate accounts that violate these terms. You may delete your account at any time from Settings."
        )
    }

    SettingsSubScreenScaffold(title = "Terms of Services", onBack = onBack) {
        Text(
            "Last updated: June 2026",
            color = secondaryText,
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        sections.forEach { (heading, body) ->
            Text(heading, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(body, color = secondaryText, fontSize = 14.sp, lineHeight = 22.sp)
            Spacer(modifier = Modifier.height(18.dp))
        }
    }
}

@Composable
fun ContactSupportScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val textColor = MaterialTheme.colorScheme.onSurface
    val secondaryText = MaterialTheme.colorScheme.onSurfaceVariant
    val cardBg = MaterialTheme.colorScheme.surface
    val borderColor = MaterialTheme.colorScheme.outline
    val pinkGradient = Brush.horizontalGradient(listOf(PinkPrimary, OrangeSecondary))

    var category by remember { mutableStateOf("General") }
    var message by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var showSuccess by remember { mutableStateOf(false) }
    val categories = listOf("General", "Account", "Payments", "Calls & Chat", "Technical Issue")

    SettingsSubScreenScaffold(title = "Contact Support", onBack = onBack) {
        Text(
            "Our support team typically responds within 24 hours.",
            color = secondaryText,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text("Category", color = textColor, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(8.dp))
        categories.chunked(2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { item ->
                    FilterChip(
                        selected = category == item,
                        onClick = { category = item },
                        label = { Text(item, fontSize = 12.sp) },
                        modifier = Modifier.weight(1f),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PinkPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
                if (row.size == 1) Spacer(modifier = Modifier.weight(1f))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email (optional)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = message,
            onValueChange = { message = it },
            label = { Text("Your message") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 5,
            maxLines = 8,
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = {
                if (message.isBlank()) {
                    Toast.makeText(context, "Please enter a message.", Toast.LENGTH_SHORT).show()
                } else {
                    showSuccess = true
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            contentPadding = PaddingValues(0.dp),
            shape = RoundedCornerShape(14.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize().background(pinkGradient, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("Send Message", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showSuccess) {
        SuccessDialog(
            title = "Message Sent",
            message = "Thanks for reaching out! Our support team will reply to you soon.",
            onDismiss = {
                showSuccess = false
                onBack()
            }
        )
    }
}

@Composable
fun ReportProblemScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val textColor = MaterialTheme.colorScheme.onSurface
    val secondaryText = MaterialTheme.colorScheme.onSurfaceVariant
    val pinkGradient = Brush.horizontalGradient(listOf(PinkPrimary, OrangeSecondary))

    var category by remember { mutableStateOf("App Bug") }
    var description by remember { mutableStateOf("") }
    var showSuccess by remember { mutableStateOf(false) }
    val categories = listOf("App Bug", "Harassment", "Payment Issue", "Call Quality", "Fake Profile", "Other")

    SettingsSubScreenScaffold(title = "Report A Problem", onBack = onBack) {
        Text(
            "Tell us what went wrong. Reports are reviewed by our trust & safety team.",
            color = secondaryText,
            fontSize = 14.sp,
            lineHeight = 20.sp
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text("Issue type", color = textColor, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(8.dp))
        categories.chunked(2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { item ->
                    FilterChip(
                        selected = category == item,
                        onClick = { category = item },
                        label = { Text(item, fontSize = 12.sp) },
                        modifier = Modifier.weight(1f),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PinkPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
                if (row.size == 1) Spacer(modifier = Modifier.weight(1f))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Describe the issue") },
            placeholder = { Text("Include steps to reproduce if reporting a bug…") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 6,
            maxLines = 10,
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = {
                if (description.isBlank()) {
                    Toast.makeText(context, "Please describe the problem.", Toast.LENGTH_SHORT).show()
                } else {
                    showSuccess = true
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            contentPadding = PaddingValues(0.dp),
            shape = RoundedCornerShape(14.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize().background(pinkGradient, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("Submit Report", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showSuccess) {
        SuccessDialog(
            title = "Report Submitted",
            message = "Thank you. We have received your report and will investigate shortly.",
            onDismiss = {
                showSuccess = false
                onBack()
            }
        )
    }
}
