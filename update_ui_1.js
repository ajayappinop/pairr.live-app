const fs = require('fs');
let code = fs.readFileSync('app/src/main/java/com/example/ui/screens/model/ModelSideProfileScreen.kt', 'utf8');

const targetContentToReplace = `                    ExposedDropdownMenuBox(
                        expanded = languageExpanded,
                        onExpandedChange = { languageExpanded = it },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = tempLanguage,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Language", color = Color.Gray) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = languageExpanded) },
                            colors = textFieldColors,
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = languageExpanded,
                            onDismissRequest = { languageExpanded = false }
                        ) {
                            languages.forEach { selectionOption ->
                                DropdownMenuItem(
                                    text = { Text(selectionOption) },
                                    onClick = {
                                        tempLanguage = selectionOption
                                        languageExpanded = false
                                    }
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Interests & Hobbies (Select Multiple)",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                            .background(cardBg)
                            .padding(12.dp)
                    ) {
                        val chunks = availableInterests.chunked(3)
                        chunks.forEach { chunk ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                chunk.forEach { interest ->
                                    val isSelected = tempSelectedInterests.contains(interest)
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (isSelected) PinkPrimary.copy(alpha = 0.2f) else borderColor)
                                            .border(1.dp, if (isSelected) PinkPrimary else Color.Transparent, RoundedCornerShape(10.dp))
                                            .clickable {
                                                tempSelectedInterests = if (isSelected) {
                                                    tempSelectedInterests - interest
                                                } else {
                                                    tempSelectedInterests + interest
                                                }
                                            }
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = interest,
                                            color = if (isSelected) PinkPrimary else Color.White,
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                }
                                if (chunk.size < 3) {
                                    repeat(3 - chunk.size) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }`;

const newContent = `                    Text(
                        text = "Languages (Select Multiple)",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                            .background(cardBg)
                            .padding(12.dp)
                    ) {
                        val chunks = languages.chunked(3)
                        chunks.forEach { chunk ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                chunk.forEach { lang ->
                                    val isSelected = tempSelectedLanguages.contains(lang)
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (isSelected) PinkPrimary.copy(alpha = 0.2f) else borderColor)
                                            .border(1.dp, if (isSelected) PinkPrimary else Color.Transparent, RoundedCornerShape(10.dp))
                                            .clickable {
                                                tempSelectedLanguages = if (isSelected) {
                                                    tempSelectedLanguages - lang
                                                } else {
                                                    tempSelectedLanguages + lang
                                                }
                                            }
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = lang,
                                            color = if (isSelected) PinkPrimary else Color.White,
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                }
                                if (chunk.size < 3) {
                                    repeat(3 - chunk.size) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Categories (Select Multiple)",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                            .background(cardBg)
                            .padding(12.dp)
                    ) {
                        val chunks = availableCategories.chunked(3)
                        chunks.forEach { chunk ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                chunk.forEach { category ->
                                    val isSelected = tempSelectedCategories.contains(category)
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (isSelected) PinkPrimary.copy(alpha = 0.2f) else borderColor)
                                            .border(1.dp, if (isSelected) PinkPrimary else Color.Transparent, RoundedCornerShape(10.dp))
                                            .clickable {
                                                tempSelectedCategories = if (isSelected) {
                                                    tempSelectedCategories - category
                                                } else {
                                                    tempSelectedCategories + category
                                                }
                                            }
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = category,
                                            color = if (isSelected) PinkPrimary else Color.White,
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                }
                                if (chunk.size < 3) {
                                    repeat(3 - chunk.size) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }`;

const newCode = code.replace(targetContentToReplace, newContent);
fs.writeFileSync('app/src/main/java/com/example/ui/screens/model/ModelSideProfileScreen.kt', newCode);
