package com.example.splitplugin.frontend.jcef

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.jcef.JBCefBrowserBuilder
import org.cef.browser.CefBrowser
import org.cef.browser.CefFrame
import org.cef.handler.CefRequestHandlerAdapter
import org.cef.handler.CefResourceRequestHandler
import org.cef.handler.CefResourceRequestHandlerAdapter
import org.cef.misc.BoolRef
import org.cef.network.CefRequest
import org.cef.network.CefResponse
import org.cef.network.CefURLRequest
import java.awt.BorderLayout
import java.awt.FlowLayout
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField

class ChatUrlWindowFactory : ToolWindowFactory {
    private val logger = Logger.getInstance(ChatToolWindowFactory::class.java)

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        // Create top panel for URL input
        val topPanel = JPanel(BorderLayout())
        val urlField = JTextField("http://0.0.0.0:8000/")
        topPanel.add(JLabel("URL: "), BorderLayout.WEST)
        topPanel.add(urlField, BorderLayout.CENTER)

        // Create bottom panel for buttons
        val buttonsPanel = JPanel(FlowLayout(FlowLayout.RIGHT))
        val devToolsButton = JButton("Open DevTools")
        val showButtonsButton = JButton("Show Buttons")

        // Add show buttons click handler
        showButtonsButton.addActionListener {
//      project.service<ButtonDemoService>().showButtons()
        }

        // Add all buttons to the buttons panel
        buttonsPanel.add(showButtonsButton)
        buttonsPanel.add(devToolsButton)

        // Create header panel to hold both URL and buttons panels
        val headerPanel = JPanel(BorderLayout())
        headerPanel.add(topPanel, BorderLayout.NORTH)
        headerPanel.add(buttonsPanel, BorderLayout.CENTER)

        // Create browser
        val browser = JBCefBrowserBuilder()
            .setEnableOpenDevToolsMenuItem(true)
            .build()
            .apply {
                // Set longer timeouts for remote development
                System.setProperty(
                    "jcef.browser.args",
                    "--disable-features=TranslateUI --host-resolver-retry-attempts=3 --service-worker-fetch-timeout=60000 --dns-retry-timeout=60000"
                )

                jbCefClient.addRequestHandler(object : CefRequestHandlerAdapter() {
                    override fun onBeforeBrowse(
                        browser: CefBrowser?,
                        frame: CefFrame?,
                        request: CefRequest?,
                        user_gesture: Boolean,
                        is_redirect: Boolean
                    ): Boolean {
                        request?.let {
                            logger.info("Browsing to URL: ${it.url}")
                        }
                        return false // false to allow the navigation
                    }

                    override fun getResourceRequestHandler(
                        browser: CefBrowser,
                        frame: CefFrame?,
                        request: CefRequest,
                        isNavigation: Boolean,
                        isDownload: Boolean,
                        requestInitiator: String?,
                        disableDefaultHandling: BoolRef
                    ): CefResourceRequestHandler? {
                        return object : CefResourceRequestHandlerAdapter() {
                            override fun onResourceLoadComplete(
                                browser: CefBrowser?,
                                frame: CefFrame?,
                                request: CefRequest?,
                                response: CefResponse?,
                                status: CefURLRequest.Status,
                                receivedContentLength: Long
                            ) {
                                logger.info("STATUS: $status")
                            }
                        }
                    }
                }, this.cefBrowser)
            }

        // Add URL input handler
        urlField.addActionListener {
            val targetUrl = urlField.text
            thisLogger().error("Target URL: $targetUrl")
            browser.loadURL(targetUrl)
        }

        // Add dev tools button handler
        devToolsButton.addActionListener {
            logger.info("Opening DevTools")
            browser.openDevtools()
        }

        // Create main panel with URL input and browser
        val mainPanel = JPanel(BorderLayout())
        mainPanel.add(headerPanel, BorderLayout.NORTH)
        mainPanel.add(browser.component, BorderLayout.CENTER)

        // Add to tool window
        val contentFactory = ContentFactory.getInstance()
        val content = contentFactory.createContent(mainPanel, "", false)
        toolWindow.contentManager.addContent(content)
    }

    override fun shouldBeAvailable(project: Project) = true

    override fun isApplicable(project: Project): Boolean {
        return true
    }
}