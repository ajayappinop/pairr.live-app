package com.example

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.AuthResult
import com.google.android.gms.tasks.Task
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.Wallet
import com.example.data.ChatMessage
import com.example.data.ChatThread
import com.example.data.ChatRepository
import com.example.data.UserProfile
import com.example.data.displayImageUrls
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class WalletTransaction(
    val id: String,
    val title: String,
    val amount: String,
    val date: String,
    val isPositive: Boolean
)

data class BlockedUser(
    val id: String,
    val name: String,
    val avatarUrl: String,
    val blockedAt: String
)

data class RandomPeerMatch(
    val userId: String,
    val name: String,
    val avatarUrl: String
)

data class CallBooking(
    val id: String,
    val modelId: String,
    val modelName: String,
    val modelAvatarUrl: String,
    val isVideo: Boolean,
    val date: String,
    val timeSlot: String,
    val cost: Int,
    val userId: String = "",
    val userName: String = "",
    val userAvatarUrl: String = "",
    val status: String = "Scheduled" // "Scheduled", "Accepted", "Completed", "Cancelled"
)

data class ModelReview(
    val id: String,
    val modelId: String,
    val reviewerName: String,
    val rating: Int,
    val reviewText: String,
    val date: String,
    val isVideo: Boolean
)

data class ModelCallEarning(
    val id: String,
    val modelId: String,
    val callerName: String,
    val isVideo: Boolean,
    val duration: String,
    val date: String,
    val amountEarned: Int,
    val status: String = "Completed"
)

data class AppNotification(
    val id: String,
    val title: String,
    val message: String,
    val time: String,
    val isRead: Boolean = false
)

class MainViewModel : ViewModel() {
    private val firebaseAuth: FirebaseAuth? by lazy {
        try {
            FirebaseAuth.getInstance()
        } catch (e: Exception) {
            android.util.Log.e("Firebase", "google-services.json is missing or not configured.")
            null
        }
    }

    private val firestore: FirebaseFirestore? by lazy {
        try {
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            android.util.Log.e("Firebase", "Firestore initialization failed: ${e.message}")
            null
        }
    }

    private val chatRepository by lazy { ChatRepository(firestore) }

    private val _currentUserId = MutableStateFlow<String?>(null)
    val currentUserId: StateFlow<String?> = _currentUserId.asStateFlow()

    private val _linkedModelId = MutableStateFlow<String?>(null)

    private var activeThreadId: String? = null

    private val userModelLinks = mapOf(
        "model_female@pairr.live" to "1"
    )

    // Model Mode State
    private val _isModelMode = MutableStateFlow(false)
    val isModelMode: StateFlow<Boolean> = _isModelMode.asStateFlow()

    private val _isDarkMode = MutableStateFlow(true)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    private val _modelAudioRate = MutableStateFlow(20)
    val modelAudioRate: StateFlow<Int> = _modelAudioRate.asStateFlow()

    private val _modelVideoRate = MutableStateFlow(50)
    val modelVideoRate: StateFlow<Int> = _modelVideoRate.asStateFlow()

    private val _modelAvailability = MutableStateFlow("Online")
    val modelAvailability: StateFlow<String> = _modelAvailability.asStateFlow()

    private val _modelDailyEarnings = MutableStateFlow(120)
    val modelDailyEarnings: StateFlow<Int> = _modelDailyEarnings.asStateFlow()

    private val _modelWeeklyEarnings = MutableStateFlow(840)
    val modelWeeklyEarnings: StateFlow<Int> = _modelWeeklyEarnings.asStateFlow()

    private val _modelMonthlyEarnings = MutableStateFlow(3200)
    val modelMonthlyEarnings: StateFlow<Int> = _modelMonthlyEarnings.asStateFlow()

    private val _modelLifetimeEarnings = MutableStateFlow(15000)
    val modelLifetimeEarnings: StateFlow<Int> = _modelLifetimeEarnings.asStateFlow()
    
    fun setModelAudioRate(rate: Int) { _modelAudioRate.value = rate }
    fun setModelVideoRate(rate: Int) { _modelVideoRate.value = rate }
    fun setModelAvailability(status: String) { _modelAvailability.value = status }
    
    fun requestWithdrawal(amount: Int) {
        if (amount > 0 && amount <= _modelDailyEarnings.value) {
            _modelDailyEarnings.value -= amount
        }
    }

    private val _models = MutableStateFlow<List<com.example.data.AppModel>>(com.example.data.mockModels)

    val models: StateFlow<List<com.example.data.AppModel>> = _models.asStateFlow()

    private val _reviews = MutableStateFlow<List<ModelReview>>(
        listOf(
            ModelReview("r1", "1", "Rahul S.", 5, "Amazing conversation! Very friendly.", "10 Jun 2026", true),
            ModelReview("r2", "1", "Amit P.", 4, "Enjoyed talking to her. Sound quality was great.", "09 Jun 2026", false),
            ModelReview("r3", "3", "Sagar K.", 5, "Incredible insights about the universe. Must connect!", "08 Jun 2026", true)
        )
    )
    val reviews: StateFlow<List<ModelReview>> = _reviews.asStateFlow()

    private val _modelEarnings = MutableStateFlow<List<ModelCallEarning>>(
        listOf(
            ModelCallEarning("e1", "1", "Rahul S.", true, "15:00", "10 Jun 2026, 10:15 AM", 300),
            ModelCallEarning("e2", "1", "Amit P.", false, "12:30", "09 Jun 2026, 08:35 PM", 250),
            ModelCallEarning("e3", "1", "Vikram D.", true, "08:45", "08 Jun 2026, 06:20 PM", 180),
            ModelCallEarning("e4", "1", "Karan W.", false, "20:00", "07 Jun 2026, 11:00 AM", 400),
            ModelCallEarning("e5", "3", "Sagar K.", true, "25:00", "08 Jun 2026, 09:10 PM", 360),
            ModelCallEarning("e6", "3", "Neha R.", false, "10:00", "06 Jun 2026, 04:45 PM", 200)
        )
    )
    val modelEarnings: StateFlow<List<ModelCallEarning>> = _modelEarnings.asStateFlow()

    private val _modelProfileUsername = MutableStateFlow("alessia_beauty")
    val modelProfileUsername: StateFlow<String> = _modelProfileUsername.asStateFlow()

    private val _modelProfileFullName = MutableStateFlow("Alessia K.")
    val modelProfileFullName: StateFlow<String> = _modelProfileFullName.asStateFlow()

    fun setModelProfileUsername(username: String) {
        val trimmed = username.trim()
        _modelProfileUsername.value = trimmed
        updateCurrentModel { it.copy(username = trimmed) }
    }

    fun setModelProfileFullName(fullName: String) {
        _modelProfileFullName.value = fullName.trim()
    }

    fun updateModelProfileIdentity(username: String, fullName: String) {
        setModelProfileUsername(username)
        setModelProfileFullName(fullName)
    }

    private val _notifications = MutableStateFlow(
        listOf(
            AppNotification(
                id = "n1",
                title = "Welcome Bonus 🎁",
                message = "You received 250 bonus tokens on your first visit. Claim them from the Token Store!",
                time = "Just now"
            ),
            AppNotification(
                id = "n2",
                title = "Aisha Khan is live",
                message = "Your favorite model just went online. Start a chat or call now.",
                time = "12 min ago"
            ),
            AppNotification(
                id = "n3",
                title = "New message",
                message = "Riya Patel sent you a message: \"Are you free for a call later?\"",
                time = "1 hour ago"
            ),
            AppNotification(
                id = "n4",
                title = "Video pack offer",
                message = "Save up to 35% on premium video token packs — limited time only.",
                time = "Yesterday",
                isRead = true
            )
        )
    )
    val notifications: StateFlow<List<AppNotification>> = _notifications.asStateFlow()

    private val _modelNotifications = MutableStateFlow(
        listOf(
            AppNotification(
                id = "mn1",
                title = "New call request 📞",
                message = "Rahul Sharma requested a video call — 150 tokens/min.",
                time = "2 min ago"
            ),
            AppNotification(
                id = "mn2",
                title = "Earnings credited 💰",
                message = "You earned 300 tokens from your call with Rahul S.",
                time = "15 min ago"
            ),
            AppNotification(
                id = "mn3",
                title = "New fan message",
                message = "Amit P. sent you a message: \"Hey, are you available?\"",
                time = "45 min ago"
            ),
            AppNotification(
                id = "mn4",
                title = "Profile featured ⭐",
                message = "Your profile was highlighted in Recommended For You today.",
                time = "Yesterday",
                isRead = true
            )
        )
    )
    val modelNotifications: StateFlow<List<AppNotification>> = _modelNotifications.asStateFlow()

    val unreadNotificationCount: Int
        get() = _notifications.value.count { !it.isRead }

    val unreadModelNotificationCount: Int
        get() = _modelNotifications.value.count { !it.isRead }

    fun markNotificationRead(notificationId: String) {
        _notifications.update { list ->
            list.map { if (it.id == notificationId) it.copy(isRead = true) else it }
        }
    }

    fun markModelNotificationRead(notificationId: String) {
        _modelNotifications.update { list ->
            list.map { if (it.id == notificationId) it.copy(isRead = true) else it }
        }
    }

    fun markAllNotificationsRead() {
        _notifications.update { list -> list.map { it.copy(isRead = true) } }
    }

    fun markAllModelNotificationsRead() {
        _modelNotifications.update { list -> list.map { it.copy(isRead = true) } }
    }

    fun dismissNotification(notificationId: String) {
        _notifications.update { list -> list.filter { it.id != notificationId } }
    }

    fun dismissModelNotification(notificationId: String) {
        _modelNotifications.update { list -> list.filter { it.id != notificationId } }
    }

    private fun pushModelNotification(title: String, message: String) {
        val notification = AppNotification(
            id = "mn_${System.currentTimeMillis()}",
            title = title,
            message = message,
            time = "Just now"
        )
        _modelNotifications.update { listOf(notification) + it }
    }

    private fun pushUserNotification(title: String, message: String) {
        val notification = AppNotification(
            id = "n_${System.currentTimeMillis()}",
            title = title,
            message = message,
            time = "Just now"
        )
        _notifications.update { listOf(notification) + it }
    }

    fun getCurrentModelId(): String? = _linkedModelId.value

    /** Picks a random regular user for peer video calls — never a model account. */
    fun findRandomPeerUser(excludeUserIds: Set<String> = emptySet()): RandomPeerMatch? {
        val currentUserId = _currentUserId.value
        val blockedIds = _blockedUsers.value.map { it.id }.toSet()
        val modelAccountIds = userModelLinks.keys + _userModes.value.filter { it.value }.keys

        val peerPool = demoUserDisplayNames.keys.filter { userId ->
            userId !in modelAccountIds &&
                userId !in blockedIds &&
                userId != currentUserId &&
                userId !in excludeUserIds
        }
        if (peerPool.isEmpty()) return null

        val pickedId = peerPool.random()
        val profile = getUserProfile(pickedId)
        return RandomPeerMatch(
            userId = pickedId,
            name = profile.name,
            avatarUrl = profile.avatarUrl
        )
    }

    fun getReviewsForCurrentModel(): List<ModelReview> {
        val modelId = _linkedModelId.value ?: return emptyList()
        return _reviews.value.filter { it.modelId == modelId }
    }

    fun getAverageRatingForCurrentModel(): Float {
        val modelReviews = getReviewsForCurrentModel()
        if (modelReviews.isNotEmpty()) {
            return (modelReviews.sumOf { it.rating }.toFloat() / modelReviews.size)
        }
        return _models.value.find { it.id == _linkedModelId.value }?.rating ?: 0f
    }

    fun getReviewCountForCurrentModel(): Int {
        val modelId = _linkedModelId.value ?: return 0
        val model = _models.value.find { it.id == modelId }
        return model?.reviewsCount?.takeIf { it > 0 } ?: getReviewsForCurrentModel().size
    }

    fun getEarningsForCurrentModel(): List<ModelCallEarning> {
        val modelId = _linkedModelId.value ?: return emptyList()
        return _modelEarnings.value.filter { it.modelId == modelId }
    }

    fun getTotalEarningsForCurrentModel(): Int {
        return getEarningsForCurrentModel().sumOf { it.amountEarned }
    }

    init {
        viewModelScope.launch {
            val statuses = listOf("Online", "Offline", "Busy")
            while (true) {
                delay(6000) // update every 6 seconds to be highly responsive and real-time representational
                val currentList = _models.value
                if (currentList.isNotEmpty()) {
                    val randomIndex = currentList.indices.random()
                    val modelToUpdate = currentList[randomIndex]
                    val currentStatus = modelToUpdate.status
                    val nextStatus = statuses.filter { it != currentStatus }.random()
                    _models.value = currentList.mapIndexed { idx, model ->
                        if (idx == randomIndex) {
                            model.copy(
                                status = nextStatus,
                                isOnline = nextStatus == "Online"
                            )
                        } else {
                            model
                        }
                    }
                }
            }
        }
    }

    private val _walletState = MutableStateFlow(Wallet(balance = 1500, audioBalance = 750, videoBalance = 750))
    val walletState: StateFlow<Wallet> = _walletState.asStateFlow()

    private val _favorites = MutableStateFlow<Set<String>>(emptySet())
    val favorites: StateFlow<Set<String>> = _favorites.asStateFlow()

    private val _blockedUsers = MutableStateFlow<List<BlockedUser>>(emptyList())
    val blockedUsers: StateFlow<List<BlockedUser>> = _blockedUsers.asStateFlow()

    private val _chatThreads = MutableStateFlow(createInitialChatThreads())
    val chatThreads: StateFlow<List<ChatThread>> = _chatThreads.asStateFlow()

    private val _chatMessages = MutableStateFlow(createInitialChatMessages())
    val chatMessages: StateFlow<Map<String, List<ChatMessage>>> = _chatMessages.asStateFlow()

    private val _bookings = MutableStateFlow<List<CallBooking>>(
        listOf(
            CallBooking(
                id = "b1",
                modelId = "1",
                modelName = "alessia_beauty",
                modelAvatarUrl = "https://api.dicebear.com/7.x/bottts/png?seed=1",
                isVideo = true,
                date = "Tomorrow, Jun 12",
                timeSlot = "04:30 PM - 05:00 PM",
                cost = 250,
                userId = "rahul_sharma",
                userName = "Rahul Sharma",
                userAvatarUrl = "https://i.pravatar.cc/300?u=rahul_sharma"
            ),
            CallBooking(
                id = "b3",
                modelId = "1",
                modelName = "alessia_beauty",
                modelAvatarUrl = "https://api.dicebear.com/7.x/bottts/png?seed=1",
                isVideo = false,
                date = "Sat, Jun 13",
                timeSlot = "11:30 AM - 12:00 PM",
                cost = 180,
                userId = "vikram_singh",
                userName = "Vikram Singh",
                userAvatarUrl = "https://i.pravatar.cc/300?u=vikram_singh"
            ),
            CallBooking(
                id = "b4",
                modelId = "1",
                modelName = "alessia_beauty",
                modelAvatarUrl = "https://api.dicebear.com/7.x/bottts/png?seed=1",
                isVideo = true,
                date = "Sun, Jun 14",
                timeSlot = "02:00 PM - 02:30 PM",
                cost = 300,
                userId = "ananya_k",
                userName = "Ananya K.",
                userAvatarUrl = "https://i.pravatar.cc/300?u=ananya_k"
            ),
            CallBooking(
                id = "b2",
                modelId = "3",
                modelName = "riya_patel",
                modelAvatarUrl = "https://api.dicebear.com/7.x/bottts/png?seed=3",
                isVideo = false,
                date = "Jun 14, 2026",
                timeSlot = "11:00 AM - 11:30 AM",
                cost = 150,
                userId = "ajay@appinop.com",
                userName = "Ajay Kumar",
                userAvatarUrl = "https://i.pravatar.cc/300?u=ajay@appinop.com"
            )
        )
    )
    val bookings: StateFlow<List<CallBooking>> = _bookings.asStateFlow()

    private val _transactions = MutableStateFlow<List<WalletTransaction>>(
        listOf(
            WalletTransaction("tx1", "Gold Plan Activation", "+1250 Tokens", "10 Jun 2026, 09:30 AM", true),
            WalletTransaction("tx2", "Private Video Call - Aisha Khan", "-300 Video Tokens", "10 Jun 2026, 10:15 AM", false),
            WalletTransaction("tx3", "Private Audio Call - Riya Patel", "-250 Audio Tokens", "09 Jun 2026, 08:35 PM", false),
            WalletTransaction("tx4", "Welcome Bonus", "+250 Tokens", "08 Jun 2026, 03:00 PM", true)
        )
    )
    val transactions: StateFlow<List<WalletTransaction>> = _transactions.asStateFlow()

    fun scheduleCall(booking: CallBooking): Boolean {
        val prepaid = deductTokens(
            amount = booking.cost,
            isVideo = booking.isVideo,
            reason = "Scheduled Call - ${booking.modelName}"
        )
        if (!prepaid) return false

        val userId = _currentUserId.value.orEmpty()
        val enriched = if (userId.isNotBlank() && booking.userId.isBlank()) {
            val profile = getUserProfile(userId)
            booking.copy(
                userId = userId,
                userName = profile.name,
                userAvatarUrl = profile.avatarUrl
            )
        } else {
            booking
        }
        _bookings.value = listOf(enriched) + _bookings.value

        val callType = if (enriched.isVideo) "video" else "audio"
        val userLabel = enriched.userName.ifBlank { "A user" }
        val timeLabel = enriched.timeSlot.substringBefore(" -").trim()
        pushModelNotification(
            title = "New scheduled call 📅",
            message = "$userLabel scheduled a $callType session on ${enriched.date} at $timeLabel (${enriched.cost} tokens paid). Please accept."
        )
        return true
    }

    fun acceptBooking(bookingId: String) {
        val accepted = _bookings.value.find { it.id == bookingId && it.status == "Scheduled" } ?: return
        _bookings.update { list ->
            list.map { booking ->
                if (booking.id == bookingId && booking.status == "Scheduled") {
                    booking.copy(status = "Accepted")
                } else {
                    booking
                }
            }
        }
        val callType = if (accepted.isVideo) "video" else "audio"
        val timeLabel = accepted.timeSlot.substringBefore(" -").trim()
        pushUserNotification(
            title = "Session confirmed ✅",
            message = "${accepted.modelName} accepted your $callType session on ${accepted.date} at $timeLabel."
        )
    }

    fun scheduledBookingsForModel(modelId: String): List<CallBooking> =
        _bookings.value.filter {
            it.modelId == modelId && (it.status == "Scheduled" || it.status == "Accepted")
        }

    fun cancelBooking(bookingId: String) {
        val current = _bookings.value
        val found = current.find { it.id == bookingId }
        if (found != null) {
            _bookings.value = current.map {
                if (it.id == bookingId) it.copy(status = "Cancelled") else it
            }
            addTransaction("Cancelled Call - ${found.modelName}", "Booking Cancelled", false)
        }
    }

    fun deductTokens(amount: Int, isVideo: Boolean = false, reason: String? = null): Boolean {
        val wallet = _walletState.value
        val txTitle = reason ?: if (isVideo) "Private Video Call deducted" else "Private Audio Call deducted"
        if (isVideo) {
            val current = wallet.videoBalance
            if (current >= amount) {
                val newVideo = current - amount
                _walletState.value = wallet.copy(
                    videoBalance = newVideo,
                    balance = wallet.audioBalance + newVideo
                )
                addTransaction(txTitle, "-$amount Video Tokens", false)
                return true
            }
        } else {
            val current = wallet.audioBalance
            if (current >= amount) {
                val newAudio = current - amount
                _walletState.value = wallet.copy(
                    audioBalance = newAudio,
                    balance = newAudio + wallet.videoBalance
                )
                addTransaction(txTitle, "-$amount Audio Tokens", false)
                return true
            }
        }
        return false
    }

    fun rechargeTokens(amount: Int, isVideo: Boolean = false) {
        val wallet = _walletState.value
        if (isVideo) {
            val newVideo = wallet.videoBalance + amount
            _walletState.value = wallet.copy(
                videoBalance = newVideo,
                balance = wallet.audioBalance + newVideo
            )
            addTransaction("Video Token Pack Recharge", "+$amount Video Tokens", true)
        } else {
            val newAudio = wallet.audioBalance + amount
            _walletState.value = wallet.copy(
                audioBalance = newAudio,
                balance = newAudio + wallet.videoBalance
            )
            addTransaction("Audio Token Pack Recharge", "+$amount Audio Tokens", true)
        }
    }

    fun addTransaction(title: String, amountText: String, isPositive: Boolean) {
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        val currentDate = sdf.format(Date())
        val newTx = WalletTransaction(
            id = "tx_${System.currentTimeMillis()}",
            title = title,
            amount = amountText,
            date = currentDate,
            isPositive = isPositive
        )
        _transactions.value = listOf(newTx) + _transactions.value
    }

    fun toggleFavorite(modelId: String) {
        val current = _favorites.value
        if (modelId in current) {
            _favorites.value = current - modelId
        } else {
            _favorites.value = current + modelId
        }
    }

    fun blockUser(userId: String, name: String, avatarUrl: String = "https://i.pravatar.cc/150?u=$userId") {
        if (isBlocked(userId)) return
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val entry = BlockedUser(
            id = userId,
            name = name,
            avatarUrl = avatarUrl,
            blockedAt = sdf.format(Date())
        )
        _blockedUsers.value = _blockedUsers.value + entry
        _favorites.update { current -> current - userId }
    }

    fun unblockUser(userId: String) {
        _blockedUsers.update { list -> list.filter { it.id != userId } }
    }

    fun isBlocked(accountId: String): Boolean {
        return _blockedUsers.value.any { it.id == accountId }
    }

    // --- REFERRAL & PROMO CODES SYSTEM ---
    private val _myPromoCode = MutableStateFlow("VIP-AJAY-999")
    val myPromoCode: StateFlow<String> = _myPromoCode.asStateFlow()

    private val _referredUsers = MutableStateFlow<List<String>>(
        listOf("emily_clark", "luke_sky12")
    )
    val referredUsers: StateFlow<List<String>> = _referredUsers.asStateFlow()

    private val _hasRedeemedPromoCode = MutableStateFlow(false)
    val hasRedeemedPromoCode: StateFlow<Boolean> = _hasRedeemedPromoCode.asStateFlow()

    /**
     * Simulates another user using our promo code to sign up.
     * This awards bonus tokens to the current user (the referrer)
     */
    fun simulateFriendRegister(friendName: String, codeEntered: String): Boolean {
        val trimmedCode = codeEntered.trim().uppercase()
        val currentCode = _myPromoCode.value.trim().uppercase()

        if (trimmedCode == currentCode && friendName.isNotBlank()) {
            val username = friendName.trim().lowercase().replace(" ", "_")
            // Add to referred list
            _referredUsers.value = _referredUsers.value + username
            
            // Reward referrer with 100 bonus tokens (50 Audio, 50 Video)
            val wallet = _walletState.value
            val newAudio = wallet.audioBalance + 50
            val newVideo = wallet.videoBalance + 50
            _walletState.value = wallet.copy(
                audioBalance = newAudio,
                videoBalance = newVideo,
                balance = newAudio + newVideo
            )

            // Add transaction log
            addTransaction(
                title = "Referral Reward: @$username Signed Up",
                amountText = "+100 Tokens (+50 A / +50 V)",
                isPositive = true
            )
            return true
        }
        return false
    }

    /**
     * Allows the current user to redeem a friend's promo code,
     * giving themselves a sign up/welcome bonus of 100 tokens.
     */
    fun redeemPromoCode(code: String): Boolean {
        val trimmedCode = code.trim().uppercase()
        val currentCode = _myPromoCode.value.trim().uppercase()

        if (trimmedCode == currentCode) {
            // Cannot use your own referral code
            return false
        }
        if (_hasRedeemedPromoCode.value) {
            // Already redeemed a code
            return false
        }
        if (trimmedCode.isBlank()) {
            return false
        }

        // Set redeemed state
        _hasRedeemedPromoCode.value = true

        // Reward user with 100 bonus tokens (50 Audio, 50 Video)
        val wallet = _walletState.value
        val newAudio = wallet.audioBalance + 50
        val newVideo = wallet.videoBalance + 50
        _walletState.value = wallet.copy(
            audioBalance = newAudio,
            videoBalance = newVideo,
            balance = newAudio + newVideo
        )

        // Add transaction log
        addTransaction(
            title = "Promo Code Redeemed: $trimmedCode",
            amountText = "+100 Tokens (+50 A / +50 V)",
            isPositive = true
        )
        return true
    }

    // Dynamic simulated user database
    private val _userAccounts = MutableStateFlow<Map<String, String>>(
        mapOf(
            "ajay@appinop.com" to "password123",
            "john_doe_99" to "password123",
            "vip_ajay_999" to "password123",
            "9876543210@dummy.phone" to "password123",
            "model_female@pairr.live" to "password123"
        )
    )
    val userAccounts: StateFlow<Map<String, String>> = _userAccounts.asStateFlow()

    private val _userNames = MutableStateFlow<Map<String, String>>(
        mapOf(
            "ajay@appinop.com" to "Ajay Kumar",
            "john_doe_99" to "John Doe",
            "vip_ajay_999" to "VIP Ajay",
            "9876543210@dummy.phone" to "Ajay Kumar",
            "model_female@pairr.live" to "Model Female"
        )
    )
    val userNames: StateFlow<Map<String, String>> = _userNames.asStateFlow()

    private val _userModes = MutableStateFlow<Map<String, Boolean>>(
        mapOf(
            "ajay@appinop.com" to false,
            "john_doe_99" to false,
            "9876543210@dummy.phone" to false,
            "vip_ajay_999" to false,
            "model_female@pairr.live" to true
        )
    )

    fun registerUser(
        name: String, 
        phone: String, 
        passwordEntered: String, 
        isModel: Boolean = false,
        gender: String = "Male",
        age: String = "",
        audioRate: String = "",
        videoRate: String = "",
        onResult: (Boolean, String?) -> Unit = { _, _ -> }
    ) {
        val phoneClean = phone.trim()
        val normalized = if (phoneClean.contains("@")) phoneClean else "$phoneClean@dummy.phone"
        if (normalized.isBlank() || passwordEntered.isBlank()) {
            onResult(false, "Phone and password cannot be blank")
            return
        }

        val performLocalRegistration = {
            val accounts = _userAccounts.value.toMutableMap()
            accounts[normalized] = passwordEntered
            _userAccounts.value = accounts

            val names = _userNames.value.toMutableMap()
            names[normalized] = name.trim()
            _userNames.value = names

            val modes = _userModes.value.toMutableMap()
            modes[normalized] = isModel
            _userModes.value = modes
            
            _isModelMode.value = isModel
            onAuthSuccess(normalized, isModel)
            
            if (isModel) {
                audioRate.toIntOrNull()?.let { setModelAudioRate(it) }
                videoRate.toIntOrNull()?.let { setModelVideoRate(it) }
            }
            onResult(true, null)
        }

        val auth = firebaseAuth
        if (auth != null) {
            auth.createUserWithEmailAndPassword(normalized, passwordEntered)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        performLocalRegistration()
                    } else {
                        onResult(false, task.exception?.localizedMessage ?: "Firebase error. Make sure google-services.json is configured.")
                    }
                }
        } else {
            performLocalRegistration()
        }
    }

    fun loginUser(phone: String, passwordEntered: String, onResult: (Boolean, String?) -> Unit = { _, _ -> }) {
        val phoneClean = phone.trim()
        val normalized = if (phoneClean.contains("@")) phoneClean else "$phoneClean@dummy.phone"
        
        val performLocalLogin = {
            val accountKey = resolveAccountKey(phoneClean, normalized)
            val registeredPassword = accountKey?.let { _userAccounts.value[it] }
            val success = registeredPassword == passwordEntered
            if (success && accountKey != null) {
                val isModel = _userModes.value[accountKey] == true
                _isModelMode.value = isModel
                onAuthSuccess(accountKey, isModel)
                onResult(true, null)
            } else {
                onResult(false, "Invalid credentials")
            }
        }

        val auth = firebaseAuth
        if (auth != null) {
            auth.signInWithEmailAndPassword(normalized, passwordEntered)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        performLocalLogin()
                    } else {
                        onResult(false, task.exception?.localizedMessage ?: "Firebase error. Make sure google-services.json is configured.")
                    }
                }
        } else {
            performLocalLogin()
        }
    }

    fun resetUserPassword(emailOrPhone: String, newPasswordEntered: String): Boolean {
        val normalized = emailOrPhone.trim().lowercase()
        val accounts = _userAccounts.value.toMutableMap()
        accounts[normalized] = newPasswordEntered
        _userAccounts.value = accounts
        return true
    }

    fun addModelReview(modelId: String, rating: Int, text: String, isVideo: Boolean) {
        val reviewId = "rev_" + System.currentTimeMillis()
        val sdf = SimpleDateFormat("dd MMM 2026", Locale.getDefault())
        val dateString = sdf.format(Date())
        val newReview = ModelReview(
            id = reviewId,
            modelId = modelId,
            reviewerName = "You",
            rating = rating,
            reviewText = text,
            date = dateString,
            isVideo = isVideo
        )
        _reviews.value = listOf(newReview) + _reviews.value
        
        // update average model rating
        val currentList = _models.value
        _models.value = currentList.map { model ->
            if (model.id == modelId) {
                val newCount = model.reviewsCount + 1
                val newAvg = ((model.rating * model.reviewsCount) + rating) / newCount
                // Format to 1 decimal place safely
                val formattedAvg = (Math.round(newAvg * 10.0) / 10.0).toFloat()
                model.copy(
                    rating = formattedAvg,
                    reviewsCount = newCount
                )
            } else {
                model
            }
        }
    }

    fun resetData() {
        _models.value = com.example.data.mockModels
        _walletState.value = Wallet(balance = 1500, audioBalance = 750, videoBalance = 750)
        _favorites.value = emptySet()
        _blockedUsers.value = emptyList()
        _bookings.value = listOf(
            CallBooking(
                id = "b1",
                modelId = "1",
                modelName = "alessia_beauty",
                modelAvatarUrl = "https://api.dicebear.com/7.x/bottts/png?seed=1",
                isVideo = true,
                date = "Tomorrow, Jun 12",
                timeSlot = "04:30 PM - 05:00 PM",
                cost = 250,
                userId = "rahul_sharma",
                userName = "Rahul Sharma",
                userAvatarUrl = "https://i.pravatar.cc/300?u=rahul_sharma"
            ),
            CallBooking(
                id = "b3",
                modelId = "1",
                modelName = "alessia_beauty",
                modelAvatarUrl = "https://api.dicebear.com/7.x/bottts/png?seed=1",
                isVideo = false,
                date = "Sat, Jun 13",
                timeSlot = "11:30 AM - 12:00 PM",
                cost = 180,
                userId = "vikram_singh",
                userName = "Vikram Singh",
                userAvatarUrl = "https://i.pravatar.cc/300?u=vikram_singh"
            ),
            CallBooking(
                id = "b4",
                modelId = "1",
                modelName = "alessia_beauty",
                modelAvatarUrl = "https://api.dicebear.com/7.x/bottts/png?seed=1",
                isVideo = true,
                date = "Sun, Jun 14",
                timeSlot = "02:00 PM - 02:30 PM",
                cost = 300,
                userId = "ananya_k",
                userName = "Ananya K.",
                userAvatarUrl = "https://i.pravatar.cc/300?u=ananya_k"
            ),
            CallBooking(
                id = "b2",
                modelId = "3",
                modelName = "riya_patel",
                modelAvatarUrl = "https://api.dicebear.com/7.x/bottts/png?seed=3",
                isVideo = false,
                date = "Jun 14, 2026",
                timeSlot = "11:00 AM - 11:30 AM",
                cost = 150,
                userId = "ajay@appinop.com",
                userName = "Ajay Kumar",
                userAvatarUrl = "https://i.pravatar.cc/300?u=ajay@appinop.com"
            )
        )
        _transactions.value = listOf(
            WalletTransaction("tx1", "Gold Plan Activation", "+1250 Tokens", "10 Jun 2026, 09:30 AM", true),
            WalletTransaction("tx2", "Private Video Call - Aisha Khan", "-300 Video Tokens", "10 Jun 2026, 10:15 AM", false),
            WalletTransaction("tx3", "Private Audio Call - Riya Patel", "-250 Audio Tokens", "09 Jun 2026, 08:35 PM", false),
            WalletTransaction("tx4", "Welcome Bonus", "+250 Tokens", "08 Jun 2026, 03:00 PM", true)
        )
        _referredUsers.value = listOf("emily_clark", "luke_sky12")
        _hasRedeemedPromoCode.value = false
        _chatThreads.value = createInitialChatThreads()
        _chatMessages.value = createInitialChatMessages()
        _modelEarnings.value = listOf(
            ModelCallEarning("e1", "1", "Rahul S.", true, "15:00", "10 Jun 2026, 10:15 AM", 300),
            ModelCallEarning("e2", "1", "Amit P.", false, "12:30", "09 Jun 2026, 08:35 PM", 250),
            ModelCallEarning("e3", "1", "Vikram D.", true, "08:45", "08 Jun 2026, 06:20 PM", 180),
            ModelCallEarning("e4", "1", "Karan W.", false, "20:00", "07 Jun 2026, 11:00 AM", 400),
            ModelCallEarning("e5", "3", "Sagar K.", true, "25:00", "08 Jun 2026, 09:10 PM", 360),
            ModelCallEarning("e6", "3", "Neha R.", false, "10:00", "06 Jun 2026, 04:45 PM", 200)
        )
    }

    fun clearAllCache() {
        resetData()
    }

    fun logout() {
        chatRepository.stopListening()
        activeThreadId = null
        _currentUserId.value = null
        _linkedModelId.value = null
        _isModelMode.value = false
        firebaseAuth?.signOut()
    }

    fun setActiveThread(threadId: String?) {
        activeThreadId = threadId
    }

    fun startChatSync() {
        val userId = _currentUserId.value ?: return
        chatRepository.stopListening()
        if (_isModelMode.value) {
            val modelId = _linkedModelId.value ?: return
            chatRepository.listenToModelThreads(modelId) { remoteThreads ->
                mergeThreadsFromRemote(remoteThreads)
            }
        } else {
            chatRepository.listenToUserThreads(userId) { remoteThreads ->
                mergeThreadsFromRemote(remoteThreads)
            }
        }
    }

    fun listenToThreadMessages(threadId: String) {
        chatRepository.listenToMessages(threadId) { remoteMessages ->
            _chatMessages.update { current ->
                val local = current[threadId] ?: emptyList()
                val merged = mergeMessages(local, remoteMessages)
                current + (threadId to merged)
            }
            updateThreadPreviewFromMessages(threadId)
        }
    }

    fun stopListeningToMessages() {
        chatRepository.stopMessagesListener()
    }

    private fun onAuthSuccess(userId: String, isModel: Boolean) {
        _currentUserId.value = userId
        _linkedModelId.value = if (isModel) userModelLinks[userId] ?: "1" else null
        startChatSync()
    }

    private fun mergeThreadsFromRemote(remoteThreads: List<ChatThread>) {
        if (remoteThreads.isEmpty()) return
        _chatThreads.update { localThreads ->
            val byId = localThreads.associateBy { it.id }.toMutableMap()
            remoteThreads.forEach { remote ->
                val existing = byId[remote.id]
                byId[remote.id] = if (existing == null) {
                    remote
                } else {
                    val remoteMillis = remote.lastMessageMillis
                    val localMillis = existing.lastMessageMillis
                    if (remoteMillis >= localMillis) remote else existing
                }
            }
            byId.values.toList()
        }
    }

    private fun mergeMessages(local: List<ChatMessage>, remote: List<ChatMessage>): List<ChatMessage> {
        if (remote.isEmpty()) return local
        val byId = (local + remote).associateBy { it.id }
        return byId.values.sortedBy { it.timestampMillis }
    }

    private fun updateThreadPreviewFromMessages(threadId: String) {
        val messages = _chatMessages.value[threadId] ?: return
        val last = messages.lastOrNull() ?: return
        _chatThreads.update { threads ->
            threads.map { thread ->
                if (thread.id == threadId) {
                    thread.copy(
                        lastMessage = last.content,
                        lastMessageTime = last.timestamp,
                        lastMessageMillis = last.timestampMillis
                    )
                } else thread
            }
        }
    }

    private fun resolveAccountKey(phoneClean: String, normalized: String): String? {
        return when {
            _userAccounts.value.containsKey(normalized) -> normalized
            _userAccounts.value.containsKey(phoneClean) -> phoneClean
            else -> null
        }
    }

    private fun userIdsMatch(storedUserId: String, activeUserId: String): Boolean {
        if (storedUserId == activeUserId) return true
        val activeRaw = activeUserId.removeSuffix("@dummy.phone")
        val storedRaw = storedUserId.removeSuffix("@dummy.phone")
        return activeRaw == storedUserId ||
            storedRaw == activeUserId ||
            activeRaw == storedRaw
    }

    fun getThreadsForMode(isModelSide: Boolean): List<ChatThread> {
        val userId = _currentUserId.value ?: return emptyList()
        return _chatThreads.value
            .filter { thread ->
                if (isModelSide) {
                    val modelId = _linkedModelId.value
                    modelId != null && thread.modelId == modelId
                } else {
                    userIdsMatch(thread.userId, userId)
                }
            }
            .filter { thread ->
                val blockedId = if (isModelSide) thread.userId else thread.modelId
                blockedId.isBlank() || !isBlocked(blockedId)
            }
            .map { thread -> thread.toDisplayThread(isModelSide, _models.value) }
            .sortedByDescending { thread ->
                thread.lastMessageMillis.takeIf { it > 0 }
                    ?: _chatMessages.value[thread.id]?.maxOfOrNull { it.timestampMillis }
                    ?: 0L
            }
    }

    fun getUnreadCount(isModelSide: Boolean): Int {
        return getThreadsForMode(isModelSide).sumOf { it.unreadCount }
    }

    fun ensureChatThread(
        modelId: String,
        modelName: String,
        modelAvatarUrl: String,
        isOnline: Boolean = false
    ): String {
        val userId = _currentUserId.value ?: return modelId
        val userName = _userNames.value[userId] ?: "User"
        val userAvatarUrl = "https://i.pravatar.cc/150?u=$userId"
        val threadId = chatRepository.buildThreadId(userId, modelId)

        if (_chatThreads.value.none { it.id == threadId }) {
            val thread = ChatThread(
                id = threadId,
                participantName = modelName,
                participantAvatarUrl = modelAvatarUrl,
                lastMessage = "Start a conversation…",
                lastMessageTime = "",
                unreadCount = 0,
                isOnline = isOnline,
                isModelThread = true,
                userId = userId,
                modelId = modelId,
                userName = userName,
                modelName = modelName,
                userAvatarUrl = userAvatarUrl,
                modelAvatarUrl = modelAvatarUrl
            )
            _chatThreads.value = listOf(thread) + _chatThreads.value
            chatRepository.upsertThread(thread)
        }
        if (!_chatMessages.value.containsKey(threadId)) {
            _chatMessages.update { it + (threadId to emptyList()) }
        }
        return threadId
    }

    fun markThreadAsRead(threadId: String) {
        val isModel = _isModelMode.value
        _chatThreads.update { threads ->
            threads.map { thread ->
                if (thread.id != threadId) return@map thread
                if (isModel) {
                    thread.copy(modelUnreadCount = 0, unreadCount = 0)
                } else {
                    thread.copy(userUnreadCount = 0, unreadCount = 0)
                }
            }
        }
        val thread = _chatThreads.value.find { it.id == threadId } ?: return
        chatRepository.updateUnreadCounts(
            threadId = threadId,
            userUnreadCount = thread.userUnreadCount,
            modelUnreadCount = thread.modelUnreadCount
        )
    }

    fun sendMessage(threadId: String, content: String, isFromUser: Boolean) {
        val trimmed = content.trim()
        if (trimmed.isBlank()) return

        ensureThreadExistsForMessage(threadId, isFromUser)

        val now = System.currentTimeMillis()
        val timeStr = formatChatTime(now)
        val message = ChatMessage(
            id = "msg_$now",
            content = trimmed,
            isFromUser = isFromUser,
            timestamp = timeStr,
            timestampMillis = now
        )

        _chatMessages.update { map ->
            val existing = map[threadId] ?: emptyList()
            map + (threadId to (existing + message))
        }

        val thread = _chatThreads.value.find { it.id == threadId }
        val recipientViewing = activeThreadId == threadId
        val userUnread = when {
            isFromUser -> 0
            recipientViewing && !_isModelMode.value -> 0
            else -> (thread?.userUnreadCount ?: 0) + 1
        }
        val modelUnread = when {
            !isFromUser -> 0
            recipientViewing && _isModelMode.value -> 0
            else -> (thread?.modelUnreadCount ?: 0) + 1
        }

        _chatThreads.update { threads ->
            threads.map { item ->
                if (item.id == threadId) {
                    item.copy(
                        lastMessage = trimmed,
                        lastMessageTime = timeStr,
                        lastMessageMillis = now,
                        userUnreadCount = userUnread,
                        modelUnreadCount = modelUnread
                    )
                } else item
            }
        }

        _chatThreads.value.find { it.id == threadId }?.let { chatRepository.upsertThread(it) }
        chatRepository.sendMessage(
            threadId = threadId,
            content = trimmed,
            isFromUser = isFromUser,
            timestamp = now,
            userUnreadCount = userUnread,
            modelUnreadCount = modelUnread,
            lastMessage = trimmed
        )
    }

    private fun ChatThread.toDisplayThread(
        isModelSide: Boolean,
        models: List<com.example.data.AppModel>
    ): ChatThread {
        val modelOnline = models.find { it.id == modelId }?.isOnline == true
        return if (isModelSide) {
            copy(
                participantName = userName,
                participantAvatarUrl = userAvatarUrl,
                unreadCount = modelUnreadCount,
                isOnline = true,
                isModelThread = false
            )
        } else {
            copy(
                participantName = modelName,
                participantAvatarUrl = modelAvatarUrl,
                unreadCount = userUnreadCount,
                isOnline = modelOnline,
                isModelThread = true
            )
        }
    }

    private fun ensureThreadExistsForMessage(threadId: String, isFromUser: Boolean) {
        if (_chatThreads.value.any { it.id == threadId }) return
        val parsed = parseThreadId(threadId) ?: return
        val (userId, modelId) = parsed
        val model = _models.value.find { it.id == modelId }
        val thread = ChatThread(
            id = threadId,
            participantName = model?.name ?: "Model",
            participantAvatarUrl = model?.displayImageUrls()?.firstOrNull() ?: "https://i.pravatar.cc/150?u=$modelId",
            lastMessage = "Start a conversation…",
            lastMessageTime = "",
            unreadCount = 0,
            isOnline = model?.isOnline == true,
            isModelThread = true,
            userId = userId,
            modelId = modelId,
            userName = _userNames.value[userId] ?: "User",
            modelName = model?.name ?: "Model",
            userAvatarUrl = "https://i.pravatar.cc/150?u=$userId",
            modelAvatarUrl = model?.displayImageUrls()?.firstOrNull() ?: "https://i.pravatar.cc/150?u=$modelId"
        )
        _chatThreads.value = listOf(thread) + _chatThreads.value
        if (!_chatMessages.value.containsKey(threadId)) {
            _chatMessages.update { it + (threadId to emptyList()) }
        }
        chatRepository.upsertThread(thread)
    }

    private fun parseThreadId(threadId: String): Pair<String, String>? {
        val lastUnderscore = threadId.lastIndexOf('_')
        if (lastUnderscore <= 0 || lastUnderscore == threadId.lastIndex) return null
        return threadId.substring(0, lastUnderscore) to threadId.substring(lastUnderscore + 1)
    }

    private fun formatChatTime(millis: Long): String = ChatRepository.formatChatTime(millis)

    companion object {
        private fun createInitialChatThreads(): List<ChatThread> = listOf(
            ChatThread(
                id = "john_doe_99_1",
                participantName = "Aisha Khan",
                participantAvatarUrl = "https://i.pravatar.cc/150?u=1",
                lastMessage = "Are you free for a call later?",
                lastMessageTime = "10:24",
                unreadCount = 0,
                isOnline = true,
                isModelThread = true,
                userId = "john_doe_99",
                modelId = "1",
                userName = "John Doe",
                modelName = "Aisha Khan",
                userAvatarUrl = "https://i.pravatar.cc/150?u=john",
                modelAvatarUrl = "https://i.pravatar.cc/150?u=1",
                userUnreadCount = 2,
                modelUnreadCount = 0,
                lastMessageMillis = 0L
            ),
            ChatThread(
                id = "john_doe_99_3",
                participantName = "Riya Patel",
                participantAvatarUrl = "https://i.pravatar.cc/150?u=3",
                lastMessage = "It was lovely talking to you",
                lastMessageTime = "20:35",
                unreadCount = 0,
                isOnline = true,
                isModelThread = true,
                userId = "john_doe_99",
                modelId = "3",
                userName = "John Doe",
                modelName = "Riya Patel",
                userAvatarUrl = "https://i.pravatar.cc/150?u=john",
                modelAvatarUrl = "https://i.pravatar.cc/150?u=3",
                userUnreadCount = 0,
                modelUnreadCount = 0
            ),
            ChatThread(
                id = "ajay@appinop.com_1",
                participantName = "Aisha Khan",
                participantAvatarUrl = "https://i.pravatar.cc/150?u=1",
                lastMessage = "Thank you so much! 🙏",
                lastMessageTime = "14:08",
                unreadCount = 0,
                isOnline = true,
                isModelThread = true,
                userId = "ajay@appinop.com",
                modelId = "1",
                userName = "Ajay Kumar",
                modelName = "Aisha Khan",
                userAvatarUrl = "https://i.pravatar.cc/150?u=ajay",
                modelAvatarUrl = "https://i.pravatar.cc/150?u=1",
                userUnreadCount = 0,
                modelUnreadCount = 1
            ),
            ChatThread(
                id = "9876543210@dummy.phone_4",
                participantName = "Neha Singh",
                participantAvatarUrl = "https://i.pravatar.cc/150?u=4",
                lastMessage = "See you in the live room! 🎵",
                lastMessageTime = "18:05",
                unreadCount = 0,
                isOnline = false,
                isModelThread = true,
                userId = "9876543210@dummy.phone",
                modelId = "4",
                userName = "Ajay Kumar",
                modelName = "Neha Singh",
                userAvatarUrl = "https://i.pravatar.cc/150?u=ajay",
                modelAvatarUrl = "https://i.pravatar.cc/150?u=4",
                userUnreadCount = 1,
                modelUnreadCount = 0
            )
        )

        private fun createInitialChatMessages(): Map<String, List<ChatMessage>> {
            fun msg(id: String, content: String, fromUser: Boolean, hour: Int, minute: Int): ChatMessage {
                val cal = java.util.Calendar.getInstance().apply {
                    set(java.util.Calendar.HOUR_OF_DAY, hour)
                    set(java.util.Calendar.MINUTE, minute)
                    set(java.util.Calendar.SECOND, 0)
                }
                val millis = cal.timeInMillis
                val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
                return ChatMessage(id, content, fromUser, sdf.format(Date(millis)), millis)
            }

            return mapOf(
                "john_doe_99_1" to listOf(
                    msg("m1", "Hi Aisha! How are you doing today?", true, 10, 18),
                    msg("m2", "Hey! I'm doing great, thanks for asking 😊", false, 10, 19),
                    msg("m3", "I loved our last conversation about movies!", true, 10, 21),
                    msg("m4", "Me too! We should talk about the new releases soon 🎬", false, 10, 22),
                    msg("m5", "Are you free for a call later?", true, 10, 24)
                ),
                "john_doe_99_3" to listOf(
                    msg("m6", "Thanks for the deep talk yesterday!", false, 20, 30),
                    msg("m7", "It was lovely talking to you", true, 20, 35)
                ),
                "ajay@appinop.com_1" to listOf(
                    msg("m10", "Hi! I really enjoyed your profile.", true, 9, 10),
                    msg("m11", "Thank you Ajay! Happy to connect 💫", false, 9, 12),
                    msg("m12", "You gave me such great advice today.", true, 14, 0),
                    msg("m13", "Glad I could help! Anytime.", false, 14, 5),
                    msg("m14", "Thank you so much! 🙏", true, 14, 8)
                ),
                "9876543210@dummy.phone_4" to listOf(
                    msg("m8", "Your singing session was amazing!", true, 18, 0),
                    msg("m9", "See you in the live room! 🎵", false, 18, 5)
                )
            )
        }
    }

    fun saveModelProfile(model: com.example.data.AppModel) {
        if (model.imageUrls.size > 5) {
            android.util.Log.e("Firebase", "Cannot save model: more than 5 images")
            return
        }
        firestore?.collection("models")?.document(model.id)?.set(model)
            ?.addOnFailureListener { e -> android.util.Log.e("Firebase", "Error saving model: ${e.message}") }
    }

    private fun updateCurrentModel(transform: (com.example.data.AppModel) -> com.example.data.AppModel) {
        val modelId = _linkedModelId.value ?: return
        _models.update { list ->
            list.map { model -> if (model.id == modelId) transform(model) else model }
        }
        _models.value.find { it.id == modelId }?.let { saveModelProfile(it) }
    }

    fun addGalleryPhotoToCurrentModel(uri: String): Boolean {
        val modelId = _linkedModelId.value ?: return false
        var added = false
        _models.update { list ->
            list.map { model ->
                if (model.id != modelId) return@map model
                if (model.imageUrls.size >= 5) {
                    added = false
                    model
                } else {
                    added = true
                    val newUrls = model.imageUrls + uri
                    model.copy(
                        imageUrls = newUrls,
                        profilePhotoUrl = model.profilePhotoUrl?.takeIf { it.isNotBlank() } ?: uri
                    )
                }
            }
        }
        if (added) {
            _models.value.find { it.id == modelId }?.let { saveModelProfile(it) }
        }
        return added
    }

    fun removeGalleryPhotoFromCurrentModel(index: Int) {
        updateCurrentModel { model ->
            if (index !in model.imageUrls.indices) return@updateCurrentModel model
            val removedUrl = model.imageUrls[index]
            val newUrls = model.imageUrls.filterIndexed { i, _ -> i != index }
            val newProfilePhoto = when {
                model.profilePhotoUrl == removedUrl -> newUrls.firstOrNull()
                else -> model.profilePhotoUrl
            }
            model.copy(imageUrls = newUrls, profilePhotoUrl = newProfilePhoto)
        }
    }

    fun setIntroVideoForCurrentModel(uri: String) {
        updateCurrentModel { it.copy(introVideoUrl = uri) }
    }

    fun clearIntroVideoForCurrentModel() {
        updateCurrentModel { it.copy(introVideoUrl = null) }
    }

    fun setProfilePhotoForCurrentModel(uri: String) {
        updateCurrentModel { model ->
            val urls = model.imageUrls.toMutableList()
            if (urls.isEmpty()) {
                model.copy(profilePhotoUrl = uri, imageUrls = listOf(uri))
            } else {
                urls.remove(uri)
                urls.add(0, uri)
                model.copy(profilePhotoUrl = uri, imageUrls = urls.take(5))
            }
        }
    }

    fun getModelProfile(modelId: String, onModel: (com.example.data.AppModel?) -> Unit) {
        firestore?.collection("models")?.document(modelId)?.get()
            ?.addOnSuccessListener { doc ->
                val model = doc.toObject(com.example.data.AppModel::class.java)
                onModel(model)
            }
            ?.addOnFailureListener { e -> 
                android.util.Log.e("Firebase", "Error getting model: ${e.message}")
                onModel(null)
            }
    }

    private data class UserProfileDetails(
        val bio: String,
        val age: Int,
        val gender: String,
        val location: String,
        val interests: List<String>,
        val memberSince: String,
        val totalCalls: Int
    )

    private val userProfileDetailsByKey = mapOf(
        "john_doe_99" to UserProfileDetails(
            bio = "Hey there! I love connecting with models for meaningful conversations.",
            age = 28,
            gender = "Male",
            location = "New York, USA",
            interests = listOf("Music", "Travel", "Fitness", "Gaming"),
            memberSince = "Jan 2025",
            totalCalls = 12
        ),
        "ajay@appinop.com" to UserProfileDetails(
            bio = "Exploring Pairr to find great conversations and new connections.",
            age = 29,
            gender = "Male",
            location = "Mumbai, India",
            interests = listOf("Coding", "Reading", "Movies", "Travel"),
            memberSince = "Dec 2024",
            totalCalls = 8
        ),
        "9876543210@dummy.phone" to UserProfileDetails(
            bio = "Exploring Pairr to find great conversations and new connections.",
            age = 29,
            gender = "Male",
            location = "Mumbai, India",
            interests = listOf("Coding", "Reading", "Movies", "Travel"),
            memberSince = "Dec 2024",
            totalCalls = 8
        ),
        "vip_ajay_999" to UserProfileDetails(
            bio = "VIP member — always looking for premium chat experiences.",
            age = 32,
            gender = "Male",
            location = "Delhi, India",
            interests = listOf("Business", "Travel", "Fitness", "Photography"),
            memberSince = "Nov 2024",
            totalCalls = 24
        ),
        "rahul_sharma" to UserProfileDetails(
            bio = "Love meaningful conversations and meeting new people on Pairr.",
            age = 27,
            gender = "Male",
            location = "Bangalore, India",
            interests = listOf("Cricket", "Movies", "Tech", "Food"),
            memberSince = "Mar 2025",
            totalCalls = 6
        ),
        "vikram_singh" to UserProfileDetails(
            bio = "Entrepreneur looking for engaging chats after work.",
            age = 31,
            gender = "Male",
            location = "Jaipur, India",
            interests = listOf("Business", "Travel", "Fitness"),
            memberSince = "Feb 2025",
            totalCalls = 9
        ),
        "ananya_k" to UserProfileDetails(
            bio = "Creative soul — always up for fun and friendly conversations.",
            age = 24,
            gender = "Female",
            location = "Pune, India",
            interests = listOf("Art", "Music", "Fashion", "Travel"),
            memberSince = "Apr 2025",
            totalCalls = 4
        ),
        "ramesh_k" to UserProfileDetails(
            bio = "Regular Pairr user who enjoys audio and video calls.",
            age = 30,
            gender = "Male",
            location = "Hyderabad, India",
            interests = listOf("Gaming", "Music", "Sports"),
            memberSince = "Jan 2025",
            totalCalls = 11
        ),
        "amit_p" to UserProfileDetails(
            bio = "Software engineer — happy to connect during evenings.",
            age = 26,
            gender = "Male",
            location = "Chennai, India",
            interests = listOf("Coding", "Gaming", "Movies"),
            memberSince = "May 2025",
            totalCalls = 3
        ),
        "sagar_k" to UserProfileDetails(
            bio = "Fitness enthusiast who loves motivational chats.",
            age = 28,
            gender = "Male",
            location = "Kolkata, India",
            interests = listOf("Fitness", "Nutrition", "Travel"),
            memberSince = "Dec 2024",
            totalCalls = 7
        ),
        "karan_w" to UserProfileDetails(
            bio = "Music lover and weekend traveler.",
            age = 29,
            gender = "Male",
            location = "Chandigarh, India",
            interests = listOf("Music", "Travel", "Photography"),
            memberSince = "Oct 2024",
            totalCalls = 15
        )
    )

    private val demoUserDisplayNames = mapOf(
        "rahul_sharma" to "Rahul Sharma",
        "vikram_singh" to "Vikram Singh",
        "ananya_k" to "Ananya K.",
        "ramesh_k" to "Ramesh K.",
        "amit_p" to "Amit P.",
        "sagar_k" to "Sagar K.",
        "karan_w" to "Karan W."
    )

    private val demoUserIdsByDisplayName = mapOf(
        "Ramesh K." to "ramesh_k",
        "Rahul Sharma" to "rahul_sharma",
        "Rahul S." to "rahul_sharma",
        "Vikram Singh" to "vikram_singh",
        "Vikram D." to "vikram_singh",
        "Ananya K." to "ananya_k",
        "Amit P." to "amit_p",
        "Sagar K." to "sagar_k",
        "Karan W." to "karan_w",
        "John Doe" to "john_doe_99",
        "Ajay Kumar" to "ajay@appinop.com"
    )

    fun resolveUserIdByDisplayName(displayName: String): String {
        val trimmed = displayName.trim()
        if (trimmed.isBlank()) return ""

        demoUserIdsByDisplayName[trimmed]?.let { return it }
        demoUserIdsByDisplayName.entries
            .firstOrNull { it.key.equals(trimmed, ignoreCase = true) }
            ?.value
            ?.let { return it }

        _chatThreads.value
            .firstOrNull { it.userName.equals(trimmed, ignoreCase = true) }
            ?.userId
            ?.takeIf { it.isNotBlank() }
            ?.let { return it }

        _userNames.value.entries
            .firstOrNull { it.value.equals(trimmed, ignoreCase = true) }
            ?.key
            ?.let { return it }

        val firstToken = trimmed.substringBefore(' ').lowercase()
        _chatThreads.value
            .firstOrNull { thread ->
                thread.userName.isNotBlank() &&
                    thread.userName.substringBefore(' ').equals(firstToken, ignoreCase = true)
            }
            ?.userId
            ?.takeIf { it.isNotBlank() }
            ?.let { return it }

        return trimmed.lowercase()
            .replace(Regex("[^a-z0-9]+"), "_")
            .trim('_')
            .ifBlank { "guest_user" }
    }

    fun getUserProfile(userId: String): UserProfile {
        val thread = _chatThreads.value.find { userIdsMatch(it.userId, userId) }
        val canonicalId = thread?.userId?.takeIf { it.isNotBlank() } ?: userId
        val name = thread?.userName?.takeIf { it.isNotBlank() }
            ?: _userNames.value.entries.find { userIdsMatch(it.key, userId) }?.value
            ?: demoUserDisplayNames.entries.find { userIdsMatch(it.key, userId) }?.value
            ?: canonicalId.substringBefore("@").replace("_", " ")
                .split(" ").joinToString(" ") { word ->
                    word.replaceFirstChar { c -> c.uppercase() }
                }
        val avatarUrl = thread?.userAvatarUrl?.takeIf { it.isNotBlank() }
            ?: "https://i.pravatar.cc/300?u=$canonicalId"
        val details = userProfileDetailsByKey.entries
            .find { userIdsMatch(it.key, userId) }?.value
            ?: UserProfileDetails(
                bio = "Pairr member enjoying conversations with models.",
                age = 25,
                gender = "Prefer not to say",
                location = "India",
                interests = listOf("Music", "Travel"),
                memberSince = "2025",
                totalCalls = (userId.hashCode() and 0x7FFFFFFF) % 20 + 1
            )

        return UserProfile(
            id = canonicalId,
            name = name,
            avatarUrl = avatarUrl,
            bio = details.bio,
            age = details.age,
            gender = details.gender,
            location = details.location,
            interests = details.interests,
            memberSince = details.memberSince,
            totalCalls = details.totalCalls,
            isOnline = thread?.let { true } ?: false
        )
    }
}
