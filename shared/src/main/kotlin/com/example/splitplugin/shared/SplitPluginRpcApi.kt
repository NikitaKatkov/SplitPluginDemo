package com.example.splitplugin.shared

import com.intellij.ide.vfs.VirtualFileId
import com.intellij.platform.rpc.RemoteApiProviderService
import fleet.rpc.RemoteApi
import fleet.rpc.Rpc
import fleet.rpc.remoteApiDescriptor
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable

@Rpc
interface SplitPluginRpcApi : RemoteApi<Unit> {
    companion object {
        suspend fun getInstanceAsync(): SplitPluginRpcApi {
            return RemoteApiProviderService.resolve(remoteApiDescriptor<SplitPluginRpcApi>())
        }
    }

    suspend fun getSomeHeavyComputationResultsFlow(): Flow<ComputationResult>
    suspend fun updateBackendState(request: UpdateBackendStateRequest)
}

@Serializable
data class ComputationResult(val value: Int, val id: String? = null)

@Serializable
sealed interface UpdateBackendStateRequest {
    @Serializable
    data class DecreaseCounter(val value: Int) : UpdateBackendStateRequest

    @Serializable
    data class IncreaseCounter(val value: Int, val fileId: VirtualFileId?) : UpdateBackendStateRequest

    @Serializable
    data class StartPortForwarding(val backendPort: Int) : UpdateBackendStateRequest
}