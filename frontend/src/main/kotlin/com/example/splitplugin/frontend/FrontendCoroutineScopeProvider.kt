package com.example.splitplugin.frontend

import com.intellij.openapi.components.Service
import kotlinx.coroutines.CoroutineScope

@Service(Service.Level.PROJECT)
internal class FrontendCoroutineScopeProvider(val scope: CoroutineScope) {
    companion object {
        fun getInstance(project: com.intellij.openapi.project.Project): FrontendCoroutineScopeProvider {
            return project.getService(FrontendCoroutineScopeProvider::class.java)
        }
    }
}