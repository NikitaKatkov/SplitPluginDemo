package com.example.splitplugin.frontend

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import kotlinx.coroutines.CoroutineScope

@Service(Service.Level.APP)
internal class FrontendCoroutineScopeProvider(val scope: CoroutineScope) {
    companion object {
        fun getInstance(): FrontendCoroutineScopeProvider {
            return ApplicationManager.getApplication().getService(FrontendCoroutineScopeProvider::class.java)
        }
    }
}