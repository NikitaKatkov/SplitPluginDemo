package com.example.splitplugin.frontend.compose.chatApp.viewmodel

import com.example.splitplugin.frontend.compose.chatApp.model.ChatMessage

data class ChatListUiState(
    val messages: List<ChatMessage> = emptyList(),
) {
    companion object Companion {
        val EMPTY = ChatListUiState()
    }
}