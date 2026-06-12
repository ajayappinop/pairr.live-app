const fs = require('fs');
let code = fs.readFileSync('app/src/main/java/com/example/ui/screens/model/ModelSideProfileScreen.kt', 'utf8');

const target = `            if (selectedInterests.isNotEmpty()) {
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
                Spacer(modifier = Modifier.height(10.dp))
            }`;

const newHeader = target.replaceAll('selectedInterests', 'selectedCategories').replaceAll('interest', 'category');

code = code.replace(target, newHeader);

fs.writeFileSync('app/src/main/java/com/example/ui/screens/model/ModelSideProfileScreen.kt', code);
