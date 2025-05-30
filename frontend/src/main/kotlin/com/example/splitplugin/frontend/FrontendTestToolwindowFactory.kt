package com.example.splitplugin.frontend

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.platform.util.coroutines.childScope

private class FrontendTestToolwindowFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val scope = FrontendCoroutineScopeProvider.getInstance(project)
            .scope.childScope("Backend rpc subscription")

        val contentFactory = toolWindow.contentManager.factory
        val label = FlowLabel(scope)
        val content = contentFactory.createContent(label, null, false)
        toolWindow.contentManager.addContent(content)
    }
}