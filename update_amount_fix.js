const fs = require('fs');
let code = fs.readFileSync('app/src/main/java/com/example/ui/screens/model/ModelSideProfileScreen.kt', 'utf8');

code = code.replace(
    'Text("+ \\${tx.amount} \uD83E\uDE99", color = Color.Green, fontWeight = FontWeight.Bold, fontSize = 15.sp)',
    'Text(tx.amount, color = Color.Green, fontWeight = FontWeight.Bold, fontSize = 15.sp)'
);

fs.writeFileSync('app/src/main/java/com/example/ui/screens/model/ModelSideProfileScreen.kt', code);
