package com.example.splitplugin.frontend.compose.chatApp.viewmodel

import com.example.splitplugin.shared.ChatMessage
import kotlinx.coroutines.flow.StateFlow

/**
 * Interface defining the contract for managing chat messages and interactions within a chat system.
 * Provides access to the flow of messages and supports operations for sending and editing chat messages.
 */
interface ChatRepositoryApi {
    /**
     * Flow that emits a list of chat messages.
     * Updates with new messages as they are received or edited.
     */
    val messagesFlow: StateFlow<List<ChatMessage>>

    /**
     * Sends a message with the provided content.
     *
     * @param messageContent The content of the message to be sent.
     */
    suspend fun sendMessage(messageContent: String)
}