const fs = require('fs');
let code = fs.readFileSync('app/src/main/java/com/example/ui/screens/model/ModelSideProfileScreen.kt', 'utf8');

// 1. Change Interest -> Categories, and language to multiple selection
code = code.replace(
    'val availableInterests = listOf("Music", "Coding", "Reading", "Travel", "Fitness", "Gaming", "Photography", "Cooking", "Art", "Movies", "Sports", "Nature")',
    'val availableCategories = listOf("Astrology", "Lifestyle", "Fashion", "Fitness", "Gaming", "Music", "Art")'
);
code = code.replace(
    'var selectedInterests by remember { mutableStateOf(setOf("Music", "Coding", "Travel", "Fitness")) }',
    'var selectedCategories by remember { mutableStateOf(setOf("Astrology", "Lifestyle")) }'
);
code = code.replace(
    'var language by remember { mutableStateOf("English") }',
    'var selectedLanguages by remember { mutableStateOf(setOf("English")) }'
);

code = code.replace(
    'var tempLanguage by remember { mutableStateOf(language) }',
    'var tempSelectedLanguages by remember { mutableStateOf(selectedLanguages) }'
);

code = code.replace(
    'var tempSelectedInterests by remember { mutableStateOf(selectedInterests) }',
    'var tempSelectedCategories by remember { mutableStateOf(selectedCategories) }'
);

// 2. Change tabs
code = code.replace(
    'val tabs = listOf("Payout", "Personal Info", "Favorites")',
    'val tabs = listOf("Payout", "Personal Info", "Performance")'
);

fs.writeFileSync('app/src/main/java/com/example/ui/screens/model/ModelSideProfileScreen.kt', code);
