package com.example.splitplugin.frontend.jcef

sealed class ChatWindowCommand {
    object InitializeChatWindow : ChatWindowCommand()
    object ReloadChatWindow : ChatWindowCommand()
    object FocusEditor : ChatWindowCommand()
    object SendToWebView : ChatWindowCommand()
    object RefreshChatWindow : ChatWindowCommand()
}
