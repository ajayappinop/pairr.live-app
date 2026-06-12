const fs = require('fs');

// 1. Update MainViewModel.kt
let vmCode = fs.readFileSync('app/src/main/java/com/example/MainViewModel.kt', 'utf8');

vmCode = vmCode.replace(
    'emailOrPhone: String,',
    'phone: String,'
);
vmCode = vmCode.replace(
    'val normalized = emailOrPhone.trim().lowercase()',
    'val phoneClean = phone.trim()\n        val normalized = if (phoneClean.contains("@")) phoneClean else "$phoneClean@dummy.phone"'
);

vmCode = vmCode.replace(
    'fun loginUser(emailOrPhone: String, passwordEntered: String, onResult: (Boolean, String?) -> Unit = { _, _ -> }) {',
    'fun loginUser(phone: String, passwordEntered: String, onResult: (Boolean, String?) -> Unit = { _, _ -> }) {'
);

vmCode = vmCode.replace(
    'val normalized = emailOrPhone.trim().lowercase()',
    'val phoneClean = phone.trim()\n        val normalized = if (phoneClean.contains("@")) phoneClean else "$phoneClean@dummy.phone"'
);

vmCode = vmCode.replace(
    'onResult(false, "Email and password cannot be blank")',
    'onResult(false, "Phone and password cannot be blank")'
);

fs.writeFileSync('app/src/main/java/com/example/MainViewModel.kt', vmCode);


// 2. Update SignUpScreen.kt
let signupCode = fs.readFileSync('app/src/main/java/com/example/ui/screens/SignUpScreen.kt', 'utf8');

signupCode = signupCode.replaceAll('emailOrPhone', 'phone');
signupCode = signupCode.replaceAll('Email or Phone Number', 'Phone Number');
signupCode = signupCode.replaceAll('Email or Phone', 'Phone');
signupCode = signupCode.replaceAll('Icons.Outlined.Email', 'Icons.Outlined.Phone');
// The validation logic for email needs to change
let signupOldVal = `                    if (phone.isBlank()) {
                        phoneError = "Required"
                        isValid = false
                    } else if (phone.contains("@") && !android.util.Patterns.EMAIL_ADDRESS.matcher(phone).matches()) {
                        phoneError = "Invalid email format"
                        isValid = false
                    } else if (!phone.contains("@") && phone.length < 6) {
                        phoneError = "Please enter a valid phone number or email"
                        isValid = false
                    } else {
                        phoneError = null
                    }`;
let signupNewVal = `                    if (phone.isBlank()) {
                        phoneError = "Required"
                        isValid = false
                    } else if (phone.length < 6) {
                        phoneError = "Please enter a valid phone number"
                        isValid = false
                    } else {
                        phoneError = null
                    }`;
signupCode = signupCode.replace(signupOldVal, signupNewVal);
signupCode = signupCode.replace('import androidx.compose.material.icons.outlined.Email', 'import androidx.compose.material.icons.outlined.Phone');

fs.writeFileSync('app/src/main/java/com/example/ui/screens/SignUpScreen.kt', signupCode);


// 3. Update LoginScreen.kt
let loginCode = fs.readFileSync('app/src/main/java/com/example/ui/screens/LoginScreen.kt', 'utf8');

loginCode = loginCode.replaceAll('emailOrPhone', 'phone');
loginCode = loginCode.replaceAll('Email or Phone Number', 'Phone Number');
loginCode = loginCode.replaceAll('Email or Phone', 'Phone');
loginCode = loginCode.replaceAll('Icons.Outlined.Email', 'Icons.Outlined.Phone');
// The validation logic for email needs to change
let loginOldVal = `                    if (phone.isBlank()) {
                        phoneError = "Required"
                        isValid = false
                    } else if (phone.contains("@") && !android.util.Patterns.EMAIL_ADDRESS.matcher(phone).matches()) {
                        phoneError = "Invalid email format"
                        isValid = false
                    } else if (!phone.contains("@") && phone.length < 6) {
                        phoneError = "Please enter a valid phone number or email"
                        isValid = false
                    } else {
                        phoneError = null
                    }`;
let loginNewVal = `                    if (phone.isBlank()) {
                        phoneError = "Required"
                        isValid = false
                    } else if (phone.length < 6) {
                        phoneError = "Please enter a valid phone number"
                        isValid = false
                    } else {
                        phoneError = null
                    }`;
loginCode = loginCode.replace(loginOldVal, loginNewVal);
loginCode = loginCode.replace('import androidx.compose.material.icons.outlined.Email', 'import androidx.compose.material.icons.outlined.Phone');
loginCode = loginCode.replaceAll('Invalid email/phone or password', 'Invalid phone or password');
loginCode = loginCode.replaceAll('forgotEmail', 'forgotPhone');

fs.writeFileSync('app/src/main/java/com/example/ui/screens/LoginScreen.kt', loginCode);
