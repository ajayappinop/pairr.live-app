const fs = require('fs');

try {
    // 1. Update MainViewModel.kt
    let vmCode = fs.readFileSync('app/src/main/java/com/example/MainViewModel.kt', 'utf8');

    // Add Imports
    const imports = `import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.AuthResult
import com.google.android.gms.tasks.Task\n`;
    vmCode = vmCode.replace('import androidx.lifecycle.ViewModel', imports + 'import androidx.lifecycle.ViewModel');

    // Add firebaseAuth lazy property
    const authProp = `class MainViewModel : ViewModel() {
    private val firebaseAuth: FirebaseAuth? by lazy {
        try {
            FirebaseAuth.getInstance()
        } catch (e: Exception) {
            android.util.Log.e("Firebase", "google-services.json is missing or not configured.")
            null
        }
    }`;
    vmCode = vmCode.replace('class MainViewModel : ViewModel() {', authProp);

    // Update registerUser
    const oldRegister = `    fun registerUser(
        name: String, 
        emailOrPhone: String, 
        passwordEntered: String, 
        isModel: Boolean = false,
        gender: String = "Male",
        age: String = "",
        audioRate: String = "",
        videoRate: String = ""
    ): Boolean {
        val normalized = emailOrPhone.trim().lowercase()
        if (normalized.isBlank() || passwordEntered.isBlank()) return false
        
        val accounts = _userAccounts.value.toMutableMap()
        accounts[normalized] = passwordEntered
        _userAccounts.value = accounts

        val names = _userNames.value.toMutableMap()
        names[normalized] = name.trim()
        _userNames.value = names

        val modes = _userModes.value.toMutableMap()
        modes[normalized] = isModel
        _userModes.value = modes
        
        _isModelMode.value = isModel
        
        if (isModel) {
            audioRate.toIntOrNull()?.let { setModelAudioRate(it) }
            videoRate.toIntOrNull()?.let { setModelVideoRate(it) }
        }
        
        return true
    }`;

    const newRegister = `    fun registerUser(
        name: String, 
        emailOrPhone: String, 
        passwordEntered: String, 
        isModel: Boolean = false,
        gender: String = "Male",
        age: String = "",
        audioRate: String = "",
        videoRate: String = "",
        onResult: (Boolean, String?) -> Unit = { _, _ -> }
    ) {
        val normalized = emailOrPhone.trim().lowercase()
        if (normalized.isBlank() || passwordEntered.isBlank()) {
            onResult(false, "Email and password cannot be blank")
            return
        }

        val performLocalRegistration = {
            val accounts = _userAccounts.value.toMutableMap()
            accounts[normalized] = passwordEntered
            _userAccounts.value = accounts

            val names = _userNames.value.toMutableMap()
            names[normalized] = name.trim()
            _userNames.value = names

            val modes = _userModes.value.toMutableMap()
            modes[normalized] = isModel
            _userModes.value = modes
            
            _isModelMode.value = isModel
            
            if (isModel) {
                audioRate.toIntOrNull()?.let { setModelAudioRate(it) }
                videoRate.toIntOrNull()?.let { setModelVideoRate(it) }
            }
            onResult(true, null)
        }

        if (firebaseAuth != null) {
            firebaseAuth.createUserWithEmailAndPassword(normalized, passwordEntered)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        performLocalRegistration()
                    } else {
                        onResult(false, task.exception?.localizedMessage ?: "Firebase error. Make sure google-services.json is configured.")
                    }
                }
        } else {
            performLocalRegistration()
        }
    }`;

    vmCode = vmCode.replace(oldRegister, newRegister);

    // Update loginUser
    const oldLogin = `    fun loginUser(emailOrPhone: String, passwordEntered: String): Boolean {
        val normalized = emailOrPhone.trim().lowercase()
        val registeredPassword = _userAccounts.value[normalized]
        val success = registeredPassword == passwordEntered
        if (success) {
            _isModelMode.value = _userModes.value[normalized] == true
        }
        return success
    }`;

    const newLogin = `    fun loginUser(emailOrPhone: String, passwordEntered: String, onResult: (Boolean, String?) -> Unit = { _, _ -> }) {
        val normalized = emailOrPhone.trim().lowercase()
        
        val performLocalLogin = {
            val registeredPassword = _userAccounts.value[normalized]
            val success = registeredPassword == passwordEntered
            if (success) {
                _isModelMode.value = _userModes.value[normalized] == true
                onResult(true, null)
            } else {
                onResult(false, "Invalid credentials")
            }
        }

        if (firebaseAuth != null) {
            firebaseAuth.signInWithEmailAndPassword(normalized, passwordEntered)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        performLocalLogin()
                    } else {
                        onResult(false, task.exception?.localizedMessage ?: "Firebase error. Make sure google-services.json is configured.")
                    }
                }
        } else {
            performLocalLogin()
        }
    }`;

    vmCode = vmCode.replace(oldLogin, newLogin);
    fs.writeFileSync('app/src/main/java/com/example/MainViewModel.kt', vmCode);
    console.log('MainViewModel.kt updated successfully.');

    // 2. Update SignUpScreen.kt
    let signupCode = fs.readFileSync('app/src/main/java/com/example/ui/screens/SignUpScreen.kt', 'utf8');

    const oldSignupCall = `if (isValid) {
                        viewModel?.registerUser(name, emailOrPhone, password, isModel, gender, age, audioRate, videoRate)
                        Toast.makeText(context, "Account Created Successfully! Welcome to Pairr.", Toast.LENGTH_LONG).show()
                        onSignUpSuccess()
                    }`;

    const newSignupCall = `if (isValid) {
                        viewModel?.registerUser(name, emailOrPhone, password, isModel, gender, age, audioRate, videoRate) { success, errorMsg ->
                            if (success) {
                                Toast.makeText(context, "Account Created Successfully! Welcome to Pairr.", Toast.LENGTH_LONG).show()
                                onSignUpSuccess()
                            } else {
                                Toast.makeText(context, errorMsg ?: "Registration failed", Toast.LENGTH_LONG).show()
                            }
                        }
                    }`;

    signupCode = signupCode.replace(oldSignupCall, newSignupCall);
    fs.writeFileSync('app/src/main/java/com/example/ui/screens/SignUpScreen.kt', signupCode);
    console.log('SignUpScreen.kt updated successfully.');

    // 3. Update LoginScreen.kt
    let loginCode = fs.readFileSync('app/src/main/java/com/example/ui/screens/LoginScreen.kt', 'utf8');

    const oldLoginCall = `                        val verified = viewModel == null || viewModel.loginUser(emailOrPhone, password)
                        if (verified) {
                            Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
                            onLoginSuccess()
                        } else {
                            errorMessage = "Invalid email/phone or password"
                        }`;

    const newLoginCall = `                        if (viewModel != null) {
                            viewModel.loginUser(emailOrPhone, password) { success, errorMsg ->
                                if (success) {
                                    Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
                                    onLoginSuccess()
                                } else {
                                    errorMessage = errorMsg ?: "Invalid email/phone or password"
                                }
                            }
                        } else {
                            onLoginSuccess()
                        }`;

    loginCode = loginCode.replace(oldLoginCall, newLoginCall);
    fs.writeFileSync('app/src/main/java/com/example/ui/screens/LoginScreen.kt', loginCode);
    console.log('LoginScreen.kt updated successfully.');

} catch (e) {
    console.error('Error applying script: ', e);
}
