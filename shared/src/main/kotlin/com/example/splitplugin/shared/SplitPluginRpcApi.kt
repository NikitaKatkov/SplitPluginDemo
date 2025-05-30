package com.example.splitplugin.shared

import com.intellij.platform.rpc.RemoteApiProviderService
import fleet.rpc.RemoteApi
import fleet.rpc.Rpc
import fleet.rpc.remoteApiDescriptor
import kotlinx.coroutines.flow.Flow

@Rpc
interface SplitPluginRpcApi : RemoteApi<Unit> {
    companion object {
        suspend fun getInstanceAsync(): SplitPluginRpcApi {
            return RemoteApiProviderService.resolve(remoteApiDescriptor<SplitPluginRpcApi>())
        }
    }

    suspend fun getSomeHeavyComputationResultsFlow(): Flow<Int>
}