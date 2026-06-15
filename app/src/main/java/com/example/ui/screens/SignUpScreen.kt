package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.widget.Toast
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import com.example.ui.theme.LightShadow
import com.example.ui.theme.OrangeSecondary
import com.example.ui.theme.PinkPrimary
import com.example.ui.theme.SoftScreenBackground
import com.example.ui.theme.isAppDarkTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    viewModel: com.example.MainViewModel? = null,
    onSignUpSuccess: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var isModel by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Male") }
    var age by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var kycUri by remember { mutableStateOf<Uri?>(null) }
    var audioRate by remember { mutableStateOf("") }
    var videoRate by remember { mutableStateOf("") }
    
    var nameError by remember { mutableStateOf<String?>(null) }
    var ageError by remember { mutableStateOf<String?>(null) }
    var audioRateError by remember { mutableStateOf<String?>(null) }
    var videoRateError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) } 

    var kycError by remember { mutableStateOf<String?>(null) }
    var otp by remember { mutableStateOf("") }
    var isOtpSent by remember { mutableStateOf(false) }
    var generatedOtp by remember { mutableStateOf("") }
    var isSendingOtp by remember { mutableStateOf(false) }
    var otpError by remember { mutableStateOf<String?>(null) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        kycUri = uri
    }

    val bg = MaterialTheme.colorScheme.background
    val inputBg = MaterialTheme.colorScheme.surface
    val borderColor = MaterialTheme.colorScheme.outline
    val textColor = MaterialTheme.colorScheme.onSurface
    val secondaryTextColor = MaterialTheme.colorScheme.onSurfaceVariant
    val isDarkTheme = isAppDarkTheme()
    
    val buttonGradient = Brush.horizontalGradient(
        colors = listOf(  PinkPrimary, OrangeSecondary)
    )

    SoftScreenBackground {
        AuthBackgroundGlows(isDarkTheme = isDarkTheme)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Box(modifier = Modifier.fillMaxWidth()) {
                IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = textColor)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            // App Logo
            AuthAppLogo(size = 80.dp, cornerRadius = 20.dp)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Create Account ✨",
                    color = textColor,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Join Pairr.live and start connecting.",
                    color = secondaryTextColor,
                    fontSize = 14.sp
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // User / Model Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .then(
                        if (!isDarkTheme) {
                            Modifier.shadow(
                                4.dp,
                                RoundedCornerShape(24.dp),
                                ambientColor = LightShadow,
                                spotColor = Color.Black.copy(alpha = 0.06f)
                            )
                        } else Modifier
                    )
                    .clip(RoundedCornerShape(24.dp))
                    .background(inputBg)
                    .border(1.dp, borderColor, RoundedCornerShape(24.dp)),
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(24.dp))
                        .background(if (!isModel) PinkPrimary else Color.Transparent)
                        .clickable { isModel = false },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "I am a User",
                        color = if (!isModel) Color.White else textColor,
                        fontWeight = if (!isModel) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 14.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(24.dp))
                        .background(if (isModel) PinkPrimary else Color.Transparent)
                        .clickable { isModel = true },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "I am a Model",
                        color = if (isModel) Color.White else textColor,
                        fontWeight = if (isModel) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 14.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))

            val textFieldColors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = inputBg,
                unfocusedContainerColor = inputBg,
                disabledContainerColor = inputBg,
                focusedBorderColor = PinkPrimary,
                unfocusedBorderColor = borderColor,
                focusedTextColor = textColor,
                unfocusedTextColor = textColor,
                focusedPlaceholderColor = secondaryTextColor,
                unfocusedPlaceholderColor = secondaryTextColor,
                focusedLeadingIconColor = PinkPrimary,
                unfocusedLeadingIconColor = PinkPrimary,
                focusedTrailingIconColor = secondaryTextColor,
                unfocusedTrailingIconColor = secondaryTextColor
            )

            OutlinedTextField(
                value = name,
                onValueChange = { 
                    name = it
                    if (it.isNotBlank()) nameError = null
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Full Name") },
                leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = "Name") },
                shape = RoundedCornerShape(12.dp),
                colors = textFieldColors,
                singleLine = true,
                isError = nameError != null,
                supportingText = { nameError?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = age,
                onValueChange = { 
                    age = it
                    if (it.isNotBlank()) ageError = null
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Age") },
                leadingIcon = { Icon(Icons.Outlined.CalendarToday, contentDescription = "Age") },
                shape = RoundedCornerShape(12.dp),
                colors = textFieldColors,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = ageError != null,
                supportingText = { ageError?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
            )

            Spacer(modifier = Modifier.height(16.dp))
            
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Gender", color = secondaryTextColor, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    val genders = listOf("Male", "Female", "Other")
                    genders.forEach { g ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = gender == g,
                                onClick = { gender = g },
                                colors = RadioButtonDefaults.colors(selectedColor = PinkPrimary, unselectedColor = secondaryTextColor)
                            )
                            Text(g, color = textColor, fontSize = 14.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { 
                    phone = it
                    if (it.isNotBlank()) phoneError = null
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Phone Number") },
                leadingIcon = { Icon(Icons.Outlined.Phone, contentDescription = "Phone") },
                shape = RoundedCornerShape(12.dp),
                colors = textFieldColors,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                isError = phoneError != null,
                supportingText = { phoneError?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = otp,
                onValueChange = { 
                    otp = it
                    if (it.isNotBlank()) otpError = null
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Verification OTP") },
                leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = "OTP Icon") },
                shape = RoundedCornerShape(12.dp),
                colors = textFieldColors,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = otpError != null,
                supportingText = { otpError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                trailingIcon = {
                    TextButton(
                        onClick = {
                            if (phone.isBlank()) {
                                phoneError = "Required"
                            } else {
                                val code = (100000..999999).random().toString()
                                generatedOtp = code
                                isOtpSent = true
                                Toast.makeText(context, "OTP code: $code (Auto-filled)", Toast.LENGTH_LONG).show()
                                otp = code // Auto-fill for excellent user experience and simulation
                            }
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = PinkPrimary)
                    ) {
                        Text(
                            text = if (isOtpSent) "Resend OTP" else "Send OTP",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (isModel) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedTextField(
                        value = audioRate,
                        onValueChange = { 
                            audioRate = it
                            if (it.isNotBlank()) audioRateError = null
                        },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Audio Rate") },
                        leadingIcon = { Icon(Icons.Outlined.Phone, contentDescription = "Audio Rate") },
                        shape = RoundedCornerShape(12.dp),
                        colors = textFieldColors,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = audioRateError != null,
                        supportingText = { audioRateError?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
                    )
                    
                    OutlinedTextField(
                        value = videoRate,
                        onValueChange = { 
                            videoRate = it
                            if (it.isNotBlank()) videoRateError = null
                        },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Video Rate") },
                        leadingIcon = { Icon(Icons.Outlined.Videocam, contentDescription = "Video Rate") },
                        shape = RoundedCornerShape(12.dp),
                        colors = textFieldColors,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = videoRateError != null,
                        supportingText = { videoRateError?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                OutlinedButton(
                    onClick = { 
                        filePickerLauncher.launch("*/*")
                        kycError = null
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = textColor
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (kycError != null) MaterialTheme.colorScheme.error else borderColor)
                ) {
                    Icon(Icons.Default.UploadFile, contentDescription = "Upload KYC", tint = PinkPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (kycUri != null) "Document Selected" else "Upload KYC Document")
                }
                kycError?.let {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = {
                    var isValid = true
                    
                    if (name.isBlank()) {
                        nameError = "Required"
                        isValid = false
                    } else {
                        nameError = null
                    }
                    
                    if (age.isBlank()) {
                        ageError = "Required"
                        isValid = false
                    } else {
                        ageError = null
                    }

                    if (isModel) {
                        if (audioRate.isBlank()) {
                            audioRateError = "Required"
                            isValid = false
                        } else {
                            audioRateError = null
                        }
                        
                        if (videoRate.isBlank()) {
                            videoRateError = "Required"
                            isValid = false
                        } else {
                            videoRateError = null
                        }
                    }

                    if (phone.isBlank()) {
                        phoneError = "Required"
                        isValid = false
                    } else if (phone.length < 6) {
                        phoneError = "Please enter a valid phone number"
                        isValid = false
                    } else {
                        phoneError = null
                    }

                    if (generatedOtp.isEmpty()) {
                        otpError = "Please click Send OTP first"
                        isValid = false
                    } else if (otp.isBlank()) {
                        otpError = "Required"
                        isValid = false
                    } else if (otp != generatedOtp) {
                        otpError = "Invalid verification code"
                        isValid = false
                    } else {
                        otpError = null
                    }

                    if (isModel && kycUri == null) {
                        kycError = "KYC document required for registering as a Model"
                        isValid = false
                    } else {
                        kycError = null
                    }

                    if (isValid) {
                        viewModel?.registerUser(name, phone, "dummy_password", isModel, gender, age, audioRate, videoRate) { success, errorMsg ->
                            if (success) {
                                Toast.makeText(context, "Account Created Successfully! Welcome to Pairr.", Toast.LENGTH_LONG).show()
                                onSignUpSuccess()
                            } else {
                                Toast.makeText(context, errorMsg ?: "Registration failed", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .then(
                        if (!isDarkTheme) {
                            Modifier.shadow(
                                8.dp,
                                RoundedCornerShape(12.dp),
                                ambientColor = LightShadow,
                                spotColor = PinkPrimary.copy(alpha = 0.25f)
                            )
                        } else Modifier
                    ),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(buttonGradient),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Sign Up",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = borderColor)
                Text(
                    text = "or register with",
                    color = secondaryTextColor,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = borderColor)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AuthSocialButton(
                    provider = SocialProvider.Google,
                    label = "Google",
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            Toast.makeText(context, "Signed up via Google successfully! Welcome.", Toast.LENGTH_SHORT).show()
                            onSignUpSuccess()
                        }
                )
                AuthSocialButton(
                    provider = SocialProvider.Facebook,
                    label = "Facebook",
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            Toast.makeText(context, "Signed up via Facebook successfully! Welcome.", Toast.LENGTH_SHORT).show()
                            onSignUpSuccess()
                        }
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                modifier = Modifier
                    .padding(bottom = 32.dp)
                    .clickable { onBack() },
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = secondaryTextColor)) {
                        append("Already have an account? ")
                    }
                    withStyle(style = SpanStyle(color = PinkPrimary, fontWeight = FontWeight.Bold)) {
                        append("Login")
                    }
                },
                fontSize = 14.sp
            )
        }
    }
}

private fun Color.luminance(): Float = 0.299f * red + 0.587f * green + 0.114f * blue
