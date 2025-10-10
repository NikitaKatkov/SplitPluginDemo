package com.example.splitplugin.frontend

import com.example.splitplugin.shared.SplitPluginRpcApi
import com.example.splitplugin.shared.UpdateBackendStateRequest
import com.intellij.openapi.diagnostic.logger
import com.intellij.util.ui.components.BorderLayoutPanel
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch
import javax.swing.JButton
import javax.swing.JLabel

private val LOG = logger<SimpleInteractivePanel>()

internal class SimpleInteractivePanel : BorderLayoutPanel() {
    init {
        val csProvider = FrontendCoroutineScopeProvider.getInstance()
        val label = JLabel("Wait for backend RPC service to respond..")
        val buttonIncreaseCounter = JButton().apply {
            text = "Increase counter"
            addActionListener {
                csProvider.scope.launch {
                    SplitPluginRpcApi.getInstanceAsync()
                        .updateBackendState(UpdateBackendStateRequest.IncreaseCounter(1000))
                }
            }
        }
        val buttonDecreaseCounter = JButton().apply {
            text = "Decrease counter"
            addActionListener {
                csProvider.scope.launch {
                    SplitPluginRpcApi.getInstanceAsync()
                        .updateBackendState(UpdateBackendStateRequest.DecreaseCounter(1000))
                }
            }
        }
        val buttonsPanel = BorderLayoutPanel()
        buttonsPanel.addToLeft(buttonDecreaseCounter)
        buttonsPanel.addToRight(buttonIncreaseCounter)

        addToCenter(label)
        addToBottom(buttonsPanel)

        csProvider.scope.launch(start = CoroutineStart.UNDISPATCHED) {
            LOG.warn("Starting new coroutine in FrontendTestToolwindowFactory instance")
            SplitPluginRpcApi.getInstanceAsync().getSomeHeavyComputationResultsFlow().collect {
                LOG.warn("Received value from backend: $it")
                label.text = "Received value from backend: ${it.value}, id: ${it.id}"
            }
        }
    }
}