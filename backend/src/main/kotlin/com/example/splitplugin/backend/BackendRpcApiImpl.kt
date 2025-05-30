package com.example.splitplugin.backend

import com.example.splitplugin.shared.SplitPluginRpcApi
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.diagnostic.logger
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal class BackendRpcApiImpl : SplitPluginRpcApi {
    private val LOG: Logger = logger<BackendRpcApiImpl>()

    override suspend fun getSomeHeavyComputationResultsFlow(): Flow<Int> {
        LOG.warn("Starting heavy computation flow")
        // pretend we do some heavy backend-specific stuff here

        return flow {
            LOG.warn("Initializing flow emission")
            var count = 0
            for (value in generateSequence(0, Int::inc)) {
                LOG.debug("Emitting value: $value")
                emit(value)
                delay(500)
                count++

                if (count % 10 == 0) {
                    LOG.warn("Emitted $count values so far")
                }
            }
        }
    }
}