package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.shadow
import com.example.ui.theme.LightShadow
import com.example.ui.theme.OrangeSecondary
import com.example.ui.theme.PinkPrimary
import com.example.ui.theme.SoftScreenBackground
import com.example.ui.theme.appOutlinedFieldColors
import com.example.ui.theme.appCaptionText
import com.example.ui.theme.appSecondaryText
import com.example.ui.theme.appBorderColor
import com.example.ui.theme.appCaptionText
import com.example.ui.theme.appSecondaryText
import com.example.ui.theme.appSoftShadow
import com.example.ui.theme.AppBorderWeight
import com.example.ui.theme.isAppDarkTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: com.example.MainViewModel? = null,
    onLoginSuccess: () -> Unit,
    onSignUpClick: () -> Unit
) {
    val context = LocalContext.current
    var phone by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var otpError by remember { mutableStateOf<String?>(null) }
    var isOtpSent by remember { mutableStateOf(false) }
    var generatedOtp by remember { mutableStateOf("") }
    var isSendingOtp by remember { mutableStateOf(false) }

    val bg = MaterialTheme.colorScheme.background
    val inputBg = MaterialTheme.colorScheme.surface
    val textColor = MaterialTheme.colorScheme.onSurface
    val secondaryTextColor = appSecondaryText()
    val isDarkTheme = isAppDarkTheme()
    val fieldShape = RoundedCornerShape(12.dp)
    
    val buttonGradient = Brush.horizontalGradient(
        colors = listOf(PinkPrimary, OrangeSecondary)
    )

    val textFieldColors = appOutlinedFieldColors()

    SoftScreenBackground {
        AuthBackgroundGlows(isDarkTheme = isDarkTheme)

        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val compact = maxHeight < 680.dp
            val logoSize = if (compact) 64.dp else 72.dp
            val logoRadius = if (compact) 18.dp else 20.dp
            val brandSize = if (compact) 28.sp else 32.sp
            val sectionGap = if (compact) 12.dp else 16.dp
            val fieldGap = if (compact) 10.dp else 12.dp
            val topSpacing = if (compact) 16.dp else 32.dp
            val brandFormGap = if (compact) 24.dp else 36.dp

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .imePadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(topSpacing))

                AuthAppLogo(size = logoSize, cornerRadius = logoRadius)

                Spacer(modifier = Modifier.height(if (compact) 8.dp else 10.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Pairr",
                        color = OrangeSecondary,
                        fontSize = brandSize,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = ".live",
                        color = PinkPrimary,
                        fontSize = brandSize,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "Connect. Chat. Live.",
                    color = secondaryTextColor,
                    fontSize = if (compact) 14.sp else 15.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(brandFormGap))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Welcome Back! 👋",
                        color = textColor,
                        fontSize = if (compact) 20.sp else 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Login to continue and explore exciting connections.",
                        color = secondaryTextColor,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(sectionGap))

                    OutlinedTextField(
                        value = phone,
                        onValueChange = {
                            phone = it
                            if (it.isNotBlank()) phoneError = null
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .appSoftShadow(fieldShape, elevation = if (isDarkTheme) 2.dp else 6.dp),
                        placeholder = { Text("Phone Number", color = appCaptionText()) },
                        leadingIcon = { Icon(Icons.Outlined.Phone, contentDescription = "Phone Number") },
                        shape = fieldShape,
                        colors = textFieldColors,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        isError = phoneError != null,
                        supportingText = {
                            if (phoneError != null) {
                                Text(phoneError!!, color = MaterialTheme.colorScheme.error)
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(fieldGap))

                    OutlinedTextField(
                        value = otp,
                        onValueChange = {
                            otp = it
                            if (it.isNotBlank()) otpError = null
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .appSoftShadow(fieldShape, elevation = if (isDarkTheme) 2.dp else 6.dp),
                        placeholder = { Text("Verification OTP", color = appCaptionText()) },
                        leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = "OTP") },
                        shape = fieldShape,
                        colors = textFieldColors,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = otpError != null,
                        supportingText = {
                            if (otpError != null) {
                                Text(otpError!!, color = MaterialTheme.colorScheme.error)
                            }
                        },
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
                                        otp = code
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

                    Spacer(modifier = Modifier.height(sectionGap))

                    Button(
                        onClick = {
                            var isValid = true
                            if (phone.isBlank()) {
                                phoneError = "Required"
                                isValid = false
                            } else if (phone.length < 6) {
                                phoneError = "Please enter a valid phone number"
                                isValid = false
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
                            }

                            if (isValid) {
                                if (viewModel != null) {
                                    viewModel.loginUser(phone, "dummy_password") { success, errorMsg ->
                                        if (success) {
                                            Toast.makeText(context, "Welcome back!", Toast.LENGTH_SHORT).show()
                                            onLoginSuccess()
                                        } else {
                                            otpError = errorMsg ?: "Login failed."
                                        }
                                    }
                                } else {
                                    onLoginSuccess()
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(if (compact) 50.dp else 52.dp)
                            .appSoftShadow(RoundedCornerShape(12.dp), elevation = if (isDarkTheme) 4.dp else 8.dp),
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
                                text = "Login",
                                color = Color.White,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(sectionGap))

                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        HorizontalDivider(modifier = Modifier.weight(1f), color = appBorderColor(AppBorderWeight.Subtle))
                        Text(
                            text = "or continue with",
                            color = secondaryTextColor,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                        HorizontalDivider(modifier = Modifier.weight(1f), color = appBorderColor(AppBorderWeight.Subtle))
                    }

                    Spacer(modifier = Modifier.height(fieldGap))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AuthSocialButton(
                            provider = SocialProvider.Google,
                            label = "Google",
                            compact = compact,
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    Toast.makeText(context, "Logged in via Google successfully!", Toast.LENGTH_SHORT).show()
                                    onLoginSuccess()
                                }
                        )
                        AuthSocialButton(
                            provider = SocialProvider.Facebook,
                            label = "Facebook",
                            compact = compact,
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    Toast.makeText(context, "Logged in via Facebook successfully!", Toast.LENGTH_SHORT).show()
                                    onLoginSuccess()
                                }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(if (compact) 20.dp else 28.dp))

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSignUpClick() },
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = secondaryTextColor)) {
                            append("Don't have an account? ")
                        }
                        withStyle(style = SpanStyle(color = PinkPrimary, fontWeight = FontWeight.Bold)) {
                            append("Sign Up")
                        }
                    },
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

