package com.example.splitplugin.frontend

import com.example.splitplugin.shared.SplitPluginRpcApi
import com.intellij.ui.components.JBLabel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal class FlowLabel(
    scope: CoroutineScope,
) : JBLabel() {

    init {
        text = "Waiting for values..."

        scope.launch {
            SplitPluginRpcApi.getInstanceAsync().getSomeHeavyComputationResultsFlow().collect {
                text = it.toString()
            }
        }
    }
}
