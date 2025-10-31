package com.example.splitplugin.frontend.compose.toolWindow

import com.example.splitplugin.frontend.compose.CoroutineScopeHolder
import com.example.splitplugin.frontend.compose.chatApp.ChatAppSample
import com.example.splitplugin.frontend.compose.chatApp.viewmodel.ChatViewModel
import com.example.splitplugin.frontend.compose.chatApp.viewmodel.FrontendChatRepositoryModel
import com.intellij.openapi.components.service
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import org.jetbrains.jewel.bridge.addComposeTab

class FrontendComposeSamplesToolWindowFactory : ToolWindowFactory, DumbAware {
    override fun shouldBeAvailable(project: Project) = true

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        chatApp(project, toolWindow)
    }

    private fun chatApp(project: Project, toolWindow: ToolWindow) {
        val viewModel = ChatViewModel(
            project.service<CoroutineScopeHolder>()
                .createScope(ChatViewModel::class.java.simpleName),
            FrontendChatRepositoryModel.getInstance(project)
        )
        Disposer.register(toolWindow.disposable, viewModel)

        toolWindow.addComposeTab("Chat App") { ChatAppSample(viewModel) }
    }
}
