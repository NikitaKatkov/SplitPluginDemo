package com.example.splitplugin.frontend.compose.toolWindow

import androidx.compose.runtime.LaunchedEffect
import com.example.splitplugin.frontend.compose.CoroutineScopeHolder
import com.example.splitplugin.frontend.compose.chatApp.ChatAppSample
import com.example.splitplugin.frontend.compose.chatApp.viewmodel.ChatViewModel
import com.example.splitplugin.frontend.compose.weatherApp.model.Location
import com.example.splitplugin.frontend.compose.weatherApp.services.LocationsProvider
import com.example.splitplugin.frontend.compose.weatherApp.services.WeatherForecastService
import com.example.splitplugin.frontend.compose.weatherApp.ui.WeatherAppSample
import com.example.splitplugin.frontend.compose.weatherApp.ui.WeatherAppViewModel
import com.example.splitplugin.shared.ChatRepositoryApi
import com.intellij.openapi.components.service
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import kotlinx.coroutines.launch
import org.jetbrains.jewel.bridge.addComposeTab

class FrontendComposeSamplesToolWindowFactory : ToolWindowFactory, DumbAware {
    override fun shouldBeAvailable(project: Project) = true

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        CoroutineScopeHolder.getInstance(project).getPluginScope().launch {
            weatherApp(project, toolWindow)
            chatApp(project, toolWindow)
        }
    }

    private fun weatherApp(project: Project, toolWindow: ToolWindow) {
        // create ViewModel once per tool window
        val viewModel = WeatherAppViewModel(
            listOf(Location("Munich", "Germany")),
            project.service<CoroutineScopeHolder>()
                .createScope(::WeatherAppViewModel.name),
            WeatherForecastService()
        )
        Disposer.register(toolWindow.disposable, viewModel)

        toolWindow.addComposeTab("Weather App") {
            LaunchedEffect(Unit) {
                viewModel.onReloadWeatherForecast()
            }

            WeatherAppSample(
                viewModel,
                viewModel,
                service<LocationsProvider>()
            )
        }
    }

    private suspend fun chatApp(project: Project, toolWindow: ToolWindow) {
        val viewModel = ChatViewModel(
            project.service<CoroutineScopeHolder>()
                .createScope(ChatViewModel::class.java.simpleName),
            ChatRepositoryApi.getInstanceAsync() // todo remote api now
        )
        Disposer.register(toolWindow.disposable, viewModel)

        toolWindow.addComposeTab("Chat App") { ChatAppSample(viewModel) }
    }
}
