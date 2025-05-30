package com.example.splitplugin.frontend

import com.example.splitplugin.shared.SplitPluginRpcApi
import com.intellij.openapi.application.EDT
import com.intellij.openapi.diagnostic.logger
import com.intellij.ui.components.JBLabel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class FlowLabel(
    scope: CoroutineScope,
) : JBLabel() {

    private val LOG = logger<FlowLabel>()

    init {
        text = "Waiting for values..."
        LOG.warn("FlowLabel initialized")

        scope.launch {
            LOG.warn("Starting to collect flow values")
            SplitPluginRpcApi.getInstanceAsync().getSomeHeavyComputationResultsFlow().collect {
                LOG.warn("Received value: $it")
                withContext(Dispatchers.EDT) {
                    text = it.toString()
                    revalidate()
                    repaint()
                }
            }
        }
    }
}