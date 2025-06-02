package com.example.splitplugin.backend

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import kotlinx.coroutines.CoroutineScope

@Service(Service.Level.APP)
internal class BackendCoroutineScopeProvider(val coroutineScope: CoroutineScope) {
    companion object {
        fun getInstance(): BackendCoroutineScopeProvider {
            return ApplicationManager.getApplication().getService(BackendCoroutineScopeProvider::class.java)
        }
    }
}