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
import androidx.compose.material.icons.filled.MonetizationOn
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
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.Badge
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.OrangeSecondary
import com.example.ui.theme.PinkPrimary
import com.example.ui.theme.AppSegmentedTabs
import com.example.ui.theme.DarkPromoGradients
import com.example.ui.theme.appMutedText
import com.example.ui.theme.appSubtleFill
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
    var username by rememberSaveable { mutableStateOf("john_doe_99") }
    var fullName by rememberSaveable { mutableStateOf("John Doe") }
    var email by remember { mutableStateOf("john.doe@example.com") }
    var phone by rememberSaveable { mutableStateOf("+1 (555) 019-2834") }
    var location by rememberSaveable { mutableStateOf("New York, USA") }
    val uniqueId = remember { "UID-${(100000..999999).random()}" }
    var age by rememberSaveable { mutableStateOf("28") }
    var gender by rememberSaveable { mutableStateOf("Male") }
    var bio by rememberSaveable { mutableStateOf("Hey there! I am using the app to explore custom matches.") }
    var language by rememberSaveable { mutableStateOf("English") }
    var profilePhotoUri by remember { mutableStateOf<Uri?>(null) }
    
    val availableInterests = listOf("Music", "Coding", "Reading", "Travel", "Fitness", "Gaming", "Photography", "Cooking", "Art", "Movies", "Sports", "Nature")
    var selectedInterests by remember { mutableStateOf(setOf("Music", "Coding", "Travel", "Fitness")) }
    
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

    var tempUsername by remember { mutableStateOf(username) }
    var tempFullName by remember { mutableStateOf(fullName) }
    var tempPhone by remember { mutableStateOf(phone) }
    var tempLocation by remember { mutableStateOf(location) }
    var tempAge by remember { mutableStateOf(age) }
    var tempGender by remember { mutableStateOf(gender) }
    var tempBio by remember { mutableStateOf(bio) }
    var tempLanguage by remember { mutableStateOf(language) }
    var tempSelectedInterests by remember { mutableStateOf(selectedInterests) }

    var usernameError by remember { mutableStateOf<String?>(null) }
    var fullNameError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var locationError by remember { mutableStateOf<String?>(null) }
    var ageError by remember { mutableStateOf<String?>(null) }
    
    var genderExpanded by remember { mutableStateOf(false) }
    val genders = listOf("Male", "Female", "Other", "Prefer not to say")
    
    var languageExpanded by remember { mutableStateOf(false) }
    val languages = listOf("English", "Spanish", "French", "German", "Hindi", "Japanese", "Chinese")

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> uri?.let { profilePhotoUri = it } }

    val bg = MaterialTheme.colorScheme.background
    val cardBg = MaterialTheme.colorScheme.surface
    val borderColor = MaterialTheme.colorScheme.outline
    val textColor = MaterialTheme.colorScheme.onSurface
    val secondaryTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    
    val pinkGradient = Brush.horizontalGradient(listOf(PinkPrimary, OrangeSecondary))
    
    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = cardBg,
        unfocusedContainerColor = cardBg,
        disabledContainerColor = cardBg,
        focusedBorderColor = PinkPrimary,
        unfocusedBorderColor = borderColor,
        focusedTextColor = textColor,
        unfocusedTextColor = textColor
    )

    Box(modifier = Modifier.fillMaxSize().background(bg)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
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
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "ID : ${uniqueId.filter { it.isDigit() }.ifEmpty { "53878700" }}",
                    color = secondaryTextColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(14.dp))
                
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(PinkPrimary.copy(alpha = 0.12f))
                        .border(1.dp, PinkPrimary.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 24.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Beginner",
                        color = PinkPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(text = "🥰", fontSize = 26.sp)
                
                Spacer(modifier = Modifier.height(28.dp))
                
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                    color = borderColor,
                    thickness = 1.dp
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                val walletState = viewModel?.walletState?.collectAsState()?.value
                val tokenBalance = walletState?.balance ?: 500
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ProfileStatItemV2("TOTAL CALLS", "0")
                    ProfileStatItemV2("TOKENS", "$tokenBalance")
                }
            }
            
            // Avatar (Overlapping)
            Box {
                AsyncImage(
                    model = profilePhotoUri ?: "https://i.pravatar.cc/300?u=user",
                    contentDescription = "User Profile",
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .border(3.dp, Color.White, CircleShape),
                    contentScale = ContentScale.Crop
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
                val walletStateLocal = viewModel?.walletState?.collectAsState()?.value
                val currentBalanceLocal = walletStateLocal?.balance ?: 1250
                val transactionsList = viewModel?.transactions?.collectAsState()?.value ?: emptyList()

                // Available Tokens Card
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
                            Column {
                                Text("Total Token Balance", color = appMutedText(), fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.MonetizationOn, contentDescription = "Tokens", tint = PinkPrimary, modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(String.format("%,d Tokens", currentBalanceLocal), color = textColor, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(pinkGradient)
                                    .clickable {
                                        showTopUpOptionsDialog = true
                                    }
                                    .padding(horizontal = 16.dp, vertical = 10.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Add Tokens", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
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
                            // Audio Tokens Column
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
                                Text("Audio Tokens", color = appMutedText(), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("${walletStateLocal?.audioBalance ?: 750}", color = textColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }
                            
                            Spacer(modifier = Modifier.width(16.dp))
                            
                            // Video Tokens Column
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
                                Text("Video Tokens", color = appMutedText(), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("${walletStateLocal?.videoBalance ?: 750}", color = textColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
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
                                        .background(Color.Green)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Gold Subscription", color = PinkPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }
                            Text("Click for Info", color = appMutedText(), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Active • $49.99/mo (1,250 tokens per cycle included)", color = secondaryTextColor, fontSize = 14.sp)
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Beautiful Promo Card to visit the separate Token Store
                val promoGradient = if (isAppDarkTheme()) {
                    Brush.horizontalGradient(listOf(Color(0xFF321A30), Color(0xFF1E1735)))
                } else {
                    Brush.horizontalGradient(DarkPromoGradients.welcome)
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(promoGradient)
                        .border(1.dp, PinkPrimary.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                        .clickable { onViewPackages?.invoke("All") }
                        .padding(20.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.MonetizationOn, 
                                contentDescription = "Token Store", 
                                tint = Color(0xFFFFB800), 
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Instant Token Packs Available!", 
                                color = Color.White, 
                                fontSize = 16.sp, 
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Tap here to browse all Audio & Video token packages. Get customized packages, discount minutes, and HD streaming credits fully detailed in our Store.",
                            color = Color.LightGray,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { onViewPackages?.invoke("All") },
                            modifier = Modifier.align(Alignment.End).height(40.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            contentPadding = PaddingValues(0.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(pinkGradient)
                                    .padding(horizontal = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Browse Packages 📦", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                
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
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(if (tx.isPositive) Color(0xFF1B3D2F) else Color(0xFF3D1B24)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.MonetizationOn,
                                            contentDescription = "Token Tx",
                                            tint = if (tx.isPositive) Color(0xFF3DDC84) else Color(0xFFFF4D4D),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(tx.title, color = textColor, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(tx.date, color = appMutedText(), fontSize = 11.sp)
                                    }
                                }
                                Text(
                                    text = tx.amount,
                                    color = if (tx.isPositive) Color(0xFF3DDC84) else Color(0xFFFF4D4D),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
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
                            Text("Active Plan Membership", color = Color.White, fontWeight = FontWeight.Bold)
                        },
                        text = {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Plan Name:", color = Color.Gray, fontSize = 14.sp)
                                    Text("Gold Subscription", color = PinkPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Tokens Included:", color = Color.Gray, fontSize = 14.sp)
                                    Text("1,250 Tokens / Cycle", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Cost & Billing:", color = Color.Gray, fontSize = 14.sp)
                                    Text("$49.99 / Billed Monthly", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Purchase Date:", color = Color.Gray, fontSize = 14.sp)
                                    Text("10 Jun 2026, 09:30 AM", color = Color.White, fontSize = 14.sp)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Renewal Date:", color = Color.Gray, fontSize = 14.sp)
                                    Text("10 Jul 2026", color = Color.White, fontSize = 14.sp)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Payment Method:", color = Color.Gray, fontSize = 14.sp)
                                    Text("Visa ending in 8492", color = Color.White, fontSize = 14.sp)
                                }
                                HorizontalDivider(color = borderColor)
                                Text(
                                    text = "Benefits Included:\n• Unlocked VIP badge next to your profile picture\n• Priority video and audio streaming routing\n• Double daily bonus login rewards",
                                    color = Color.LightGray,
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
                            Text("Confirm Token Purchase", color = Color.White, fontWeight = FontWeight.Bold)
                        },
                        text = {
                            Text(
                                text = "Would you like to buy '${plan.title}' for ${plan.price}? This will immediately credit ${plan.tokens} ${if (plan.isVideo) "Video" else "Audio"} Tokens to your available balance.",
                                color = Color.LightGray,
                                fontSize = 14.sp
                            )
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    viewModel?.rechargeTokens(plan.tokens, plan.isVideo)
                                    Toast.makeText(context, "Successfully purchased ${plan.tokens} ${if (plan.isVideo) "Video" else "Audio"} Tokens!", Toast.LENGTH_LONG).show()
                                    showPurchaseConfirmDialog = null
                                }
                            ) {
                                Text("Purchase", color = Color.Green, fontWeight = FontWeight.Bold)
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showPurchaseConfirmDialog = null }) {
                                Text("Cancel", color = Color.Gray)
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
                            Text("Top Up Your Tokens", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        },
                        text = {
                            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                Text("Select the token type to package and recharge. Audio and Video call tokens are split to provide optimized billing rates.", color = Color.LightGray, fontSize = 13.sp)
                                
                                // Option A: Audio Tokens Top Up
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
                                            .background(Color(0xFF1B3D2F)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Phone,
                                            contentDescription = "Audio Calls",
                                            tint = Color(0xFF3DDC84),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text("Top Up Audio Tokens", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text("Recharge credits optimized for voice calls", color = Color.Gray, fontSize = 11.sp)
                                    }
                                }

                                // Option B: Video Tokens Top Up
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
                                            .background(Color(0xFF3D1B24)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Videocam,
                                            contentDescription = "Video Calls",
                                            tint = Color(0xFFFF4D4D),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text("Top Up Video Tokens", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text("Recharge credits optimized for video streams", color = Color.Gray, fontSize = 11.sp)
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
                            Text("Transaction Details", color = Color.White, fontWeight = FontWeight.Bold)
                        },
                        text = {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Transaction Title:", color = Color.Gray, fontSize = 14.sp)
                                    Text(tx.title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Status:", color = Color.Gray, fontSize = 14.sp)
                                    Text("Completed Successfully", color = Color.Green, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Date & Time:", color = Color.Gray, fontSize = 14.sp)
                                    Text(tx.date, color = Color.White, fontSize = 14.sp)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Impact Amount:", color = Color.Gray, fontSize = 14.sp)
                                    Text(
                                        text = tx.amount,
                                        color = if (tx.isPositive) Color(0xFF3DDC84) else Color(0xFFFF4D4D),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Ref ID:", color = Color.Gray, fontSize = 14.sp)
                                    Text(tx.id, color = Color.Gray, fontSize = 12.sp)
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
                    // Unique App ID (Read-only)
                    OutlinedTextField(
                        value = uniqueId,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("App Unique ID (System Assigned)", color = Color.Gray) },
                        leadingIcon = { Icon(Icons.Default.Tag, contentDescription = "ID", tint = PinkPrimary) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = cardBg,
                            unfocusedContainerColor = cardBg,
                            disabledContainerColor = cardBg,
                            focusedBorderColor = borderColor,
                            unfocusedBorderColor = borderColor,
                            focusedTextColor = Color.Gray,
                            unfocusedTextColor = Color.Gray
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Full Name Field
                    OutlinedTextField(
                        value = tempFullName,
                        onValueChange = { 
                            tempFullName = it
                            if (it.isNotBlank()) fullNameError = null
                        },
                        label = { Text("Full Name", color = Color.Gray) },
                        leadingIcon = { Icon(Icons.Default.Badge, contentDescription = "Full Name", tint = PinkPrimary) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = textFieldColors,
                        shape = RoundedCornerShape(12.dp),
                        isError = fullNameError != null,
                        supportingText = { fullNameError?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Username Field
                    OutlinedTextField(
                        value = tempUsername,
                        onValueChange = { 
                            tempUsername = it
                            if (it.isNotBlank()) usernameError = null
                        },
                        label = { Text("Username", color = Color.Gray) },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Username", tint = PinkPrimary) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = textFieldColors,
                        shape = RoundedCornerShape(12.dp),
                        isError = usernameError != null,
                        supportingText = { usernameError?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
                    )

                    Spacer(modifier = Modifier.height(16.dp))


                    // Phone Number Field
                    OutlinedTextField(
                        value = tempPhone,
                        onValueChange = { 
                            tempPhone = it
                            if (it.isNotBlank()) phoneError = null
                        },
                        label = { Text("Phone Number", color = Color.Gray) },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = "Phone", tint = PinkPrimary) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = textFieldColors,
                        shape = RoundedCornerShape(12.dp),
                        isError = phoneError != null,
                        supportingText = { phoneError?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Location Field
                    OutlinedTextField(
                        value = tempLocation,
                        onValueChange = { 
                            tempLocation = it
                            if (it.isNotBlank()) locationError = null
                        },
                        label = { Text("Location", color = Color.Gray) },
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = "Location", tint = PinkPrimary) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = textFieldColors,
                        shape = RoundedCornerShape(12.dp),
                        isError = locationError != null,
                        supportingText = { locationError?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        ExposedDropdownMenuBox(
                            expanded = genderExpanded,
                            onExpandedChange = { genderExpanded = it },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = tempGender,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Gender", color = Color.Gray) },
                                modifier = Modifier.menuAnchor().fillMaxWidth(),
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = genderExpanded) },
                                colors = textFieldColors,
                                shape = RoundedCornerShape(12.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = genderExpanded,
                                onDismissRequest = { genderExpanded = false }
                            ) {
                                genders.forEach { selectionOption ->
                                    DropdownMenuItem(
                                        text = { Text(selectionOption) },
                                        onClick = {
                                            tempGender = selectionOption
                                            genderExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = tempAge,
                            onValueChange = { 
                                tempAge = it
                                if (it.isNotBlank() && it.toIntOrNull() != null) ageError = null
                            },
                            label = { Text("Age", color = Color.Gray) },
                            modifier = Modifier.weight(1f),
                            colors = textFieldColors,
                            shape = RoundedCornerShape(12.dp),
                            isError = ageError != null,
                            supportingText = { ageError?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
                        )
                    }

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
                            label = { Text("Language", color = Color.Gray) },
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

                    Text(
                        text = "Interests & Hobbies (Select Multiple)",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                            .background(cardBg)
                            .padding(12.dp)
                    ) {
                        val chunks = availableInterests.chunked(3)
                        chunks.forEach { chunk ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                chunk.forEach { interest ->
                                    val isSelected = tempSelectedInterests.contains(interest)
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (isSelected) PinkPrimary.copy(alpha = 0.2f) else borderColor)
                                            .border(1.dp, if (isSelected) PinkPrimary else Color.Transparent, RoundedCornerShape(10.dp))
                                            .clickable {
                                                tempSelectedInterests = if (isSelected) {
                                                    tempSelectedInterests - interest
                                                } else {
                                                    tempSelectedInterests + interest
                                                }
                                            }
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = interest,
                                            color = if (isSelected) PinkPrimary else Color.White,
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                }
                                if (chunk.size < 3) {
                                    repeat(3 - chunk.size) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    
                    OutlinedTextField(
                        value = tempBio,
                        onValueChange = { tempBio = it },
                        label = { Text("Bio", color = Color.Gray) },
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
                            if (tempUsername.isBlank()) {
                                usernameError = "Required"
                                isValid = false
                            } else {
                                usernameError = null
                            }

                            if (tempFullName.isBlank()) {
                                fullNameError = "Required"
                                isValid = false
                            } else {
                                fullNameError = null
                            }

                            if (tempPhone.isBlank()) {
                                phoneError = "Required"
                                isValid = false
                            } else {
                                phoneError = null
                            }

                            if (tempLocation.isBlank()) {
                                locationError = "Required"
                                isValid = false
                            } else {
                                locationError = null
                            }

                            if (tempAge.isBlank() || tempAge.toIntOrNull() == null) {
                                ageError = "Invalid age"
                                isValid = false
                            } else {
                                ageError = null
                            }
                            
                            if (isValid) {
                                username = tempUsername
                                fullName = tempFullName
                                phone = tempPhone
                                location = tempLocation
                                age = tempAge
                                gender = tempGender
                                bio = tempBio
                                language = tempLanguage
                                selectedInterests = tempSelectedInterests
                                Toast.makeText(context, "Personal Info Saved Successfully!", Toast.LENGTH_SHORT).show()
                            }
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
                        Text("No saved favorites yet.", color = Color.Gray, fontSize = 16.sp)
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
                                    Text(model.name, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Star, contentDescription = "Rating", tint = Color(0xFFFFD700), modifier = Modifier.size(13.dp))
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(String.format("%.1f", model.rating), color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        Text(" (${model.reviewsCount})", color = Color.LightGray, fontSize = 12.sp)
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(model.categories.firstOrNull() ?: "", color = Color.LightGray, fontSize = 14.sp)
                                }
                                IconButton(onClick = { viewModel?.toggleFavorite(model.id) }) {
                                    Icon(Icons.Default.Favorite, contentDescription = "Remove Favorite", tint = PinkPrimary)
                                }
                            }
                        }
                    }
                }
            }
            
            if (selectedTab == 2) {
                Spacer(modifier = Modifier.height(32.dp))
                val context = LocalContext.current
                var showLogOutAtProfileConfirm by remember { mutableStateOf(false) }
                
                OutlinedButton(
                    onClick = { showLogOutAtProfileConfirm = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(bottom = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = PinkPrimary
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PinkPrimary.copy(alpha = 0.5f))
                ) {
                    Icon(Icons.Default.Logout, contentDescription = "Logout")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Logout", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                if (showLogOutAtProfileConfirm) {
                    ConfirmDialog(
                        title = "Log Out",
                        text = "Are you sure you want to log out?",
                        confirmText = "Log Out",
                        onConfirm = {
                            Toast.makeText(context, "Logged Out Successfully", Toast.LENGTH_SHORT).show()
                        },
                        onDismiss = { showLogOutAtProfileConfirm = false }
                    )
                }
            }
        }
    }
    }
}

@Composable
fun ProfileStatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, color = Color.Gray, fontSize = 12.sp)
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
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(cardVariant, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = title, tint = textColor, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(title, color = textColor, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(value, color = textColor.copy(alpha = 0.6f), fontSize = 14.sp)
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

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = textColor,
        unfocusedTextColor = textColor,
        focusedContainerColor = cardBg,
        unfocusedContainerColor = cardBg,
        focusedBorderColor = PinkPrimary,
        unfocusedBorderColor = borderColor,
        cursorColor = PinkPrimary
    )

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

    var simFriendNameSetting by remember { mutableStateOf("") }
    var simPromoCodeSetting by remember(myPromoCode) { mutableStateOf(myPromoCode) }
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
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
        SettingsSection(title = "Invite & Earn 🎁", icon = Icons.Default.MonetizationOn) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Share the joy! Invite friends to join and get free rewards. For each friend who uses your invite code, both of you will receive 100 free bonus tokens!",
                    color = Color.LightGray,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Big Clear Invite Code Box
                Text(
                    text = "YOUR REWARDS CODE:",
                    color = Color.Gray,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                
                Spacer(modifier = Modifier.height(6.dp))
                
                val clipboardManager = androidx.compose.ui.platform.LocalClipboardManager.current
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF2A2836).copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .border(1.dp, PinkPrimary, RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = myPromoCode,
                        color = Color.White,
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
                            .background(Color(0xFF2A2836).copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFF2A2836), RoundedCornerShape(12.dp))
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
                            color = Color.Gray,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    // Total Earnings card
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(Color(0xFF2A2836).copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFF2A2836), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${referredUsers.size * 100} Tokens",
                            color = Color(0xFFFFD700),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.testTag("referral_settings_bonus_tokens_text")
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Reward Tokens",
                            color = Color.Gray,
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
                    color = Color.Gray,
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
                            .background(Color(0xFF1B3D2F))
                            .padding(10.dp)
                    ) {
                        Text("✓ You have successfully redeemed a code and earned +100 tokens!", color = Color(0xFF3DDC84), fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
                            placeholder = { Text("e.g. VIP-FRIEND-123", fontSize = 12.sp, color = Color.Gray) },
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
                                    Toast.makeText(context, "Successful! Received +100 bonus tokens.", Toast.LENGTH_LONG).show()
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
                
                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(color = borderColor)
                Spacer(modifier = Modifier.height(16.dp))
                
                // SIMULATOR (Beautiful, clean, and highly automated for easy user registration testing)
                Text(
                    text = "DEMO & TEST ZONE:",
                    color = Color.Gray,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Test this feature instantly: enter a name below and click simulate to see your stats grow by +100 tokens as if a real friend signed up!",
                    color = Color.Gray,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = simFriendNameSetting,
                        onValueChange = { simFriendNameSetting = it },
                        placeholder = { Text("Friend's Username", fontSize = 12.sp, color = Color.Gray) },
                        singleLine = true,
                        colors = textFieldColors,
                        modifier = Modifier
                            .weight(1.3f)
                            .height(46.dp)
                            .testTag("sim_settings_friend_input")
                    )
                    
                    OutlinedTextField(
                        value = simPromoCodeSetting,
                        onValueChange = { simPromoCodeSetting = it },
                        placeholder = { Text("Code Used", fontSize = 12.sp, color = Color.Gray) },
                        singleLine = true,
                        colors = textFieldColors,
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("sim_settings_code_input")
                    )
                }
                
                Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            if (simFriendNameSetting.isBlank()) {
                                Toast.makeText(context, "Please enter a test friend's username.", Toast.LENGTH_SHORT).show()
                            } else {
                                val codeToUse = if (simPromoCodeSetting.isBlank()) myPromoCode else simPromoCodeSetting
                                if (viewModel?.simulateFriendRegister(simFriendNameSetting, codeToUse) == true) {
                                    Toast.makeText(context, "Mock Signup Success! @$simFriendNameSetting has registered with your code and you got +100 referral tokens!", Toast.LENGTH_LONG).show()
                                    simFriendNameSetting = ""
                                    simPromoCodeSetting = myPromoCode
                                } else {
                                    Toast.makeText(context, "Invalid Referral Code used inside demo. Make sure it matches your own code: $myPromoCode", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("sim_settings_action_btn"),
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
                            Text("Simulate Free Registration 🚀", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
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
fun ConfirmDialog(title: String, text: String, confirmText: String = "Confirm", onConfirm: () -> Unit, onDismiss: () -> Unit) {
    val textColor = MaterialTheme.colorScheme.onSurface
    val secondaryText = MaterialTheme.colorScheme.onSurfaceVariant
    val cardBg = MaterialTheme.colorScheme.surface

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        containerColor = cardBg,
        title = {
            Text(title, color = textColor, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        },
        text = {
            Text(text, color = secondaryText, fontSize = 14.sp, lineHeight = 22.sp)
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(); onDismiss() }) {
                Text(confirmText, color = Color(0xFFFF4D4D), fontWeight = FontWeight.Bold)
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