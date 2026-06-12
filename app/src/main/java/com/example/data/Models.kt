package com.example.data

const val SAMPLE_INTRO_VIDEO_1 =
    "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"
const val SAMPLE_INTRO_VIDEO_2 =
    "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4"
const val SAMPLE_INTRO_VIDEO_3 =
    "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyrides.mp4"
const val SAMPLE_INTRO_VIDEO_4 =
    "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerMeltdowns.mp4"

fun modelGalleryImageUrl(modelId: String, index: Int): String =
    "https://picsum.photos/seed/pairr_${modelId}_$index/600/800"

fun defaultModelImageUrls(modelId: String, count: Int = 5): List<String> =
    (1..count.coerceIn(1, 5)).map { modelGalleryImageUrl(modelId, it) }

fun AppModel.storedImageUrls(): List<String> = imageUrls.take(5)

fun AppModel.displayImageUrls(): List<String> =
    storedImageUrls().ifEmpty { defaultModelImageUrls(id) }

fun AppModel.displayIntroVideoUrl(): String? =
    introVideoUrl?.takeIf { it.isNotBlank() }

fun AppModel.displayProfilePhotoUrl(): String =
    profilePhotoUrl?.takeIf { it.isNotBlank() }
        ?: storedImageUrls().firstOrNull()
        ?: displayImageUrls().firstOrNull()
        ?: "https://i.pravatar.cc/600?u=$id"

/** Image shown on user-facing cards when the model has no intro video. */
fun AppModel.displayCardImageUrl(): String = displayProfilePhotoUrl()

data class AppModel(
    val id: String = "",
    val name: String = "",
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
        imageUrls = defaultModelImageUrls("1"),
        introVideoUrl = SAMPLE_INTRO_VIDEO_1
    ),
    AppModel(
        id = "2",
        name = "Priya Sharma",
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
        imageUrls = defaultModelImageUrls("2", 4),
        introVideoUrl = SAMPLE_INTRO_VIDEO_2
    ),
    AppModel(
        id = "3",
        name = "Riya Patel",
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
        imageUrls = defaultModelImageUrls("3"),
        introVideoUrl = SAMPLE_INTRO_VIDEO_3
    ),
    AppModel(
        id = "4",
        name = "Neha Singh",
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
        imageUrls = defaultModelImageUrls("4", 3),
        introVideoUrl = SAMPLE_INTRO_VIDEO_4
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

data class UserProfile(
    val id: String,
    val name: String,
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
