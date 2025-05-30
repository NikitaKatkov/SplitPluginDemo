package com.example.splitplugin.backend

import com.example.splitplugin.shared.SplitPluginRpcApi
import com.intellij.platform.rpc.backend.RemoteApiProvider
import fleet.rpc.remoteApiDescriptor

private class BackendRpcApiProvider : RemoteApiProvider {
    override fun RemoteApiProvider.Sink.remoteApis() {
        remoteApi(remoteApiDescriptor<SplitPluginRpcApi>()) {
            BackendRpcApiImpl()
        }
    }
}