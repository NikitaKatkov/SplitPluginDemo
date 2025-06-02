package com.example.splitplugin.backend

import com.example.splitplugin.shared.SplitPluginRpcApi
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.platform.rpc.backend.RemoteApiProvider
import fleet.rpc.remoteApiDescriptor

private val LOG: Logger = logger<BackendRpcApiProvider>()

private class BackendRpcApiProvider : RemoteApiProvider {

    override fun RemoteApiProvider.Sink.remoteApis() {
        LOG.warn("Registering SplitPluginRpcApi")
        remoteApi(remoteApiDescriptor<SplitPluginRpcApi>()) {
            LOG.warn("Creating new BackendRpcApiImpl instance")
            BackendRpcApiImpl()
        }
    }
}