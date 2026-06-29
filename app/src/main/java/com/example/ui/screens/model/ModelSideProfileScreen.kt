package com.example.ui.screens.model

import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.ConfirmDialog
import com.example.ui.screens.ProfileStatItemV2
import com.example.ui.screens.TokenPlan
import com.example.ui.screens.SimpleTextDialog
import com.example.ui.screens.InputDialog
import com.example.ui.screens.SettingsSection
import com.example.ui.screens.SettingsSwitchItem
import com.example.ui.screens.SettingsActionItem

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
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Notifications
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
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.AccountBalanceWallet
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.ModelCallEarning
import com.example.ModelReview
import com.example.data.displayProfilePhotoUrl
import com.example.ui.components.ModelProfilePhotoSection
import com.example.ui.components.ModelRatePerMinuteRow
import com.example.ui.screens.AllReviewsDialog
import com.example.ui.theme.AppSegmentedTabs
import com.example.ui.theme.OrangeSecondary
import com.example.ui.theme.PinkPrimary
import com.example.ui.theme.SoftScreenBackground
import com.example.ui.theme.appSuccessColor
import com.example.ui.theme.appSuccessContainer
import com.example.ui.theme.appSurfaceCard
import com.example.ui.theme.AppBottomNavClearance
import com.example.ui.theme.appVideoAccentContainer

data class TokenPlan(
    val title: String,
    val tokens: Int,
    val price: String,
    val bonus: String = "",
    val isVideo: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelSideProfileScreen(
    viewModel: com.example.MainViewModel? = null,
    onViewMoreTransactions: (() -> Unit)? = null,
    onViewPackages: ((String) -> Unit)? = null,
    onLogout: (() -> Unit)? = null,
    onUserClick: (String) -> Unit = {},
    onViewAllCallEarnings: () -> Unit = {}
) {

    // Model specific state variables
    val audioRate by (viewModel?.modelAudioRate ?: kotlinx.coroutines.flow.MutableStateFlow(0)).collectAsState(initial = 0)
    val videoRate by (viewModel?.modelVideoRate ?: kotlinx.coroutines.flow.MutableStateFlow(0)).collectAsState(initial = 0)
    
    val monthlyEarn by (viewModel?.modelMonthlyEarnings ?: kotlinx.coroutines.flow.MutableStateFlow(0)).collectAsState(initial = 0)
    val lifetimeEarn by (viewModel?.modelLifetimeEarnings ?: kotlinx.coroutines.flow.MutableStateFlow(0)).collectAsState(initial = 0)
    val currentDailyEarn by (viewModel?.modelDailyEarnings ?: kotlinx.coroutines.flow.MutableStateFlow(0)).collectAsState(initial = 0) 
    
    var showWithdrawalDialog by remember { mutableStateOf(false) }

    val savedUsername by (viewModel?.modelProfileUsername
        ?: kotlinx.coroutines.flow.MutableStateFlow("alessia_beauty"))
        .collectAsStateWithLifecycle()
    val savedFullName by (viewModel?.modelProfileFullName
        ?: kotlinx.coroutines.flow.MutableStateFlow("Alessia K."))
        .collectAsStateWithLifecycle()

    var email by remember { mutableStateOf("john.doe@example.com") }
    var age by rememberSaveable { mutableStateOf("28") }
    var gender by rememberSaveable { mutableStateOf("Male") }
    var bio by rememberSaveable { mutableStateOf("") }
    var selectedLanguages by remember { mutableStateOf(setOf("English")) }
    var selectedCategories by remember { mutableStateOf(setOf<String>()) }
    var profilePhotoUri by remember { mutableStateOf<Uri?>(null) }
    
    var isSettingsShowing by remember { mutableStateOf(false) }

    var showCurrentPlanDetails by remember { mutableStateOf(false) }
    var showPurchaseConfirmDialog by remember { mutableStateOf<TokenPlan?>(null) }
    var showRatingDialog by remember { mutableStateOf(false) }
    var showReviewsDialog by remember { mutableStateOf(false) }
    var showEarningDetailsDialog by remember { mutableStateOf<ModelCallEarning?>(null) }

    val models by (viewModel?.models ?: kotlinx.coroutines.flow.MutableStateFlow(emptyList()))
        .collectAsStateWithLifecycle()
    val currentModel = models.find { it.id == viewModel?.getCurrentModelId() }
    val registeredPhone = viewModel?.getCurrentUserPhoneDisplay().orEmpty()
        .ifBlank { "+1 (555) 019-2834" }
    var showTopUpOptionsDialog by remember { mutableStateOf(false) }
    var selectedPlanFilter by remember { mutableStateOf("All") } // "All", "Audio", "Video"

    if (isSettingsShowing) {
        SettingsScreen(viewModel = viewModel, onBack = { isSettingsShowing = false }, onLogout = onLogout)
        return
    }

    var tempUsername by remember(savedUsername) { mutableStateOf(savedUsername) }
    var tempFullName by remember(savedFullName) { mutableStateOf(savedFullName) }
    var tempAge by remember { mutableStateOf(age) }
    var tempGender by remember { mutableStateOf(gender) }
    var tempBio by remember { mutableStateOf(bio) }
    var tempSelectedLanguages by remember { mutableStateOf(selectedLanguages) }
    var tempSelectedCategories by remember { mutableStateOf(selectedCategories) }

    LaunchedEffect(savedUsername, savedFullName) {
        tempUsername = savedUsername
        tempFullName = savedFullName
    }

    LaunchedEffect(currentModel?.id, currentModel?.bio, currentModel?.languages, currentModel?.categories) {
        currentModel?.let { model ->
            val defaultBio = "Hey there! I am using the app to explore custom matches."
            if (bio.isBlank()) bio = model.bio.ifBlank { defaultBio }
            tempBio = model.bio.ifBlank { defaultBio }
            val langs = model.languages.takeIf { it.isNotEmpty() } ?: listOf("English")
            selectedLanguages = langs.toSet()
            tempSelectedLanguages = langs.toSet()
            val cats = model.categories.toSet()
            selectedCategories = cats
            tempSelectedCategories = cats
        }
    }

    var usernameError by remember { mutableStateOf<String?>(null) }
    var fullNameError by remember { mutableStateOf<String?>(null) }
    var ageError by remember { mutableStateOf<String?>(null) }
    
    var genderExpanded by remember { mutableStateOf(false) }
    val genders = listOf("Male", "Female", "Other", "Prefer not to say")
    
    val languages = listOf(
        "Hindi", "English", "Bengali",
        "Telugu", "Marathi", "Tamil",
        "Gujarati", "Kannada", "Malayalam",
        "Punjabi", "Urdu", "Odia"
    )
    val talkCategories = listOf(
        "Casual Talk", "Movies", "Music", "Advice", "Listening",
        "Deep Talk", "Arts", "Gaming", "Travel", "Fitness",
        "Relationships", "Career", "Spirituality", "Cooking", "Fashion"
    )

    val context = LocalContext.current

    val profilePhotoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            profilePhotoUri = it
            viewModel?.setProfilePhotoForCurrentModel(it.toString())
            Toast.makeText(context, "Profile photo updated", Toast.LENGTH_SHORT).show()
        }
    }

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
        unfocusedTextColor = textColor,
        cursorColor = PinkPrimary,
        focusedLabelColor = PinkPrimary,
        unfocusedLabelColor = secondaryTextColor
    )

    SoftScreenBackground {
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
                    Box(modifier = Modifier.fillMaxWidth().padding(end = 16.dp, top = 16.dp), contentAlignment = Alignment.TopEnd) {
                        IconButton(
                            onClick = { isSettingsShowing = true },
                            colors = IconButtonDefaults.iconButtonColors(containerColor = borderColor.copy(alpha = 0.35f))
                        ) {
                            Icon(Icons.Default.Settings, contentDescription = "Settings", tint = PinkPrimary)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    
                    Text(
                        text = savedFullName,
                        color = textColor,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                        color = borderColor,
                        thickness = 1.dp
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    currentModel?.let { model ->
                        ModelRatePerMinuteRow(
                            audioPrice = model.audioPrice,
                            videoPrice = model.videoPrice,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ProfileStatItemV2("TOTAL CALLS", "1,250")
                        ProfileStatItemV2(
                            "RATING",
                            String.format("%.1f", viewModel?.getAverageRatingForCurrentModel() ?: 0f)
                        )
                    }
                }
                
                // Avatar (Overlapping)
                Box {
                    AsyncImage(
                        model = profilePhotoUri
                            ?: currentModel?.profilePhotoUrl
                            ?: currentModel?.displayProfilePhotoUrl()
                            ?: "https://i.pravatar.cc/300?u=${viewModel?.getCurrentModelId() ?: "user"}",
                        contentDescription = "User Profile",
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                            .border(3.dp, Color.White, CircleShape)
                            .clickable { profilePhotoPicker.launch("image/*") },
                        contentScale = ContentScale.Crop
                    )
                }
            }

            var selectedTab by remember { mutableIntStateOf(0) }
            
            Column(modifier = Modifier.padding(horizontal = 16.dp).padding(top = 10.dp)) {
                AppSegmentedTabs(
                    tabs = listOf("Payout", "Info", "Stats"),
                    selectedIndex = selectedTab,
                    onTabSelected = { selectedTab = it },
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(20.dp))
            
            if (selectedTab == 0) {
                // PAYOUT SECTION
                val context = LocalContext.current

                // Pricing Management
                Text("Pricing Management", color = textColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                
                var localAudio by remember(audioRate) { mutableStateOf(audioRate.toString()) }
                var localVideo by remember(videoRate) { mutableStateOf(videoRate.toString()) }
                
                val isAudioValid = localAudio.toIntOrNull()?.let { it > 0 } ?: false
                val isVideoValid = localVideo.toIntOrNull()?.let { it > 0 } ?: false
                val isValidPricing = isAudioValid && isVideoValid
                
                Column(
                    modifier = Modifier.fillMaxWidth().background(cardBg, RoundedCornerShape(16.dp)).border(1.dp, borderColor, RoundedCornerShape(16.dp)).padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Box(modifier = Modifier.size(40.dp).background(appSuccessContainer(), CircleShape), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = appSuccessColor())
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Audio Rate / Min", color = textColor.copy(alpha = 0.7f), modifier = Modifier.weight(1f))
                        OutlinedTextField(
                            value = localAudio,
                            onValueChange = { localAudio = it },
                            modifier = Modifier.width(90.dp).height(50.dp),
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                            isError = !isAudioValid,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = textColor, 
                                unfocusedTextColor = textColor,
                                focusedBorderColor = PinkPrimary,
                                unfocusedBorderColor = borderColor,
                                errorBorderColor = Color.Red
                            )
                        )
                    }
                    if (!isAudioValid && localAudio.isNotEmpty()) {
                        Text("Must be > 0", color = Color.Red, fontSize = 10.sp, modifier = Modifier.align(Alignment.End).padding(top = 2.dp))
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Box(modifier = Modifier.size(40.dp).background(appVideoAccentContainer(), CircleShape), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Videocam, contentDescription = null, tint = Color(0xFFFF4D4D))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Video Rate / Min", color = textColor.copy(alpha = 0.7f), modifier = Modifier.weight(1f))
                        OutlinedTextField(
                            value = localVideo,
                            onValueChange = { localVideo = it },
                            modifier = Modifier.width(90.dp).height(50.dp),
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                            isError = !isVideoValid,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = textColor, 
                                unfocusedTextColor = textColor,
                                focusedBorderColor = PinkPrimary,
                                unfocusedBorderColor = borderColor,
                                errorBorderColor = Color.Red
                            )
                        )
                    }
                    if (!isVideoValid && localVideo.isNotEmpty()) {
                        Text("Must be > 0", color = Color.Red, fontSize = 10.sp, modifier = Modifier.align(Alignment.End).padding(top = 2.dp))
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            if (isValidPricing) {
                    viewModel?.setModelAudioRate(localAudio.toInt())
                    viewModel?.setModelVideoRate(localVideo.toInt())
                    android.widget.Toast.makeText(context, "Rates updated successfully", android.widget.Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = isValidPricing,
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
                Text("Save Custom Rates", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
    
    Spacer(modifier = Modifier.height(32.dp))
    
    // Payout Settings
    Text("Payout Settings", color = textColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(12.dp))
    
    var selectedPayoutMethod by remember { mutableStateOf("Bank Transfer") }
    var bankAccountNumber by remember { mutableStateOf("") }
    var ifscCode by remember { mutableStateOf("") }
    var upiId by remember { mutableStateOf("") }
    
    Column(
        modifier = Modifier.fillMaxWidth().background(cardBg, RoundedCornerShape(16.dp)).border(1.dp, borderColor, RoundedCornerShape(16.dp)).padding(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Surface(
                color = if (selectedPayoutMethod == "Bank Transfer") PinkPrimary.copy(alpha = 0.2f) else Color.Transparent,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f).clickable { selectedPayoutMethod = "Bank Transfer" }.border(1.dp, if (selectedPayoutMethod == "Bank Transfer") PinkPrimary else borderColor, RoundedCornerShape(12.dp))
            ) {
                Text("Bank Transfer", color = if (selectedPayoutMethod == "Bank Transfer") PinkPrimary else textColor, modifier = Modifier.padding(12.dp), textAlign = androidx.compose.ui.text.style.TextAlign.Center, fontWeight = FontWeight.Bold)
            }
            Surface(
                color = if (selectedPayoutMethod == "UPI") PinkPrimary.copy(alpha = 0.2f) else Color.Transparent,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f).clickable { selectedPayoutMethod = "UPI" }.border(1.dp, if (selectedPayoutMethod == "UPI") PinkPrimary else borderColor, RoundedCornerShape(12.dp))
            ) {
                Text("Digital Wallet / UPI", color = if (selectedPayoutMethod == "UPI") PinkPrimary else textColor, modifier = Modifier.padding(12.dp), textAlign = androidx.compose.ui.text.style.TextAlign.Center, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (selectedPayoutMethod == "Bank Transfer") {
            OutlinedTextField(
                value = bankAccountNumber,
                onValueChange = { bankAccountNumber = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Account Number", color = secondaryTextColor) },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                colors = textFieldColors
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = ifscCode,
                onValueChange = { ifscCode = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("IFSC Code", color = secondaryTextColor) },
                colors = textFieldColors
            )
        } else {
            OutlinedTextField(
                value = upiId,
                onValueChange = { upiId = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("UPI ID", color = secondaryTextColor) },
                colors = textFieldColors
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                val valid = if (selectedPayoutMethod == "Bank Transfer") bankAccountNumber.isNotBlank() && ifscCode.isNotBlank() else upiId.isNotBlank()
                if (valid) {
                    android.widget.Toast.makeText(context, "Payout details saved", android.widget.Toast.LENGTH_SHORT).show()
                } else {
                    android.widget.Toast.makeText(context, "Please enter all details", android.widget.Toast.LENGTH_SHORT).show()
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
                Text("Save Details", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
    
    Spacer(modifier = Modifier.height(32.dp))
    
    // Earnings & Withdrawals
    Text("Earnings & Withdrawals", color = textColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(12.dp))
    
    Column(
        modifier = Modifier.fillMaxWidth().background(cardBg, RoundedCornerShape(16.dp)).border(1.dp, borderColor, RoundedCornerShape(16.dp)).padding(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Available for Withdrawal", color = secondaryTextColor, fontSize = 14.sp)
            Icon(Icons.Default.CurrencyRupee, contentDescription = null, tint = PinkPrimary, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text("$currentDailyEarn Rupees", color = textColor, fontSize = 36.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        
        HorizontalDivider(color = borderColor)
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Monthly Earnings", color = secondaryTextColor, fontSize = 14.sp)
            Text("$monthlyEarn Rupees", color = PinkPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Lifetime Earnings", color = secondaryTextColor, fontSize = 14.sp)
            Text("$lifetimeEarn Rupees", color = textColor, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { showWithdrawalDialog = true },
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Request Withdrawal", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
                
            } else if (selectedTab == 1) {
                val mediaModel = currentModel ?: models.firstOrNull()
                if (mediaModel != null) {
                    ModelProfilePhotoSection(
                        profilePhotoUrl = profilePhotoUri?.toString()
                            ?: mediaModel.profilePhotoUrl?.takeIf { it.isNotBlank() },
                        cardBg = cardBg,
                        borderColor = borderColor,
                        textColor = textColor,
                        secondaryTextColor = secondaryTextColor,
                        onChangePhoto = { profilePhotoPicker.launch("image/*") },
                        onRemovePhoto = {
                            profilePhotoUri = null
                            viewModel?.clearProfilePhotoForCurrentModel()
                            Toast.makeText(context, "Profile photo removed", Toast.LENGTH_SHORT).show()
                        }
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }

                // Personal Info Section Form
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .appSurfaceCard(shape = RoundedCornerShape(20.dp))
                        .padding(16.dp)
                ) {
                    // Full Name Field
                    OutlinedTextField(
                        value = tempFullName,
                        onValueChange = { 
                            tempFullName = it
                            if (it.isNotBlank()) fullNameError = null
                        },
                        label = { Text("Full Name") },
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
                        label = { Text("Username") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Username", tint = PinkPrimary) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = textFieldColors,
                        shape = RoundedCornerShape(12.dp),
                        isError = usernameError != null,
                        supportingText = { usernameError?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
                    )

                    Spacer(modifier = Modifier.height(16.dp))


                    // Phone Number Field (read-only — tied to login)
                    OutlinedTextField(
                        value = registeredPhone,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Phone Number") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = "Phone", tint = PinkPrimary) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = cardBg,
                            unfocusedContainerColor = cardBg,
                            disabledContainerColor = cardBg,
                            focusedBorderColor = borderColor,
                            unfocusedBorderColor = borderColor,
                            disabledTextColor = secondaryTextColor,
                            focusedTextColor = secondaryTextColor,
                            unfocusedTextColor = secondaryTextColor
                        ),
                        shape = RoundedCornerShape(12.dp)
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
                                label = { Text("Gender") },
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
                            label = { Text("Age") },
                            modifier = Modifier.weight(1f),
                            colors = textFieldColors,
                            shape = RoundedCornerShape(12.dp),
                            isError = ageError != null,
                            supportingText = { ageError?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Languages (Select Multiple)",
                        color = textColor,
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
                        val languageChunks = languages.chunked(3)
                        languageChunks.forEach { chunk ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                chunk.forEach { lang ->
                                    val isSelected = tempSelectedLanguages.contains(lang)
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (isSelected) PinkPrimary.copy(alpha = 0.2f) else borderColor.copy(alpha = 0.35f))
                                            .border(1.dp, if (isSelected) PinkPrimary else Color.Transparent, RoundedCornerShape(10.dp))
                                            .clickable {
                                                tempSelectedLanguages = if (isSelected) {
                                                    tempSelectedLanguages - lang
                                                } else {
                                                    tempSelectedLanguages + lang
                                                }
                                            }
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = lang,
                                            color = if (isSelected) PinkPrimary else textColor,
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

                    Text(
                        text = "Topics (Select Multiple)",
                        color = textColor,
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
                        val categoryChunks = talkCategories.chunked(3)
                        categoryChunks.forEach { chunk ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                chunk.forEach { topic ->
                                    val isSelected = tempSelectedCategories.contains(topic)
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (isSelected) PinkPrimary.copy(alpha = 0.2f) else borderColor.copy(alpha = 0.35f))
                                            .border(1.dp, if (isSelected) PinkPrimary else Color.Transparent, RoundedCornerShape(10.dp))
                                            .clickable {
                                                tempSelectedCategories = if (isSelected) {
                                                    tempSelectedCategories - topic
                                                } else {
                                                    tempSelectedCategories + topic
                                                }
                                            }
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = topic,
                                            color = if (isSelected) PinkPrimary else textColor,
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                            maxLines = 2
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
                        label = { Text("Bio") },
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

                            if (tempAge.isBlank() || tempAge.toIntOrNull() == null) {
                                ageError = "Invalid age"
                                isValid = false
                            } else {
                                ageError = null
                            }
                            
                            if (isValid) {
                                viewModel?.updateModelProfileIdentity(tempUsername, tempFullName)
                                viewModel?.updateModelProfileContent(
                                    bio = tempBio,
                                    languages = tempSelectedLanguages,
                                    categories = tempSelectedCategories
                                )
                                age = tempAge
                                gender = tempGender
                                bio = tempBio
                                selectedLanguages = tempSelectedLanguages
                                selectedCategories = tempSelectedCategories
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
                val models by (viewModel?.models ?: kotlinx.coroutines.flow.MutableStateFlow(emptyList()))
                    .collectAsStateWithLifecycle()
                val currentModelId = viewModel?.getCurrentModelId()
                val currentModel = models.find { it.id == currentModelId }
                val modelReviews = viewModel?.getReviewsForCurrentModel() ?: emptyList()
                val avgRating = viewModel?.getAverageRatingForCurrentModel() ?: 0f
                val reviewCount = viewModel?.getReviewCountForCurrentModel() ?: 0
                val callEarnings = viewModel?.getEarningsForCurrentModel() ?: emptyList()
                val modelDisplayName = savedFullName

                Text("Your Performance", color = textColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(cardBg)
                            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
                            .clickable { showRatingDialog = true }
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = OrangeSecondary, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            String.format("%.1f", avgRating),
                            color = textColor,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text("Average Rating", color = secondaryTextColor, fontSize = 12.sp)
                        Text("Tap for breakdown", color = PinkPrimary.copy(alpha = 0.7f), fontSize = 10.sp)
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(cardBg)
                            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
                            .clickable { showReviewsDialog = true }
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.RateReview, contentDescription = null, tint = PinkPrimary, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            reviewCount.toString(),
                            color = textColor,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text("Total Reviews", color = secondaryTextColor, fontSize = 12.sp)
                        Text("Tap to view all", color = PinkPrimary.copy(alpha = 0.7f), fontSize = 10.sp)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Call Earnings", color = textColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    if (callEarnings.isNotEmpty()) {
                        TextButton(onClick = onViewAllCallEarnings) {
                            Text(
                                "View All",
                                color = PinkPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))

                if (callEarnings.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .background(cardBg, RoundedCornerShape(16.dp))
                            .border(1.dp, borderColor, RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No call earnings yet.", color = secondaryTextColor, fontSize = 14.sp)
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(cardBg, RoundedCornerShape(16.dp))
                            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        callEarnings.take(3).forEach { earning ->
                            val callerUserId = viewModel?.resolveUserIdByDisplayName(earning.callerName).orEmpty()
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { showEarningDetailsDialog = earning }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (earning.isVideo) PinkPrimary.copy(alpha = 0.12f)
                                            else appSuccessColor().copy(alpha = 0.12f),
                                            CircleShape
                                        )
                                        .clickable(enabled = callerUserId.isNotBlank()) {
                                            if (callerUserId.isNotBlank()) onUserClick(callerUserId)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        if (earning.isVideo) Icons.Default.Videocam else Icons.Default.Phone,
                                        contentDescription = null,
                                        tint = if (earning.isVideo) PinkPrimary else appSuccessColor(),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable(enabled = callerUserId.isNotBlank()) {
                                            if (callerUserId.isNotBlank()) onUserClick(callerUserId)
                                        }
                                ) {
                                    Text(
                                        "${if (earning.isVideo) "Video" else "Audio"} call — ${earning.callerName}",
                                        color = textColor,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 15.sp
                                    )
                                    Text(
                                        "${earning.duration} • ${earning.date}",
                                        color = secondaryTextColor,
                                        fontSize = 13.sp
                                    )
                                }
                                Text(
                                    "+${earning.amountEarned}",
                                    color = appSuccessColor(),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                        }

                    }
                }

            }

            Spacer(modifier = Modifier.height(AppBottomNavClearance))
        } // End of inner Tab Column
    } // End of outer Scroll Column

    if (showWithdrawalDialog) {
            val context = LocalContext.current
            var selectedMethod by remember { mutableStateOf("Bank Transfer") }
            var amountRequested by remember { mutableStateOf("") }
            var details by remember { mutableStateOf("") }
                       AlertDialog(
                onDismissRequest = { showWithdrawalDialog = false },
                containerColor = cardBg,
                title = { Text("Withdraw Funds", color = textColor) },
                text = {
                    Column {
                        Text("Available: $currentDailyEarn Rupees", color = secondaryTextColor)
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            FilterChip(
                                selected = selectedMethod == "Bank Transfer",
                                onClick = { selectedMethod = "Bank Transfer" },
                                label = { Text("Bank Transfer") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PinkPrimary,
                                    selectedLabelColor = Color.White,
                                    labelColor = secondaryTextColor
                                )
                            )
                            FilterChip(
                                selected = selectedMethod == "UPI",
                                onClick = { selectedMethod = "UPI" },
                                label = { Text("UPI") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PinkPrimary,
                                    selectedLabelColor = Color.White,
                                    labelColor = secondaryTextColor
                                )
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        OutlinedTextField(
                            value = amountRequested,
                            onValueChange = { amountRequested = it },
                            label = { Text("Amount to withdraw", color = secondaryTextColor) },
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                            colors = textFieldColors,
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        OutlinedTextField(
                            value = details,
                            onValueChange = { details = it },
                            label = { Text(if (selectedMethod == "UPI") "UPI ID" else "Account Number", color = secondaryTextColor) },
                            colors = textFieldColors,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = { 
                        val amt = amountRequested.toIntOrNull() ?: 0
                        if (amt > 0 && amt <= currentDailyEarn) {
                            viewModel?.requestWithdrawal(amt)
                            android.widget.Toast.makeText(context, "Withdrawal requested. Admin approval pending.", android.widget.Toast.LENGTH_LONG).show()
                            showWithdrawalDialog = false
                        } else {
                            android.widget.Toast.makeText(context, "Invalid amount", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Text("Submit Request", color = PinkPrimary, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showWithdrawalDialog = false }) {
                        Text("Cancel", color = secondaryTextColor)
                    }
                }
            )
        }

        if (showRatingDialog) {
            ModelRatingBreakdownDialog(
                averageRating = viewModel?.getAverageRatingForCurrentModel() ?: 0f,
                reviews = viewModel?.getReviewsForCurrentModel() ?: emptyList(),
                onDismiss = { showRatingDialog = false }
            )
        }

        if (showReviewsDialog) {
            val models by (viewModel?.models ?: kotlinx.coroutines.flow.MutableStateFlow(emptyList()))
                .collectAsStateWithLifecycle()
            val modelDisplayName = savedFullName
            AllReviewsDialog(
                modelName = modelDisplayName,
                reviews = viewModel?.getReviewsForCurrentModel() ?: emptyList(),
                onDismiss = { showReviewsDialog = false }
            )
        }

        showEarningDetailsDialog?.let { earning ->
            ModelEarningDetailsDialog(
                earning = earning,
                cardBg = cardBg,
                textColor = textColor,
                secondaryTextColor = secondaryTextColor,
                borderColor = borderColor,
                onViewCaller = {
                    val userId = viewModel?.resolveUserIdByDisplayName(earning.callerName).orEmpty()
                    if (userId.isNotBlank()) {
                        showEarningDetailsDialog = null
                        onUserClick(userId)
                    }
                },
                onDismiss = { showEarningDetailsDialog = null }
            )
        }
    }
}

@Composable
private fun ModelRatingBreakdownDialog(
    averageRating: Float,
    reviews: List<ModelReview>,
    onDismiss: () -> Unit
) {
    val textColor = MaterialTheme.colorScheme.onSurface
    val secondaryTextColor = textColor.copy(alpha = 0.6f)
    val cardBg = MaterialTheme.colorScheme.surface
    val totalReviews = reviews.size
    val distribution = (5 downTo 1).map { star ->
        star to reviews.count { it.rating == star }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = cardBg,
        title = {
            Text("Rating Breakdown", color = textColor, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = OrangeSecondary, modifier = Modifier.size(36.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        String.format("%.1f", averageRating),
                        color = textColor,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "from $totalReviews reviews",
                        color = secondaryTextColor,
                        fontSize = 14.sp
                    )
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline)

                distribution.forEach { (star, count) ->
                    val fraction = if (totalReviews > 0) count.toFloat() / totalReviews else 0f
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("$star★", color = textColor, fontSize = 13.sp, modifier = Modifier.width(28.dp))
                        LinearProgressIndicator(
                            progress = fraction,
                            modifier = Modifier
                                .weight(1f)
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = OrangeSecondary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(count.toString(), color = secondaryTextColor, fontSize = 12.sp, modifier = Modifier.width(24.dp))
                    }
                }

                if (reviews.isNotEmpty()) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                    Text("Recent ratings", color = textColor, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    reviews.take(3).forEach { review ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(review.reviewerName, color = textColor, fontSize = 13.sp)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                repeat(review.rating) {
                                    Icon(
                                        Icons.Default.Star,
                                        contentDescription = null,
                                        tint = OrangeSecondary,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = PinkPrimary)
            }
        }
    )
}

@Composable
internal fun ModelEarningDetailsDialog(
    earning: ModelCallEarning,
    cardBg: Color,
    textColor: Color,
    secondaryTextColor: Color,
    borderColor: Color,
    onViewCaller: () -> Unit = {},
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = cardBg,
        title = {
            Text("Call Earning Details", color = textColor, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable(onClick = onViewCaller)
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Caller", color = secondaryTextColor, fontSize = 14.sp)
                    Text(
                        earning.callerName,
                        color = PinkPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                DetailRow("Call Type", if (earning.isVideo) "Video Call" else "Audio Call", textColor, secondaryTextColor)
                DetailRow("Duration", earning.duration, textColor, secondaryTextColor)
                DetailRow("Date & Time", earning.date, textColor, secondaryTextColor)
                DetailRow("Status", earning.status, textColor, secondaryTextColor, valueColor = appSuccessColor())
                DetailRow("Rupees Earned", "+${earning.amountEarned}", textColor, secondaryTextColor, valueColor = appSuccessColor())
                DetailRow("Reference ID", earning.id, textColor, secondaryTextColor)
                HorizontalDivider(color = borderColor)
                Text(
                    "Earnings are credited after a completed private call session. Contact support with the reference ID for payout queries.",
                    color = secondaryTextColor,
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Dismiss", color = PinkPrimary)
            }
        }
    )
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    textColor: Color,
    secondaryTextColor: Color,
    valueColor: Color = textColor
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = secondaryTextColor, fontSize = 14.sp)
        Text(value, color = valueColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}
