package com.example.splitplugin.backend

import com.example.splitplugin.shared.SplitPluginRpcApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal class BackendRpcApiImpl : SplitPluginRpcApi {
    override suspend fun getSomeHeavyComputationResultsFlow(): Flow<Int> {
        // pretend we do some heavy backend-specific stuff here

        return flow {
            for (value in generateSequence(0, Int::inc)) {
                emit(value)
                delay(500)
            }
        }
    }
}