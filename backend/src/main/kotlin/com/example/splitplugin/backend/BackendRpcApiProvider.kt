package com.example.splitplugin.backend

import com.example.splitplugin.shared.SplitPluginRpcApi
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.platform.rpc.backend.RemoteApiProvider
import fleet.rpc.remoteApiDescriptor

private class BackendRpcApiProvider : RemoteApiProvider {
    private val LOG: Logger = thisLogger<BackendRpcApiProvider>()

    override fun RemoteApiProvider.Sink.remoteApis() {
        LOG.warn("Registering SplitPluginRpcApi")
        remoteApi(remoteApiDescriptor<SplitPluginRpcApi>()) {
            LOG.warn("Creating new BackendRpcApiImpl instance")
            BackendRpcApiImpl()
        }
    }
}