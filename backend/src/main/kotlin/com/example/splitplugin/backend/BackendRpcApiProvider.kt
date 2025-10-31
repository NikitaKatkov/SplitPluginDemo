@file:Suppress("UnstableApiUsage")

package com.example.splitplugin.backend

import com.example.splitplugin.shared.ChatRepositoryRpcApi
import com.intellij.platform.rpc.backend.RemoteApiProvider
import fleet.rpc.remoteApiDescriptor

private class BackendRpcApiProvider : RemoteApiProvider {

    override fun RemoteApiProvider.Sink.remoteApis() {
        remoteApi(remoteApiDescriptor<ChatRepositoryRpcApi>()) {
            BackendChatRepositoryRpcApi()
        }
    }
}