const fs = require('fs');

function processFile(path) {
    let sourcecode = fs.readFileSync(path, 'utf8');
    
    // Remove tempEmail declaration
    sourcecode = sourcecode.replace(/    var tempEmail by remember \{ mutableStateOf\(email\) \}\n/, '');
    
    // Remove emailError
    sourcecode = sourcecode.replace(/    var emailError by remember \{ mutableStateOf<String\?>\(null\) \}\n/, '');
    
    // Remove Email Field UI
    const emailFieldRegex = /                    \/\/ Email Field\n                    OutlinedTextField\([\s\S]*?supportingText = \{ emailError\?\.let \{ Text\(it, color = MaterialTheme\.colorScheme\.error\) \} \}\n                    \)\n\n                    Spacer\(modifier = Modifier\.height\(16\.dp\)\)\n/;
    const emailFieldMatch = sourcecode.match(emailFieldRegex);
    if (emailFieldMatch) {
       sourcecode = sourcecode.replace(emailFieldMatch[0], '');
    }

    // Remove Validation Logic
    const emailValidationRegex = /                            if \(tempEmail\.isBlank\(\) \|\| !android\.util\.Patterns\.EMAIL_ADDRESS\.matcher\(tempEmail\)\.matches\(\)\) \{\n                                emailError = "Please enter a valid email"\n                                isValid = false\n                            \} else \{\n                                emailError = null\n                            \}\n\n/;
    const emailValidationMatch = sourcecode.match(emailValidationRegex);
    if (emailValidationMatch) {
       sourcecode = sourcecode.replace(emailValidationMatch[0], '');
    }

    // Remove Variable Assignment
    sourcecode = sourcecode.replace(/                                email = tempEmail\n/, '');

    fs.writeFileSync(path, sourcecode);
    console.log("Updated " + path);
}

processFile('app/src/main/java/com/example/ui/screens/model/ModelSideProfileScreen.kt');
processFile('app/src/main/java/com/example/ui/screens/UserProfileScreen.kt');
