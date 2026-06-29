package com.example.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.Wallet
import com.example.data.formattedBalanceFull
import com.example.data.publicUsername
import com.example.ui.theme.OrangeSecondary
import com.example.ui.theme.PinkPrimary
import com.example.ui.theme.AppSegmentedTabs
import com.example.ui.theme.LightOnSurface
import com.example.ui.theme.SoftScreenBackground
import com.example.ui.theme.appAudioAccentContainer
import com.example.ui.theme.appAudioAccentColor
import com.example.ui.theme.appCaptionText
import com.example.ui.components.AppActionDialog
import com.example.ui.components.AppDialogVariant
import com.example.ui.theme.appErrorColor
import com.example.ui.theme.appErrorContainer
import com.example.ui.theme.appInsetSurface
import com.example.ui.theme.appInsetSurfaceBorder
import com.example.ui.theme.appMutedText
import com.example.ui.theme.appOutlinedFieldColors
import com.example.ui.theme.appSecondaryText
import com.example.ui.theme.appStarColor
import com.example.ui.theme.appSubtleFill
import com.example.ui.theme.appSuccessColor
import com.example.ui.theme.AppBottomNavClearance
import com.example.ui.theme.isCompactWidth
import com.example.ui.theme.appSuccessContainer
import com.example.ui.theme.appVideoAccentContainer
import com.example.ui.theme.appVideoAccentColor
import com.example.ui.theme.isAppDarkTheme

data class TokenPlan(
    val title: String,
    val tokens: Int,
    val price: String,
    val bonus: String = "",
    val isVideo: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    viewModel: com.example.MainViewModel? = null,
    onViewMoreTransactions: (() -> Unit)? = null,
    onViewPackages: ((String) -> Unit)? = null,
    onLogout: (() -> Unit)? = null
) {
    var fullName by rememberSaveable { mutableStateOf("John Doe") }
    var username by rememberSaveable { mutableStateOf("john_doe") }
    var email by remember { mutableStateOf("john.doe@example.com") }
    var phone by rememberSaveable { mutableStateOf("+1 (555) 019-2834") }
    var bio by rememberSaveable { mutableStateOf("Hey there! I am using the app to explore custom matches.") }
    var language by rememberSaveable { mutableStateOf("English") }
    var profilePhotoUri by remember { mutableStateOf<Uri?>(null) }
    
    var isSettingsShowing by remember { mutableStateOf(false) }

    var showCurrentPlanDetails by remember { mutableStateOf(false) }
    var showPurchaseConfirmDialog by remember { mutableStateOf<TokenPlan?>(null) }
    var showTxDetailsDialog by remember { mutableStateOf<com.example.WalletTransaction?>(null) }
    var showTopUpOptionsDialog by remember { mutableStateOf(false) }
    var selectedPlanFilter by remember { mutableStateOf("All") } // "All", "Audio", "Video"

    if (isSettingsShowing) {
        SettingsScreen(viewModel = viewModel, onBack = { isSettingsShowing = false }, onLogout = onLogout)
        return
    }

    var tempBio by remember { mutableStateOf(bio) }
    var tempLanguage by remember { mutableStateOf(language) }
    var tempFullName by remember { mutableStateOf(fullName) }
    var tempUsername by remember { mutableStateOf(username) }
    var fullNameError by remember { mutableStateOf<String?>(null) }
    var usernameError by remember { mutableStateOf<String?>(null) }

    val currentUserId by viewModel?.currentUserId?.collectAsStateWithLifecycle() ?: remember { mutableStateOf<String?>(null) }
    val userNames by viewModel?.userNames?.collectAsStateWithLifecycle() ?: remember { mutableStateOf(emptyMap()) }
    val userUsernames by viewModel?.userUsernames?.collectAsStateWithLifecycle() ?: remember { mutableStateOf(emptyMap()) }

    LaunchedEffect(currentUserId, userNames, userUsernames) {
        currentUserId?.let { id ->
            userNames.entries.firstOrNull { (key, _) ->
                key == id || key.removeSuffix("@dummy.phone") == id.removeSuffix("@dummy.phone")
            }?.value?.let {
                fullName = it
                tempFullName = it
            }
            userUsernames.entries.firstOrNull { (key, _) ->
                key == id || key.removeSuffix("@dummy.phone") == id.removeSuffix("@dummy.phone")
            }?.value?.let {
                username = it
                tempUsername = it
            }
        }
    }

    var languageExpanded by remember { mutableStateOf(false) }
    val languages = listOf(
        "English",
        "Hindi",
        "Bengali",
        "Telugu",
        "Marathi",
        "Tamil",
        "Gujarati",
        "Kannada",
        "Malayalam",
        "Punjabi",
        "Odia",
        "Assamese",
        "Urdu"
    )

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> uri?.let { profilePhotoUri = it } }

    val bg = MaterialTheme.colorScheme.background
    val cardBg = MaterialTheme.colorScheme.surface
    val borderColor = MaterialTheme.colorScheme.outline
    val textColor = MaterialTheme.colorScheme.onSurface
    val secondaryTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    
    val pinkGradient = Brush.horizontalGradient(listOf(PinkPrimary, OrangeSecondary))
    
    val textFieldColors = appOutlinedFieldColors()
    val fallbackWallet = remember { mutableStateOf(Wallet()) }
    val wallet by (viewModel?.walletState?.collectAsStateWithLifecycle() ?: fallbackWallet)

    SoftScreenBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = AppBottomNavClearance),
        ) {
            // --- NEW PROFILE CARD (BASED ON SCREENSHOT) ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, bottom = 10.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            // The Gradient Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 55.dp) // Offset for half avatar
                    .clip(RoundedCornerShape(24.dp))
                    .background(cardBg)
                    .border(1.dp, borderColor, RoundedCornerShape(24.dp))
                    .padding(bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 16.dp, top = 16.dp),
                    contentAlignment = Alignment.TopEnd
                ) {
                    IconButton(
                        onClick = { isSettingsShowing = true },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = borderColor.copy(alpha = 0.35f)
                        )
                    ) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = PinkPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                
                Text(
                    text = fullName,
                    color = textColor,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "@$username",
                    color = appMutedText(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                    color = borderColor,
                    thickness = 1.dp
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ProfileStatItemV2("TOTAL CALLS", "0")
                    ProfileStatItemV2("TOKENS", wallet.formattedBalanceFull())
                }
            }
            
            // Avatar (Overlapping)
            Box(modifier = Modifier.align(Alignment.TopCenter)) {
                ProfilePhotoAvatar(
                    photoUri = profilePhotoUri,
                    size = 110.dp,
                    borderColor = Color.White,
                    borderWidth = 3.dp,
                    onClick = { imagePicker.launch("image/*") }
                )
            }
        }
        
        var selectedTab by remember { mutableIntStateOf(0) }
        
        Column(modifier = Modifier.padding(horizontal = 16.dp).padding(top = 10.dp)) {
            AppSegmentedTabs(
                tabs = listOf("Wallet", "Personal Info", "Favorites"),
                selectedIndex = selectedTab,
                onTabSelected = { selectedTab = it }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (selectedTab == 0) {
                // Wallet Section
                val context = LocalContext.current
                val compact = isCompactWidth()
                val transactionsList = viewModel?.transactions?.collectAsState()?.value ?: emptyList()

                // Available Rupees Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(cardBg)
                        .border(1.dp, borderColor, RoundedCornerShape(20.dp))
                        .padding(20.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                                Text("Total Rupee Balance", color = appMutedText(), fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CurrencyRupee, contentDescription = "Rupees", tint = PinkPrimary, modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        "${wallet.formattedBalanceFull()} Rupees",
                                        color = textColor,
                                        fontSize = if (compact) 20.sp else 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                            
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(pinkGradient)
                                    .clickable {
                                        showTopUpOptionsDialog = true
                                    }
                                    .padding(horizontal = if (compact) 10.dp else 16.dp, vertical = if (compact) 8.dp else 10.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White, modifier = Modifier.size(16.dp))
                                    if (!compact) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Add Rupees", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = borderColor)
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Audio Rupees Column
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(appSubtleFill())
                                    .clickable {
                                        onViewPackages?.invoke("Audio")
                                    }
                                    .padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.Phone, contentDescription = "Audio Calls", tint = PinkPrimary, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Audio Rupees", color = appMutedText(), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("${wallet.audioBalance}", color = textColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }
                            
                            Spacer(modifier = Modifier.width(16.dp))
                            
                            // Video Rupees Column
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(appSubtleFill())
                                    .clickable {
                                        onViewPackages?.invoke("Video")
                                    }
                                    .padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.Videocam, contentDescription = "Video Calls", tint = OrangeSecondary, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Video Rupees", color = appMutedText(), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("${wallet.videoBalance}", color = textColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Current Plan Header
                Text(
                    text = "Current Plan",
                    color = textColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                // Clickable Current Plan Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(cardBg)
                        .border(1.dp, borderColor, RoundedCornerShape(20.dp))
                        .clickable { showCurrentPlanDetails = true }
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(appSuccessColor())
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Gold Subscription", color = PinkPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }
                            Text("Click for Info", color = appMutedText(), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Active • ₹49.99/mo (1,250 rupees per cycle included)", color = secondaryTextColor, fontSize = 14.sp)
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                // Section of Transactions at the Bottom
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Transactions (History & Details)",
                        color = textColor,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "View More",
                        color = PinkPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            onViewMoreTransactions?.invoke()
                        }
                    )
                }

                val displayedTransactions = transactionsList.take(5)
                if (displayedTransactions.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No transactions found.", color = appMutedText())
                    }
                } else {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        displayedTransactions.forEach { tx ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(cardBg)
                                    .border(1.dp, borderColor, RoundedCornerShape(18.dp))
                                    .clickable { showTxDetailsDialog = tx }
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(if (tx.isPositive) appSuccessContainer() else appErrorContainer()),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CurrencyRupee,
                                            contentDescription = "Rupee Tx",
                                            tint = if (tx.isPositive) appSuccessColor() else appErrorColor(),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            tx.title,
                                            color = textColor,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(tx.date, color = appMutedText(), fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = tx.amount,
                                    color = if (tx.isPositive) appSuccessColor() else appErrorColor(),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                // --- Dialogs ---

                // 1. Current Plan Details Dialog
                if (showCurrentPlanDetails) {
                    AlertDialog(
                        onDismissRequest = { showCurrentPlanDetails = false },
                        title = {
                            Text("Active Plan Membership", color = textColor, fontWeight = FontWeight.Bold)
                        },
                        text = {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Plan Name:", color = appSecondaryText(), fontSize = 14.sp)
                                    Text("Gold Subscription", color = PinkPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Rupees Included:", color = appSecondaryText(), fontSize = 14.sp)
                                    Text("1,250 Rupees / Cycle", color = textColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Cost & Billing:", color = appSecondaryText(), fontSize = 14.sp)
                                    Text("₹49.99 / Billed Monthly", color = textColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Purchase Date:", color = appSecondaryText(), fontSize = 14.sp)
                                    Text("10 Jun 2026, 09:30 AM", color = textColor, fontSize = 14.sp)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Renewal Date:", color = appSecondaryText(), fontSize = 14.sp)
                                    Text("10 Jul 2026", color = textColor, fontSize = 14.sp)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Payment Method:", color = appSecondaryText(), fontSize = 14.sp)
                                    Text("Visa ending in 8492", color = textColor, fontSize = 14.sp)
                                }
                                HorizontalDivider(color = borderColor)
                                Text(
                                    text = "Benefits Included:\n• Unlocked VIP badge next to your profile picture\n• Priority video and audio streaming routing\n• Double daily bonus login rewards",
                                    color = appSecondaryText(),
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = { showCurrentPlanDetails = false }) {
                                Text("Close", color = PinkPrimary)
                            }
                        },
                        containerColor = cardBg
                    )
                }

                // 2. Buy Confirmation Dialog
                showPurchaseConfirmDialog?.let { plan ->
                    AlertDialog(
                        onDismissRequest = { showPurchaseConfirmDialog = null },
                        title = {
                            Text("Confirm Rupee Purchase", color = textColor, fontWeight = FontWeight.Bold)
                        },
                        text = {
                            Text(
                                text = "Would you like to buy '${plan.title}' for ${plan.price}? This will immediately credit ${plan.tokens} ${if (plan.isVideo) "Video" else "Audio"} Rupees to your available balance.",
                                color = appSecondaryText(),
                                fontSize = 14.sp
                            )
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    viewModel?.rechargeTokens(plan.tokens, plan.isVideo)
                                    Toast.makeText(context, "Successfully purchased ${plan.tokens} ${if (plan.isVideo) "Video" else "Audio"} Rupees!", Toast.LENGTH_LONG).show()
                                    showPurchaseConfirmDialog = null
                                }
                            ) {
                                Text("Purchase", color = appSuccessColor(), fontWeight = FontWeight.Bold)
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showPurchaseConfirmDialog = null }) {
                                Text("Cancel", color = appSecondaryText())
                            }
                        },
                        containerColor = cardBg
                    )
                }

                // 2.1 Top Up Options Dialog
                if (showTopUpOptionsDialog) {
                    AlertDialog(
                        onDismissRequest = { showTopUpOptionsDialog = false },
                        title = {
                            Text("Top Up Your Rupees", color = textColor, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        },
                        text = {
                            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                Text("Select the rupee type to package and recharge. Audio and Video call balances are split to provide optimized billing rates.", color = appSecondaryText(), fontSize = 13.sp)
                                
                                // Option A: Audio Rupees Top Up
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(borderColor)
                                        .border(1.dp, PinkPrimary.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                                        .clickable {
                                            showTopUpOptionsDialog = false
                                            onViewPackages?.invoke("Audio")
                                        }
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(appSuccessContainer()),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Phone,
                                            contentDescription = "Audio Calls",
                                            tint = appSuccessColor(),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text("Top Up Audio Rupees", color = textColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text("Recharge credits optimized for voice calls", color = appCaptionText(), fontSize = 11.sp)
                                    }
                                }

                                // Option B: Video Rupees Top Up
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(borderColor)
                                        .border(1.dp, OrangeSecondary.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                                        .clickable {
                                            showTopUpOptionsDialog = false
                                            onViewPackages?.invoke("Video")
                                        }
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(appErrorContainer()),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Videocam,
                                            contentDescription = "Video Calls",
                                            tint = appErrorColor(),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text("Top Up Video Rupees", color = textColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text("Recharge credits optimized for video streams", color = appCaptionText(), fontSize = 11.sp)
                                    }
                                }
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = { showTopUpOptionsDialog = false }) {
                                Text("Close", color = PinkPrimary)
                            }
                        },
                        containerColor = cardBg
                    )
                }

                // 3. Transaction Details Dialog
                showTxDetailsDialog?.let { tx ->
                    AlertDialog(
                        onDismissRequest = { showTxDetailsDialog = null },
                        title = {
                            Text("Transaction Details", color = textColor, fontWeight = FontWeight.Bold)
                        },
                        text = {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Transaction Title:", color = appSecondaryText(), fontSize = 14.sp)
                                    Text(tx.title, color = textColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Status:", color = appSecondaryText(), fontSize = 14.sp)
                                    Text("Completed Successfully", color = appSuccessColor(), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Date & Time:", color = appSecondaryText(), fontSize = 14.sp)
                                    Text(tx.date, color = textColor, fontSize = 14.sp)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Impact Amount:", color = appSecondaryText(), fontSize = 14.sp)
                                    Text(
                                        text = tx.amount,
                                        color = if (tx.isPositive) appSuccessColor() else appErrorColor(),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Ref ID:", color = appSecondaryText(), fontSize = 14.sp)
                                    Text(tx.id, color = appMutedText(), fontSize = 12.sp)
                                }
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = { showTxDetailsDialog = null }) {
                                Text("Dismiss", color = PinkPrimary)
                            }
                        },
                        containerColor = cardBg
                    )
                }
                
            } else if (selectedTab == 1) {
                // Personal Info Section Form
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(cardBg, RoundedCornerShape(20.dp))
                        .border(1.dp, borderColor, RoundedCornerShape(20.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Profile Photo",
                        color = textColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ProfilePhotoAvatar(
                            photoUri = profilePhotoUri,
                            size = 84.dp,
                            borderColor = borderColor,
                            borderWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { imagePicker.launch("image/*") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = PinkPrimary),
                                border = androidx.compose.foundation.BorderStroke(1.dp, PinkPrimary.copy(alpha = 0.5f))
                            ) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Change Photo", fontWeight = FontWeight.SemiBold)
                            }
                            OutlinedButton(
                                onClick = { profilePhotoUri = null },
                                enabled = profilePhotoUri != null,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = appMutedText(),
                                    disabledContentColor = appMutedText().copy(alpha = 0.4f)
                                ),
                                border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Remove Photo", fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        value = tempFullName,
                        onValueChange = {
                            tempFullName = it
                            if (it.isNotBlank()) fullNameError = null
                        },
                        label = { Text("Full Name", color = appCaptionText()) },
                        leadingIcon = { Icon(Icons.Default.Badge, contentDescription = "Full Name", tint = PinkPrimary) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = textFieldColors,
                        shape = RoundedCornerShape(12.dp),
                        isError = fullNameError != null,
                        supportingText = fullNameError?.let { error ->
                            { Text(error, color = MaterialTheme.colorScheme.error) }
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = tempUsername,
                        onValueChange = {
                            tempUsername = it.lowercase().replace(Regex("\\s+"), "_")
                            if (it.isNotBlank()) usernameError = null
                        },
                        label = { Text("Username", color = appCaptionText()) },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Username", tint = PinkPrimary) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = textFieldColors,
                        shape = RoundedCornerShape(12.dp),
                        isError = usernameError != null,
                        supportingText = {
                            usernameError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                                ?: Text("Shown on calls and public profile", color = appCaptionText(), fontSize = 12.sp)
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Phone Number Field (read-only)
                    OutlinedTextField(
                        value = phone,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Phone Number", color = appCaptionText()) },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = "Phone", tint = PinkPrimary) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = cardBg,
                            unfocusedContainerColor = cardBg,
                            disabledContainerColor = cardBg,
                            focusedBorderColor = borderColor,
                            unfocusedBorderColor = borderColor,
                            focusedTextColor = appMutedText(),
                            unfocusedTextColor = appMutedText(),
                            disabledTextColor = appMutedText()
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ExposedDropdownMenuBox(
                        expanded = languageExpanded,
                        onExpandedChange = { languageExpanded = it },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = tempLanguage,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Language", color = appCaptionText()) },
                            leadingIcon = { Icon(Icons.Default.Language, contentDescription = "Language", tint = PinkPrimary) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = languageExpanded) },
                            colors = textFieldColors,
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = languageExpanded,
                            onDismissRequest = { languageExpanded = false }
                        ) {
                            languages.forEach { selectionOption ->
                                DropdownMenuItem(
                                    text = { Text(selectionOption) },
                                    onClick = {
                                        tempLanguage = selectionOption
                                        languageExpanded = false
                                    }
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    OutlinedTextField(
                        value = tempBio,
                        onValueChange = { tempBio = it },
                        label = { Text("Bio", color = appCaptionText()) },
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        colors = textFieldColors,
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 4
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    val context = LocalContext.current
                    Button(
                        onClick = {
                            var isValid = true
                            if (tempFullName.isBlank()) {
                                fullNameError = "Required"
                                isValid = false
                            } else {
                                fullNameError = null
                            }
                            if (tempUsername.isBlank()) {
                                usernameError = "Required"
                                isValid = false
                            } else {
                                usernameError = null
                            }
                            if (!isValid) return@Button

                            fullName = tempFullName.trim()
                            username = tempUsername.trim()
                            bio = tempBio
                            language = tempLanguage
                            viewModel?.updateUserProfileIdentity(fullName, username)
                            Toast.makeText(context, "Personal Info Saved Successfully!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(0.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(pinkGradient),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Save Changes", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else if (selectedTab == 2) {
                // Favorites Section
                val favorites = viewModel?.favorites?.collectAsState()?.value ?: emptySet()
                if (favorites.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        Text("No saved favorites yet.", color = appMutedText(), fontSize = 16.sp)
                    }
                } else {
                    val models = viewModel?.models?.collectAsState()?.value ?: emptyList()
                    val favoriteModels = models.filter { favorites.contains(it.id) }
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        favoriteModels.forEach { model ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(cardBg, RoundedCornerShape(16.dp))
                                    .border(1.dp, borderColor, RoundedCornerShape(16.dp))
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = "https://i.pravatar.cc/150?u=${model.id}",
                                    contentDescription = "Profile",
                                    modifier = Modifier.size(60.dp).clip(CircleShape).border(2.dp, PinkPrimary, CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(model.publicUsername(), color = textColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Star, contentDescription = "Rating", tint = appStarColor(), modifier = Modifier.size(13.dp))
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(String.format("%.1f", model.rating), color = textColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        Text(" (${model.reviewsCount})", color = appSecondaryText(), fontSize = 12.sp)
                                    }
                                }
                                IconButton(onClick = { viewModel?.toggleFavorite(model.id) }) {
                                    Icon(Icons.Default.Favorite, contentDescription = "Remove Favorite", tint = PinkPrimary)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    }
}

@Composable
private fun ProfilePhotoAvatar(
    photoUri: Uri?,
    size: androidx.compose.ui.unit.Dp,
    borderColor: Color,
    borderWidth: androidx.compose.ui.unit.Dp = 2.dp,
    onClick: (() -> Unit)? = null
) {
    val shape = CircleShape
    Box(
        modifier = Modifier
            .size(size)
            .clip(shape)
            .border(borderWidth, borderColor, shape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        if (photoUri != null) {
            AsyncImage(
                model = photoUri,
                contentDescription = "User Profile",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                Icons.Default.Person,
                contentDescription = "User Profile",
                tint = appMutedText(),
                modifier = Modifier.size(size * 0.42f)
            )
        }
    }
}

@Composable
fun ProfileStatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
    }
}

@Composable
fun ProfileStatItemV2(label: String, value: String, lightOnGradient: Boolean = false) {
    val labelColor = if (lightOnGradient) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    val valueColor = if (lightOnGradient) Color.White else MaterialTheme.colorScheme.onSurface
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = labelColor, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(modifier = Modifier.height(6.dp))
        Text(value, color = valueColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ProfileDetailItem(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, value: String, onClick: () -> Unit) {
    val textColor = MaterialTheme.colorScheme.onSurface
    val cardVariant = MaterialTheme.colorScheme.surfaceVariant
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f, fill = false),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(cardVariant, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = title, tint = textColor, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                title,
                color = textColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                value,
                color = textColor.copy(alpha = 0.6f),
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.Default.ChevronRight, contentDescription = "Arrow", tint = textColor.copy(alpha = 0.6f))
        }
    }
}

@Composable
fun SettingsScreen(
    viewModel: com.example.MainViewModel? = null,
    onBack: () -> Unit,
    onLogout: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val bg = MaterialTheme.colorScheme.background
    val cardBg = MaterialTheme.colorScheme.surface
    val borderColor = MaterialTheme.colorScheme.outline
    val textColor = MaterialTheme.colorScheme.onSurface
    
    val pinkGradient = Brush.horizontalGradient(listOf(PinkPrimary, OrangeSecondary))

    val textFieldColors = appOutlinedFieldColors()

    val isDarkMode by (viewModel?.isDarkMode?.collectAsState() ?: remember { mutableStateOf(true) })

    val myPromoCode by (viewModel?.myPromoCode?.collectAsState() ?: remember { mutableStateOf("VIP-AJAY-999") })
    val referredUsers by (viewModel?.referredUsers?.collectAsState() ?: remember { mutableStateOf(emptyList()) })
    val hasRedeemed by (viewModel?.hasRedeemedPromoCode?.collectAsState() ?: remember { mutableStateOf(false) })

    var pushNotifications by remember { mutableStateOf(true) }
    var soundEnabled by remember { mutableStateOf(true) }
    var vibrateEnabled by remember { mutableStateOf(true) }
    var callRingEnabled by remember { mutableStateOf(true) }

    var subScreen by remember { mutableStateOf(SettingsSubScreen.Main) }

    var showLogOutConfirm by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    var redeemCodeFieldSetting by remember { mutableStateOf("") }

    when (subScreen) {
        SettingsSubScreen.BlockedUsers -> {
            BlockedUsersScreen(viewModel = viewModel, onBack = { subScreen = SettingsSubScreen.Main })
            return
        }
        SettingsSubScreen.Faq -> {
            FaqScreen(onBack = { subScreen = SettingsSubScreen.Main })
            return
        }
        SettingsSubScreen.Terms -> {
            TermsOfServiceScreen(onBack = { subScreen = SettingsSubScreen.Main })
            return
        }
        SettingsSubScreen.ContactSupport -> {
            ContactSupportScreen(onBack = { subScreen = SettingsSubScreen.Main })
            return
        }
        SettingsSubScreen.ReportProblem -> {
            ReportProblemScreen(onBack = { subScreen = SettingsSubScreen.Main })
            return
        }
        SettingsSubScreen.Main -> Unit
    }

    SoftScreenBackground {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = textColor)
            }
            Text(
                "Settings",
                color = textColor,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        // Appearance
        SettingsSection(title = "Appearance", icon = Icons.Default.Palette) {
            SettingsSwitchItem(
                title = "Dark Mode",
                description = "Toggle dark/light theme",
                checked = isDarkMode,
                onCheckedChange = {
                    viewModel?.toggleDarkMode()
                    Toast.makeText(context, if (!isDarkMode) "Dark mode enabled" else "Light mode enabled", Toast.LENGTH_SHORT).show()
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Notifications
        SettingsSection(title = "Notifications", icon = Icons.Default.Notifications) {
            SettingsSwitchItem(
                title = "Push Notifications",
                description = "Receive app notifications",
                checked = pushNotifications,
                onCheckedChange = {
                    pushNotifications = it
                    Toast.makeText(context, if (it) "Push notifications enabled" else "Push notifications disabled", Toast.LENGTH_SHORT).show()
                }
            )
            SettingsSwitchItem(
                title = "Sound",
                description = "Play notification sounds",
                checked = soundEnabled,
                onCheckedChange = {
                    soundEnabled = it
                    Toast.makeText(context, if (it) "Notification sounds on" else "Notification sounds off", Toast.LENGTH_SHORT).show()
                }
            )
            SettingsSwitchItem(
                title = "Vibrate",
                description = "Vibrate on notifications",
                checked = vibrateEnabled,
                onCheckedChange = {
                    vibrateEnabled = it
                    Toast.makeText(context, if (it) "Vibration enabled" else "Vibration disabled", Toast.LENGTH_SHORT).show()
                }
            )
            SettingsSwitchItem(
                title = "Call Ring",
                description = "Ring Tone for incoming calls",
                checked = callRingEnabled,
                onCheckedChange = {
                    callRingEnabled = it
                    Toast.makeText(context, if (it) "Call ringtone enabled" else "Call ringtone disabled", Toast.LENGTH_SHORT).show()
                }
            )
        }
        

        Spacer(modifier = Modifier.height(24.dp))

        SettingsSection(title = "Privacy & Support", icon = Icons.Default.Security) {
            SettingsActionItem(icon = Icons.Default.Block, title = "Blocked Accounts", onClick = { subScreen = SettingsSubScreen.BlockedUsers })
            SettingsActionItem(icon = Icons.Default.ReportProblem, title = "Report A Problem", onClick = { subScreen = SettingsSubScreen.ReportProblem })
            HorizontalDivider(color = borderColor, modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
            SettingsActionItem(icon = Icons.Default.QuestionAnswer, title = "FAQ", onClick = { subScreen = SettingsSubScreen.Faq })
            SettingsActionItem(icon = Icons.Default.SupportAgent, title = "Contact Support", onClick = { subScreen = SettingsSubScreen.ContactSupport })
            SettingsActionItem(icon = Icons.Default.Description, title = "Terms of Services", onClick = { subScreen = SettingsSubScreen.Terms })
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Referral Program (Simplified and placed at the bottom for normal users)
        SettingsSection(title = "Invite & Earn 🎁", icon = Icons.Default.CurrencyRupee) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Share the joy! Invite friends to join and get free rewards. For each friend who uses your invite code, both of you will receive 100 free bonus rupees!",
                    color = appSecondaryText(),
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Big Clear Invite Code Box
                Text(
                    text = "YOUR REWARDS CODE:",
                    color = appCaptionText(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                
                Spacer(modifier = Modifier.height(6.dp))
                
                val clipboardManager = androidx.compose.ui.platform.LocalClipboardManager.current
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(appInsetSurface(), RoundedCornerShape(12.dp))
                        .border(1.dp, PinkPrimary, RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = myPromoCode,
                        color = textColor,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.testTag("referral_settings_code_text")
                    )
                    Button(
                        onClick = {
                            clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(myPromoCode))
                            Toast.makeText(context, "Invite code copied! Share it with your friends.", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .height(32.dp)
                            .testTag("referral_settings_copy_button")
                    ) {
                        Text("Copy Code", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Simple Grid/Row for current stats
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Successful count card
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(appInsetSurface(), RoundedCornerShape(12.dp))
                            .border(1.dp, appInsetSurfaceBorder(), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${referredUsers.size}",
                            color = PinkPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.testTag("referral_settings_count_text")
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "My Referrals",
                            color = appCaptionText(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    // Total Earnings card
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(appInsetSurface(), RoundedCornerShape(12.dp))
                            .border(1.dp, appInsetSurfaceBorder(), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${referredUsers.size * 100} Rupees",
                            color = appStarColor(),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.testTag("referral_settings_bonus_tokens_text")
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Reward Rupees",
                            color = appCaptionText(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(color = borderColor)
                Spacer(modifier = Modifier.height(16.dp))
                
                // Redeem a friend's code section
                Text(
                    text = "REDEEM AN INVITE CODE:",
                    color = appCaptionText(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                
                if (hasRedeemed) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(appSuccessContainer())
                            .padding(10.dp)
                    ) {
                        Text("✓ You have successfully redeemed a code and earned +100 rupees!", color = appSuccessColor(), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = redeemCodeFieldSetting,
                            onValueChange = { redeemCodeFieldSetting = it },
                            placeholder = { Text("e.g. VIP-FRIEND-123", fontSize = 12.sp, color = appCaptionText()) },
                            singleLine = true,
                            colors = textFieldColors,
                            modifier = Modifier
                                .weight(1.3f)
                                .height(46.dp)
                                .testTag("redeem_settings_code_input")
                        )
                        Button(
                            onClick = {
                                if (redeemCodeFieldSetting.isBlank()) {
                                    Toast.makeText(context, "Please enter a code to redeem.", Toast.LENGTH_SHORT).show()
                                } else if (viewModel?.redeemPromoCode(redeemCodeFieldSetting) == true) {
                                    Toast.makeText(context, "Successful! Received +100 bonus rupees.", Toast.LENGTH_LONG).show()
                                    redeemCodeFieldSetting = ""
                                } else {
                                    Toast.makeText(context, "Cannot redeem code. Make sure it is not your own code.", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("redeem_settings_code_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            contentPadding = PaddingValues(0.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(pinkGradient),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Redeem", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Log Out & Delete Account
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(cardBg, RoundedCornerShape(20.dp))
                .border(1.dp, borderColor, RoundedCornerShape(20.dp))
                .padding(vertical = 8.dp)
        ) {
            SettingsActionItem(icon = Icons.Default.Logout, title = "Log Out", textColor = Color.Red, iconColor = Color.Red, showChevron = false, onClick = { showLogOutConfirm = true })
            HorizontalDivider(color = borderColor)
            SettingsActionItem(icon = Icons.Default.Delete, title = "Delete Account", showChevron = false, onClick = { showDeleteConfirm = true })
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }

    if (showLogOutConfirm) {
        ConfirmDialog(
            title = "Log Out",
            text = "Are you sure you want to log out?",
            confirmText = "Log Out",
            onConfirm = {
                showLogOutConfirm = false
                viewModel?.resetData()
                onLogout?.invoke()
            },
            onDismiss = { showLogOutConfirm = false }
        )
    }
    if (showDeleteConfirm) {
        ConfirmDialog(
            title = "Delete Account",
            text = "Are you sure you want to permanently delete your account? This will clear all data.",
            confirmText = "Delete",
            onConfirm = {
                showDeleteConfirm = false
                viewModel?.resetData()
                onLogout?.invoke()
                Toast.makeText(context, "Account deleted successfully.", Toast.LENGTH_LONG).show()
            },
            onDismiss = { showDeleteConfirm = false }
        )
    }
    }
}

@Composable
fun SettingsSection(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, content: @Composable ColumnScope.() -> Unit) {
    val cardBg = MaterialTheme.colorScheme.surface
    val borderColor = MaterialTheme.colorScheme.outline
    val textColor = MaterialTheme.colorScheme.onSurface

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(cardBg, RoundedCornerShape(20.dp))
            .border(1.dp, borderColor, RoundedCornerShape(20.dp))
            .padding(vertical = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 12.dp)
        ) {
            Icon(icon, contentDescription = title, tint = textColor, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(title, color = textColor, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        content()
    }
}

@Composable
fun SettingsSwitchItem(title: String, description: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    val textColor = MaterialTheme.colorScheme.onSurface
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = textColor, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Text(description, color = textColor.copy(alpha = 0.6f), fontSize = 12.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = PinkPrimary,
                uncheckedThumbColor = textColor.copy(alpha = 0.4f),
                uncheckedTrackColor = textColor.copy(alpha = 0.1f)
            )
        )
    }
}

@Composable
fun SettingsActionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector, 
    title: String, 
    textColor: Color = Color.Unspecified,
    iconColor: Color = Color.Unspecified,
    showChevron: Boolean = true,
    onClick: () -> Unit
) {
    val defaultTextColor = MaterialTheme.colorScheme.onSurface
    val actualTextColor = if (textColor == Color.Unspecified) defaultTextColor else textColor
    val actualIconColor = if (iconColor == Color.Unspecified) defaultTextColor else iconColor
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = title, tint = actualIconColor, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(title, color = actualTextColor, fontSize = 16.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
        if (showChevron) {
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = actualTextColor.copy(alpha = 0.4f), modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun SimpleTextDialog(title: String, text: String, onDismiss: () -> Unit) {
    val textColor = MaterialTheme.colorScheme.onSurface
    val secondaryText = MaterialTheme.colorScheme.onSurfaceVariant
    val cardBg = MaterialTheme.colorScheme.surface
    val borderColor = MaterialTheme.colorScheme.outline

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        containerColor = cardBg,
        title = {
            Text(title, color = textColor, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 320.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(text, color = secondaryText, fontSize = 14.sp, lineHeight = 22.sp)
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = PinkPrimary, fontWeight = FontWeight.Bold)
            }
        },
        tonalElevation = 6.dp
    )
}

@Composable
fun ConfirmDialog(
    title: String,
    text: String,
    confirmText: String = "Confirm",
    variant: AppDialogVariant? = null,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val resolvedVariant = variant ?: when {
        title.equals("Log Out", ignoreCase = true) -> AppDialogVariant.Logout
        title.contains("Delete", ignoreCase = true) -> AppDialogVariant.DeleteAccount
        confirmText.equals("Block", ignoreCase = true) -> AppDialogVariant.Block
        else -> AppDialogVariant.Default
    }

    AppActionDialog(
        title = title,
        message = text,
        confirmText = confirmText,
        variant = resolvedVariant,
        onConfirm = onConfirm,
        onDismiss = onDismiss
    )
}

@Composable
fun InputDialog(title: String, label: String, onDismiss: () -> Unit) {
    var text by remember { mutableStateOf("") }
    val context = LocalContext.current
    val textColor = MaterialTheme.colorScheme.onSurface
    val secondaryText = MaterialTheme.colorScheme.onSurfaceVariant
    val cardBg = MaterialTheme.colorScheme.surface
    val borderColor = MaterialTheme.colorScheme.outline

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        containerColor = cardBg,
        title = {
            Text(title, color = textColor, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(label, color = secondaryText) },
                placeholder = { Text("Type here…", color = secondaryText.copy(alpha = 0.7f)) },
                minLines = 3,
                maxLines = 6,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PinkPrimary,
                    unfocusedBorderColor = borderColor,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedTextColor = textColor,
                    unfocusedTextColor = textColor,
                    cursorColor = PinkPrimary
                )
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (text.isBlank()) {
                        Toast.makeText(context, "Please enter some text before submitting.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Submitted successfully. We'll get back to you soon.", Toast.LENGTH_SHORT).show()
                        onDismiss()
                    }
                }
            ) {
                Text("Submit", color = PinkPrimary, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = secondaryText, fontWeight = FontWeight.Medium)
            }
        },
        tonalElevation = 6.dp
    )
}