package com.example.splitplugin.frontend

import com.example.splitplugin.shared.SplitPluginRpcApi
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch
import javax.swing.JLabel

private class FrontendTestToolwindowFactory : ToolWindowFactory {
    private val LOG = logger<FrontendTestToolwindowFactory>()

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        LOG.warn("Creating new FrontendTestToolwindowFactory instance")

        val label = JLabel("Wait for backend RPC service to respond..")
        val csProvider = FrontendCoroutineScopeProvider.getInstance()
        LOG.warn("Obtained FrontendCoroutineScopeProvider instance")
        csProvider.scope.launch(start = CoroutineStart.UNDISPATCHED) {
            LOG.warn("Starting new coroutine in FrontendTestToolwindowFactory instance")
            val service = SplitPluginRpcApi.getInstanceAsync()
            LOG.warn("Obtained SplitPluginRpcApi instance")
            service.getSomeHeavyComputationResultsFlow().collect {
                LOG.warn("Received value from backend: $it")
                label.text = "Received value from backend: $it"
            }
        }

        LOG.warn("Finished creating new FrontendTestToolwindowFactory instance")
        val contentFactory = toolWindow.contentManager.factory
        val content = contentFactory.createContent(label, null, false)
        toolWindow.contentManager.addContent(content)
        LOG.warn("Finished creating tool window content")
    }
}