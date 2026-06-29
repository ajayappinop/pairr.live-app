package com.example.data

fun modelGalleryImageUrl(modelId: String, index: Int): String =
    "https://picsum.photos/seed/pairr_${modelId}_$index/600/800"

fun defaultModelProfilePhotoUrl(modelId: String): String =
    modelGalleryImageUrl(modelId, 1)

fun AppModel.storedImageUrls(): List<String> = emptyList()

fun AppModel.displayImageUrls(): List<String> =
    displayProfilePhotoUrl().takeIf { it.isNotBlank() }?.let { listOf(it) } ?: emptyList()

fun AppModel.displayIntroVideoUrl(): String? = null

fun AppModel.displayProfilePhotoUrl(): String =
    profilePhotoUrl?.takeIf { it.isNotBlank() }
        ?: "https://i.pravatar.cc/600?u=$id"

/** Image shown on user-facing cards and lists. */
fun AppModel.displayCardImageUrl(): String = displayProfilePhotoUrl()

/** Public handle shown to users — never the model's real full name. */
fun AppModel.publicUsername(): String =
    username.takeIf { it.isNotBlank() } ?: "model_$id"

/** Public handle shown to models and in calls — never the user's real full name. */
fun UserProfile.publicUsername(): String =
    username.takeIf { it.isNotBlank() } ?: name.takeIf { it.isNotBlank() }?.lowercase()?.replace(" ", "_")
        ?: "user_${id.substringBefore("@").take(8)}"

/** Sort order for home browse list: Online → Offline → Busy. */
fun AppModel.browseSortOrder(): Int = when (status) {
    "Online" -> 0
    "Offline" -> 1
    "Busy" -> 2
    else -> 3
}

/** Only online models can receive calls from the browse feed or detail screen. */
fun AppModel.canTalkNow(): Boolean = status == "Online"

fun formatReviewCount(count: Int): String = when {
    count >= 1_000_000 -> "${count / 1_000_000}M+"
    count >= 1_000 -> "${count / 1_000}K+"
    else -> count.toString()
}

fun List<String>.browseLanguageLabel(): String =
    joinToString(" - ") { lang ->
        lang.split(" ").firstOrNull()?.take(3)?.replaceFirstChar { it.uppercase() } ?: lang
    }.ifBlank { "—" }

data class AppModel(
    val id: String = "",
    val name: String = "",
    val username: String = "",
    val isOnline: Boolean = false,
    val isFeatured: Boolean = false,
    val bio: String = "",
    val languages: List<String> = emptyList(),
    val categories: List<String> = emptyList(),
    val audioPrice: Int = 0,
    val videoPrice: Int = 0,
    val rating: Float = 0f,
    val reviewsCount: Int = 0,
    val status: String = "Online", // "Online", "Offline", "Busy"
    val imageUrls: List<String> = emptyList(),
    val introVideoUrl: String? = null,
    val profilePhotoUrl: String? = null
)

val mockModels = listOf(
    AppModel(
        id = "1",
        name = "Aisha Khan",
        username = "alessia_beauty",
        isOnline = true,
        isFeatured = true,
        bio = "Friendly companion for casual talks. I love movies and music.",
        languages = listOf("English", "Hindi"),
        categories = listOf("Casual Talk", "Movies"),
        audioPrice = 20,
        videoPrice = 50,
        rating = 4.8f,
        reviewsCount = 120,
        status = "Online",
        profilePhotoUrl = defaultModelProfilePhotoUrl("1")
    ),
    AppModel(
        id = "2",
        name = "Priya Sharma",
        username = "priya_sharma",
        isOnline = false,
        isFeatured = false,
        bio = "Here to listen to you and give you the best advice.",
        languages = listOf("English", "Telugu"),
        categories = listOf("Advice", "Listening"),
        audioPrice = 15,
        videoPrice = 40,
        rating = 4.5f,
        reviewsCount = 85,
        status = "Offline",
        profilePhotoUrl = defaultModelProfilePhotoUrl("2")
    ),
    AppModel(
        id = "3",
        name = "Riya Patel",
        username = "riya_patel",
        isOnline = true,
        isFeatured = true,
        bio = "Let's talk about life, universe and everything.",
        languages = listOf("English", "Gujarati"),
        categories = listOf("Deep Talk"),
        audioPrice = 30,
        videoPrice = 60,
        rating = 4.9f,
        reviewsCount = 200,
        status = "Busy",
        profilePhotoUrl = defaultModelProfilePhotoUrl("3")
    ),
    AppModel(
        id = "4",
        name = "Neha Singh",
        username = "neha_singh",
        isOnline = true,
        isFeatured = false,
        bio = "Love to sing and discuss arts.",
        languages = listOf("English", "Hindi"),
        categories = listOf("Arts", "Music"),
        audioPrice = 25,
        videoPrice = 55,
        rating = 4.7f,
        reviewsCount = 45,
        status = "Online",
        profilePhotoUrl = defaultModelProfilePhotoUrl("4")
    )
)

data class CallHistory(
    val id: String,
    val date: String,
    val duration: String,
    val amountSpent: Int,
    val callType: String,
    val modelName: String
)

data class ChatMessage(
    val id: String,
    val content: String,
    val isFromUser: Boolean,
    val timestamp: String,
    val timestampMillis: Long = 0L
)

data class ChatThread(
    val id: String,
    val participantName: String,
    val participantAvatarUrl: String,
    val lastMessage: String,
    val lastMessageTime: String,
    val unreadCount: Int = 0,
    val isOnline: Boolean = false,
    val isModelThread: Boolean = true,
    val userId: String = "",
    val modelId: String = "",
    val userName: String = "",
    val modelName: String = "",
    val userAvatarUrl: String = "",
    val modelAvatarUrl: String = "",
    val userUnreadCount: Int = 0,
    val modelUnreadCount: Int = 0,
    val lastMessageMillis: Long = 0L
)

val mockHistory = listOf(
    CallHistory("1", "10 June, 10:00 AM", "15:00", 300, "Audio", "Aisha Khan"),
    CallHistory("2", "9 June, 08:30 PM", "05:00", 250, "Video", "Riya Patel")
)

data class ModelReview(
    val id: String = "",
    val modelId: String = "",
    val reviewerName: String = "",
    val rating: Int = 0,
    val reviewText: String = "",
    val date: String = "",
    val isVideo: Boolean = false
)

data class Wallet(
    val balance: Int = 1500,
    val audioBalance: Int = 750,
    val videoBalance: Int = 750
)

/** Compact label for top bars, e.g. 1500 → "1.5k". */
fun Wallet.formattedBalanceCompact(): String = balance.formatTokenCountCompact()

/** Full label for wallet cards, e.g. 1500 → "1,500". */
fun Wallet.formattedBalanceFull(): String = String.format("%,d", balance)

fun Int.formatTokenCountCompact(): String = when {
    this >= 1_000_000 -> formatCompactDivisor(1_000_000.0, "M")
    this >= 1_000 -> formatCompactDivisor(1_000.0, "k")
    else -> toString()
}

private fun Int.formatCompactDivisor(divisor: Double, suffix: String): String {
    val scaled = this / divisor
    val rounded = (scaled * 10).toInt() / 10.0
    return if (rounded == rounded.toLong().toDouble()) {
        "${rounded.toLong()}$suffix"
    } else {
        String.format("%.1f$suffix", rounded)
    }
}

data class UserProfile(
    val id: String,
    val name: String,
    val username: String = "",
    val avatarUrl: String,
    val bio: String = "",
    val age: Int = 0,
    val gender: String = "",
    val location: String = "",
    val interests: List<String> = emptyList(),
    val memberSince: String = "",
    val totalCalls: Int = 0,
    val isOnline: Boolean = false
)
