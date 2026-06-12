const fs = require('fs');
let code = fs.readFileSync('app/src/main/java/com/example/ui/screens/model/ModelSideProfileScreen.kt', 'utf8');

// fix walletTransactions
code = code.replace(
    'val allTxs = viewModel?.walletTransactions?.collectAsState(initial = emptyList())?.value ?: emptyList()',
    'val allTxs = viewModel?.transactions?.collectAsState(initial = emptyList())?.value ?: emptyList()'
);

code = code.replace(
    'val recentTxs = allTxs.filter { it.type == "IN" }.take(5) // Just showing income ones',
    'val recentTxs = allTxs.filter { it.isPositive }.take(5) // Just showing income ones'
);

// fix missing selectedInterests logic in Profile Header
const oldInterestsHeader = `            if (selectedInterests.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    val chunks = selectedInterests.toList().chunked(4)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        chunks.forEach { chunk ->
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                chunk.forEach { interest ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(PinkPrimary.copy(alpha = 0.15f))
                                            .border(1.dp, PinkPrimary.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = interest,
                                            color = PinkPrimary,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }`;

const newCategoriesHeader = `            if (selectedCategories.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    val chunks = selectedCategories.toList().chunked(4)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        chunks.forEach { chunk ->
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                chunk.forEach { category ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(PinkPrimary.copy(alpha = 0.15f))
                                            .border(1.dp, PinkPrimary.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = category,
                                            color = PinkPrimary,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }`;

code = code.replace(oldInterestsHeader, newCategoriesHeader);

// Fix save block
code = code.replace('language = tempLanguage', 'selectedLanguages = tempSelectedLanguages');
code = code.replace('selectedInterests = tempSelectedInterests', 'selectedCategories = tempSelectedCategories');


fs.writeFileSync('app/src/main/java/com/example/ui/screens/model/ModelSideProfileScreen.kt', code);
