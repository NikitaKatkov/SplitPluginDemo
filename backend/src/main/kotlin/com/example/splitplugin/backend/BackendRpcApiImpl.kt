package com.example.splitplugin.backend

import com.example.splitplugin.shared.ComputationResult
import com.example.splitplugin.shared.SplitPluginRpcApi
import com.example.splitplugin.shared.UpdateBackendStateRequest
import com.intellij.ide.vfs.virtualFile
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.diagnostic.logger
import com.intellij.platform.util.coroutines.childScope
import com.jetbrains.rd.platform.codeWithMe.portForwarding.PortType
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random
import com.jetbrains.rdserver.portForwarding.internal.GlobalPortForwardingManagerImpl

private val LOG: Logger = logger<BackendRpcApiImpl>()

internal class BackendRpcApiImpl : SplitPluginRpcApi {
    private val backendState = MutableStateFlow(ComputationResult(0))
    private val backendOperationsScope =
        BackendCoroutineScopeProvider.getInstance().coroutineScope.childScope("Backend Operations")

    override suspend fun getSomeHeavyComputationResultsFlow(): Flow<ComputationResult> {
        LOG.warn("Starting heavy computation flow")
        // pretend we do some heavy backend-specific stuff here
        startEmittingValuesToFlowWithRandomDelay()
        return backendState
    }

    override suspend fun updateBackendState(request: UpdateBackendStateRequest) {
        when (request) {
            is UpdateBackendStateRequest.DecreaseCounter -> {
                LOG.warn("Decreasing counter by ${request.value}")
                backendState.update { previousResult ->
                    ComputationResult(previousResult.value - request.value)
                }
            }

            is UpdateBackendStateRequest.IncreaseCounter -> {
                LOG.warn("Increasing counter by ${request.value}")
                backendState.update { previousResult ->
                    ComputationResult(previousResult.value + request.value, request.fileId?.virtualFile()?.name)
                }
            }

            is UpdateBackendStateRequest.StartPortForwarding -> {
                LOG.warn("Start port forwarding")
                GlobalPortForwardingManagerImpl.getInstance().tryForwardPort(8080, PortType.TCP) {}
            }
        }
    }

    private fun startEmittingValuesToFlowWithRandomDelay() {
        LOG.warn("Canceling previous emission")
        backendOperationsScope.coroutineContext.cancelChildren()

        LOG.warn("Set backend state to default value")
        backendState.value = ComputationResult(0)

        val newDelay = Random(System.currentTimeMillis()).nextLong(300, 2000)
        LOG.warn("Starting flow emission with delay $newDelay ms")
        backendOperationsScope.launch {
            LOG.warn("Initializing flow emission")
            while (true) {
                delay(newDelay)
                backendState.update { oldResult ->
                    ComputationResult(oldResult.value + 1)
                }
            }
        }
    }
}