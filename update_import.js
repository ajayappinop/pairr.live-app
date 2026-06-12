const fs = require('fs');
let code = fs.readFileSync('app/src/main/java/com/example/ui/screens/model/ModelSideProfileScreen.kt', 'utf8');

code = code.replace(
    'import androidx.compose.material.icons.filled.Add',
    'import androidx.compose.material.icons.filled.Add\nimport androidx.compose.material.icons.filled.RateReview'
);

fs.writeFileSync('app/src/main/java/com/example/ui/screens/model/ModelSideProfileScreen.kt', code);
