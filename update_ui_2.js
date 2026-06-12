const fs = require('fs');
let code = fs.readFileSync('app/src/main/java/com/example/ui/screens/model/ModelSideProfileScreen.kt', 'utf8');

const targetFavorites = `            } else if (selectedTab == 2) {
                // Favorites Section
                val favorites = viewModel?.favorites?.collectAsState()?.value ?: emptySet()
                if (favorites.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        Text("No saved favorites yet.", color = Color.Gray, fontSize = 16.sp)
                    }
                } else {
                    val models = viewModel?.models?.collectAsState()?.value ?: emptyList()
                    val favoriteModels = models.filter { favorites.contains(it.id) }
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        favoriteModels.forEach { model ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(cardBg, RoundedCornerShape(16.dp))
                                    .border(1.dp, borderColor, RoundedCornerShape(16.dp))
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = "https://i.pravatar.cc/150?u=\${model.id}",
                                    contentDescription = "Profile",
                                    modifier = Modifier.size(60.dp).clip(CircleShape).border(2.dp, PinkPrimary, CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(model.name, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Star, contentDescription = "Rating", tint = Color(0xFFFFD700), modifier = Modifier.size(13.dp))
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(String.format("%.1f", model.rating), color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        Text(" (\${model.reviewsCount})", color = Color.LightGray, fontSize = 12.sp)
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(model.categories.firstOrNull() ?: "", color = Color.LightGray, fontSize = 14.sp)
                                }
                                IconButton(onClick = { viewModel?.toggleFavorite(model.id) }) {
                                    Icon(Icons.Default.Favorite, contentDescription = "Remove Favorite", tint = PinkPrimary)
                                }
                            }
                        }
                    }
                }
            }`;

const newPerformance = `            } else if (selectedTab == 2) {
                // Performance Section (Ratings, Reviews, Transaction History)
                Text("Your Performance", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                
                // Ratings and Reviews
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Rating Widget
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(cardBg, RoundedCornerShape(16.dp))
                            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("4.8", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        Text("Average Rating", color = Color.Gray, fontSize = 12.sp)
                    }
                    
                    // Reviews Widget
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(cardBg, RoundedCornerShape(16.dp))
                            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.RateReview, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("124", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        Text("Total Reviews", color = Color.Gray, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                
                Text("Recent Transactions", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                
                // Transactions History
                val allTxs = viewModel?.walletTransactions?.collectAsState(initial = emptyList())?.value ?: emptyList()
                val recentTxs = allTxs.filter { it.type == "IN" }.take(5) // Just showing income ones
                
                if (recentTxs.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().height(150.dp).background(cardBg, RoundedCornerShape(16.dp)).border(1.dp, borderColor, RoundedCornerShape(16.dp)), contentAlignment = Alignment.Center) {
                        Text("No transactions yet.", color = Color.Gray, fontSize = 14.sp)
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(cardBg, RoundedCornerShape(16.dp))
                            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        recentTxs.forEach { tx ->
                            Row(
                                modifier = Modifier.fillMaxWidth().clickable { showTxDetailsDialog = tx },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier.size(40.dp).background(Color(0xFF3D1B24), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = Color(0xFFFFB800), modifier = Modifier.size(20.dp))
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(tx.title, color = Color.White, fontWeight = FontWeight.Medium, fontSize = 15.sp)
                                    Text(tx.date, color = Color.Gray, fontSize = 13.sp)
                                }
                                Text("+ \${tx.amount} 🪙", color = Color.Green, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                        }
                        
                        if (allTxs.size > 5) {
                            Spacer(modifier = Modifier.height(12.dp))
                            TextButton(
                                onClick = { onViewMoreTransactions?.invoke() },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("View All Transactions", color = PinkPrimary)
                            }
                        }
                    }
                }
            }`;

const newCode = code.replace(targetFavorites, newPerformance);
fs.writeFileSync('app/src/main/java/com/example/ui/screens/model/ModelSideProfileScreen.kt', newCode);
