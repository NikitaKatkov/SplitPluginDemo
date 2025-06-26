/*
 * Copyright Exafunction, Inc.
 */

package com.example.splitplugin.frontend.jcef

import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.Content
import com.intellij.ui.content.ContentFactory
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOf
import java.awt.BorderLayout
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.event.AncestorEvent
import javax.swing.event.AncestorListener

class ChatToolWindowFactory : ToolWindowFactory {
    private val logger = logger<ChatToolWindowFactory>()

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
//        val chatWindowRpcClient = project.service<ChatWindowRpcClient>()
        
        // Set initial icon (commented out backend dependencies)
        // updateToolWindowIcon(toolWindow)
        // ThemeManager.subscribe("toolWindowIcon") {
        //     updateToolWindowIcon(toolWindow)
        // }

        // Add initial loading content
        val loadingPanel = createLoadingPanel()
        val initialContent = ContentFactory.getInstance().createContent(loadingPanel, "", false)
        toolWindow.contentManager.addContent(initialContent)

        // Start coroutine to wait for language server and initialize chat window
        CoroutineScope(Dispatchers.Main).launch {
            try {
                logger.info("[FRONTEND-UI] Starting chat window initialization")
                waitForLanguageServerAsync(project)
                logger.info("[FRONTEND-UI] Language server wait completed, getting client URL")
                val clientUrl = "www.google.com"
                logger.info("[FRONTEND-UI] Received client URL: $clientUrl")
                logger.info("[FRONTEND-UI] About to call updateToolWindowContent with URL: $clientUrl")
                updateToolWindowContent(project, toolWindow, initialContent, clientUrl)
                logger.info("[FRONTEND-UI] updateToolWindowContent completed for URL: $clientUrl")
                
                // Start listening for commands from backend
                listenForChatWindowCommands(project, toolWindow)
            } catch (e: Exception) {
                logger.error("[FRONTEND-UI] Error initializing chat tool window", e)
                showErrorPanel(toolWindow, initialContent, "Failed to initialize chat window: ${e.message}")
            }
        }

        // Add ancestor listener for activation events
        toolWindow.component.addAncestorListener(object : AncestorListener {
            override fun ancestorAdded(event: AncestorEvent?) {
                try {
                    // Signal backend that tool window was activated
                    CoroutineScope(Dispatchers.IO).launch {
                        // This will be handled by the command flow
                    }
                } catch (e: Exception) {
                    logger.warn("Error handling tool window activation", e)
                }
            }
            override fun ancestorRemoved(event: AncestorEvent?) {}
            override fun ancestorMoved(event: AncestorEvent?) {}
        })
    }

    private suspend fun waitForLanguageServerAsync(project: Project) {
        delay(2000)
    }

    private suspend fun updateToolWindowContent(
        project: Project, 
        toolWindow: ToolWindow, 
        initialContent: Content,
        clientUrl: String
    ) {
        logger.info("[FRONTEND-BROWSER] updateToolWindowContent called with URL: $clientUrl")
        try {
            logger.info("[FRONTEND-BROWSER] Removing initial loading content")
            toolWindow.contentManager.removeContent(initialContent, true)
            
            // Check if JCEF is supported
            val inIdeChatSupported = true //JBCefApp.isSupported() // TODO: Add external JCEF check
            logger.info("[FRONTEND-BROWSER] JCEF supported: $inIdeChatSupported")
            
            if (inIdeChatSupported) {
                // Create chat viewer window with the URL from backend
                logger.info("[FRONTEND-BROWSER] Creating ChatViewerWindow with URL: $clientUrl")

                val chatViewerWindow = ChatViewerWindow(project, false, clientUrl)
                
                logger.info("[FRONTEND-BROWSER] ChatViewerWindow created, creating content wrapper")
                val content = ContentFactory.getInstance().createContent(
                    chatViewerWindow.content, 
                    "", 
                    false
                )
                logger.info("[FRONTEND-BROWSER] Adding content to tool window")
                toolWindow.contentManager.addContent(content)
                toolWindow.contentManager.setSelectedContent(content)
                logger.info("[FRONTEND-BROWSER] Content added successfully - browser should now be visible")
            } else {
                // Create empty content for external chat
                val emptyPanel = JPanel()
                val content = ContentFactory.getInstance().createContent(emptyPanel, "", false)
                toolWindow.contentManager.addContent(content)
                toolWindow.contentManager.setSelectedContent(content)
            }
        } catch (e: Exception) {
            logger.error("Error updating tool window content", e)
            showErrorPanel(toolWindow, initialContent, "Error loading chat: ${e.message}")
        }
    }

    private suspend fun listenForChatWindowCommands(
        project: Project,
        toolWindow: ToolWindow
    ) {
        flowOf(ChatWindowCommand.InitializeChatWindow)
            .catch { e -> logger.error("Error in chat window commands flow", e) }
            .collect { command ->
                handleChatWindowCommand(command, project, toolWindow)
            }
    }

    private suspend fun handleChatWindowCommand(
        command: ChatWindowCommand,
        project: Project,
        toolWindow: ToolWindow,
    ) {
        when (command) {
            is ChatWindowCommand.InitializeChatWindow -> {
                // Re-initialize the chat window
                val clientUrl = "www.yandex.ru"
                val loadingPanel = createLoadingPanel()
                val initialContent = ContentFactory.getInstance().createContent(loadingPanel, "", false)
                updateToolWindowContent(project, toolWindow, initialContent, clientUrl)
            }
            is ChatWindowCommand.ReloadChatWindow -> {
                // Reload current chat window
                val currentContent = toolWindow.contentManager.selectedContent
                if (currentContent?.component is ChatViewerWindow) {
                    // TODO: Implement reload
                }
            }
            is ChatWindowCommand.FocusEditor -> {
                // Focus the editor
                val currentContent = toolWindow.contentManager.selectedContent
                if (currentContent?.component is ChatViewerWindow) {
                    // TODO: Implement focus
                }
            }
            is ChatWindowCommand.SendToWebView -> {
                // Send data to web view
                val currentContent = toolWindow.contentManager.selectedContent
                if (currentContent?.component is ChatViewerWindow) {
                    // TODO: Implement send to webview
                }
            }
            is ChatWindowCommand.RefreshChatWindow -> {
                // Refresh the entire chat window
                val clientUrl = "www.jetbrains.com"
                val loadingPanel = createLoadingPanel()
                val initialContent = ContentFactory.getInstance().createContent(loadingPanel, "", false)
                updateToolWindowContent(project, toolWindow, initialContent, clientUrl)
            }
        }
    }

    private fun createLoadingPanel(): JPanel {
        val panel = JPanel(BorderLayout())
        val label = JLabel("Loading chat...", JLabel.CENTER)
        panel.add(label, BorderLayout.CENTER)
        return panel
    }

    private fun showErrorPanel(toolWindow: ToolWindow, initialContent: Content, message: String) {
        try {
            toolWindow.contentManager.removeContent(initialContent, true)
            val errorPanel = JPanel(BorderLayout())
            val errorLabel = JLabel("<html><div style='text-align: center;'>$message</div></html>", JLabel.CENTER)
            errorPanel.add(errorLabel, BorderLayout.CENTER)
            val errorContent = ContentFactory.getInstance().createContent(errorPanel, "", false)
            toolWindow.contentManager.addContent(errorContent)
        } catch (e: Exception) {
            logger.error("Error showing error panel", e)
        }
    }

    // Commented out due to backend dependencies
    // private fun updateToolWindowIcon(toolWindow: ToolWindow) {
    //     toolWindow.setIcon(
    //         if (PluginVersionManager.hasCascade()) {
    //             CodeiumIcons.getCascadeIcon()
    //         } else {
    //             CodeiumIcons.WindsurfIcon
    //         }
    //     )
    // }
}
