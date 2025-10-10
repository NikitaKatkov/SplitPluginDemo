package com.example.splitplugin.frontend

import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory

private val LOG = logger<FrontendTestToolwindowFactory>()

private class FrontendTestToolwindowFactory : ToolWindowFactory, DumbAware {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        LOG.warn("Creating new FrontendTestToolwindowFactory instance")
        val contentFactory = toolWindow.contentManager.factory
        val content = contentFactory.createContent(SimpleInteractivePanel(), null, false)
        toolWindow.contentManager.addContent(content)
        LOG.warn("Finished creating tool window content")
    }
}