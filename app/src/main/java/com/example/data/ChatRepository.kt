package com.example.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatRepository(private val firestore: FirebaseFirestore?) {

    private var threadsListener: ListenerRegistration? = null
    private var messagesListener: ListenerRegistration? = null

    fun buildThreadId(userId: String, modelId: String): String = "${userId}_$modelId"

    fun stopListening() {
        threadsListener?.remove()
        threadsListener = null
        stopMessagesListener()
    }

    fun stopMessagesListener() {
        messagesListener?.remove()
        messagesListener = null
    }

    fun listenToUserThreads(userId: String, onThreads: (List<ChatThread>) -> Unit) {
        val db = firestore ?: return
        threadsListener?.remove()
        threadsListener = db.collection("chats")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener
                onThreads(snapshot.documents.mapNotNull { it.toChatThread() })
            }
    }

    fun listenToModelThreads(modelId: String, onThreads: (List<ChatThread>) -> Unit) {
        val db = firestore ?: return
        threadsListener?.remove()
        threadsListener = db.collection("chats")
            .whereEqualTo("modelId", modelId)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener
                onThreads(snapshot.documents.mapNotNull { it.toChatThread() })
            }
    }

    fun listenToMessages(threadId: String, onMessages: (List<ChatMessage>) -> Unit) {
        val db = firestore ?: return
        messagesListener?.remove()
        messagesListener = db.collection("chats")
            .document(threadId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener
                val messages = snapshot.documents.mapNotNull { doc ->
                    val content = doc.getString("content") ?: return@mapNotNull null
                    val isFromUser = doc.getBoolean("isFromUser") ?: true
                    val timestamp = doc.getLong("timestamp") ?: 0L
                    ChatMessage(
                        id = doc.id,
                        content = content,
                        isFromUser = isFromUser,
                        timestamp = formatChatTime(timestamp),
                        timestampMillis = timestamp
                    )
                }
                onMessages(messages)
            }
    }

    fun upsertThread(thread: ChatThread) {
        val db = firestore ?: return
        db.collection("chats").document(thread.id).set(thread.toFirestoreMap())
    }

    fun sendMessage(
        threadId: String,
        content: String,
        isFromUser: Boolean,
        timestamp: Long,
        userUnreadCount: Int,
        modelUnreadCount: Int,
        lastMessage: String
    ) {
        val db = firestore ?: return
        val threadRef = db.collection("chats").document(threadId)
        val messageRef = threadRef.collection("messages").document()
        val batch = db.batch()
        batch.set(
            messageRef,
            mapOf(
                "content" to content,
                "isFromUser" to isFromUser,
                "timestamp" to timestamp
            )
        )
        batch.set(
            threadRef,
            mapOf(
                "lastMessage" to lastMessage,
                "lastMessageTime" to formatChatTime(timestamp),
                "lastMessageMillis" to timestamp,
                "userUnreadCount" to userUnreadCount,
                "modelUnreadCount" to modelUnreadCount
            ),
            SetOptions.merge()
        )
        batch.commit()
    }

    fun updateUnreadCounts(threadId: String, userUnreadCount: Int, modelUnreadCount: Int) {
        firestore?.collection("chats")?.document(threadId)?.update(
            mapOf(
                "userUnreadCount" to userUnreadCount,
                "modelUnreadCount" to modelUnreadCount
            )
        )
    }

    private fun com.google.firebase.firestore.DocumentSnapshot.toChatThread(): ChatThread? {
        val userId = getString("userId") ?: return null
        val modelId = getString("modelId") ?: return null
        val userName = getString("userName") ?: "User"
        val modelName = getString("modelName") ?: "Model"
        val userAvatarUrl = getString("userAvatarUrl") ?: "https://i.pravatar.cc/150?u=$userId"
        val modelAvatarUrl = getString("modelAvatarUrl") ?: "https://i.pravatar.cc/150?u=$modelId"
        val lastMessage = getString("lastMessage") ?: ""
        val lastMessageTime = getString("lastMessageTime") ?: ""
        val lastMessageMillis = getLong("lastMessageMillis") ?: 0L
        val userUnreadCount = getLong("userUnreadCount")?.toInt() ?: 0
        val modelUnreadCount = getLong("modelUnreadCount")?.toInt() ?: 0
        val isOnline = getBoolean("isOnline") ?: false
        return ChatThread(
            id = id,
            participantName = modelName,
            participantAvatarUrl = modelAvatarUrl,
            lastMessage = lastMessage,
            lastMessageTime = lastMessageTime,
            unreadCount = userUnreadCount,
            isOnline = isOnline,
            isModelThread = true,
            userId = userId,
            modelId = modelId,
            userName = userName,
            modelName = modelName,
            userAvatarUrl = userAvatarUrl,
            modelAvatarUrl = modelAvatarUrl,
            userUnreadCount = userUnreadCount,
            modelUnreadCount = modelUnreadCount,
            lastMessageMillis = lastMessageMillis
        )
    }

    private fun ChatThread.toFirestoreMap(): Map<String, Any> = mapOf(
        "userId" to userId,
        "modelId" to modelId,
        "userName" to userName,
        "modelName" to modelName,
        "userAvatarUrl" to userAvatarUrl,
        "modelAvatarUrl" to modelAvatarUrl,
        "lastMessage" to lastMessage,
        "lastMessageTime" to lastMessageTime,
        "lastMessageMillis" to lastMessageMillis,
        "userUnreadCount" to userUnreadCount,
        "modelUnreadCount" to modelUnreadCount,
        "isOnline" to isOnline
    )

    companion object {
        fun formatChatTime(millis: Long): String {
            if (millis <= 0L) return ""
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            return sdf.format(Date(millis))
        }
    }
}
