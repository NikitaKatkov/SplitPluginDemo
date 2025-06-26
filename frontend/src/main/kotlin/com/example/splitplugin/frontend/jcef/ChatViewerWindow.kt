/*
 * Copyright Exafunction, Inc.
 */

package com.example.splitplugin.frontend.jcef

// Commented out backend imports
// import com.codeium.intellij.backend.NotificationContentType
// import com.codeium.intellij.backend.askToOpenSettings
// import com.codeium.intellij.backend.auth.CodeiumAuthService
// import com.codeium.intellij.backend.diff.DiffView
// import com.codeium.intellij.backend.jcef.*
// import com.codeium.intellij.backend.jcef.ExternalBrowserLoadListener
// import com.codeium.intellij.backend.jcef.ExternalBrowserTopics
// import com.codeium.intellij.backend.settings.AppSettingsState
// import com.codeium.intellij.backend.utilities.BrowserHandler
// import com.codeium.intellij.backend.utilities.OSManager
// import com.codeium.intellij.backend.versioning.PluginVersionManager

import com.google.gson.Gson
import com.intellij.ide.BrowserUtil
import com.intellij.ide.ui.LafManagerListener
import com.intellij.ide.ui.UISettingsListener
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.editor.colors.EditorColors
import com.intellij.openapi.editor.colors.EditorColorsListener
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.colors.EditorFontType
import com.intellij.openapi.editor.impl.EditorComponentImpl
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.IdeFocusManager
import com.intellij.ui.JBColor
import com.intellij.ui.jcef.*
import com.intellij.ui.scale.JBUIScale
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import java.awt.Color
import java.net.URLEncoder
import javax.swing.JComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.cef.browser.CefBrowser
import org.cef.browser.CefFrame
import org.cef.handler.CefLoadHandler
import org.cef.handler.CefLoadHandlerAdapter
import org.cef.handler.CefRequestHandlerAdapter
import org.cef.network.CefRequest
import org.intellij.lang.annotations.Language
import kotlin.collections.iterator

const val FONT_SIZE_THRESHOLD = 30.0f

fun convertToUrlSearchParams(params: Map<String, String>): String {
  val searchParams = StringBuilder()
  for ((key, value) in params) {
    val encodedKey = URLEncoder.encode(key, "UTF-8")
    val encodedValue = URLEncoder.encode(value, "UTF-8")
    searchParams.append("$encodedKey=$encodedValue&")
  }
  return searchParams.toString().dropLast(1)
}

fun isLightTheme(): Boolean {
  return JBColor.isBright()
}

class ChatViewerWindow(
    private val project: Project,
    private val useExternalBrowser: Boolean = false,
    private val clientUrl: String
) : Disposable {

  private val cefClient: Any? =
      when (useExternalBrowser) {
        true -> null
        false -> JBCefApp.getInstance().createClient()
      }
  
  // TODO: Uncomment when ExternalJcefBrowser is available in frontend
  private val anyBrowser: Any =
      when (useExternalBrowser) {
        true ->
            // ExternalJcefBrowserBuilder()
            //     .setEnableOpenDevToolsMenuItem(true)
            //     .apply {
            //       // Only enable off-screen rendering for Linux to fix focus issues
            //       // See: https://github.com/Exafunction/Exafunction/pull/10774
            //       // Keep it disabled for Mac to prevent backspace issues in Rider
            //       if (!OSManager.isLinux) {
            //         setOffScreenRendering(false)
            //       }
            //     }
            //     .setUrl(clientUrl)
            //     .build()
            JBCefBrowserBuilder()
                .setClient(cefClient as JBCefClient)
                .setEnableOpenDevToolsMenuItem(true)
                .setUrl(clientUrl)
                .build()
        false ->
            JBCefBrowserBuilder()
                .setClient(cefClient as JBCefClient)
                .setEnableOpenDevToolsMenuItem(true)
                // .apply {
                //   if (!OSManager.isLinux) {
                //     setOffScreenRendering(false)
                //   }
                // }
                .setUrl(clientUrl)
                .build()
      }

  private val browser =
      when (useExternalBrowser) {
        // true -> anyBrowser as ExternalJcefBrowser
        true -> anyBrowser as JBCefBrowser  // Fallback for now
        false -> anyBrowser as JBCefBrowser
      }

  private val browserLoadedState: MutableStateFlow<Boolean> = MutableStateFlow(false)
  private val browserLoadHandler: CefLoadHandler
  // private val diffView = if (PluginVersionManager.hasCascade()) DiffView(project) else null

  // private val codeiumAuthService: CodeiumAuthService = service<CodeiumAuthService>()
  private val logger = logger<ChatViewerWindow>()
  private val loginQuery =
      when (useExternalBrowser) {
        true -> null
        false -> JBCefJSQuery.create(browser as JBCefBrowserBase)
      }
  private val openGenericUrlQuery =
      when (useExternalBrowser) {
        true -> null
        false -> JBCefJSQuery.create(browser as JBCefBrowserBase)
      }

  init {
    // Set page background color based on browser type
    when (browser) {
      // is ExternalJcefBrowser -> null
      is JBCefBrowser -> browser.setPageBackgroundColor("#aaa")
      else -> throw IllegalArgumentException("Unsupported browser type: ${browser::class}")
    }
    configureBrowserNavigation(browser)

    // Initialize the browser URL.
    CoroutineScope(Dispatchers.IO).launch { loadClient() }

    browserLoadHandler =
        object : CefLoadHandlerAdapter() {
          override fun onLoadEnd(browser: CefBrowser, frame: CefFrame, httpStatusCode: Int) {
            logger.info("[CHAT-BROWSER] onLoadEnd called - frame.isMain: ${frame.isMain}, httpStatusCode: $httpStatusCode, URL: ${frame.url}")
            if (frame.isMain) {
              logger.info("[CHAT-BROWSER] Main frame loaded successfully with status: $httpStatusCode for URL: ${frame.url}")
              browserLoadedState.value = true
              reloadStyles()

              if (!useExternalBrowser) {
                addChatCallBack(loginQuery, "login") { 
                  // codeiumAuthService.login(project) {} 
                  logger.info("Login callback triggered")
                }
              }

              if (!useExternalBrowser) {
                addChatCallBack(openGenericUrlQuery, "openGenericUrl") { link ->
                  BrowserUtil.browse(link)
                }
              }

              // Workaround for JCEF bugs
              // if (OSManager.isLinux) {
              //   val keyboardHandlingJS = BrowserHandler.getLinuxJCEFKeyHandlerJS()
              //   browser.executeJavaScript(keyboardHandlingJS, browser.url, 0)
              // } else if (OSManager.isWindows) {
              //   val keyboardHandlingJS = BrowserHandler.getWindowsJCEFKeyHandlerJS()
              //   browser.executeJavaScript(keyboardHandlingJS, browser.url, 0)
              // }
            }
          }
        }

    // Now attach the handler based on browser type
    when (browser) {
      // is ExternalJcefBrowser -> null
      is JBCefBrowser -> {
        (cefClient as JBCefClient).addLoadHandler(browserLoadHandler, browser.cefBrowser)
      }
      else -> throw IllegalArgumentException("Unsupported browser type: ${browser::class}")
    }

    // Subscribe to theme changes
    val connection = ApplicationManager.getApplication().messageBus.connect()
    connection.subscribe(LafManagerListener.TOPIC, LafManagerListener { reloadStyles() })

    // Subscribe to theme changes
    connection.subscribe(EditorColorsManager.TOPIC, EditorColorsListener { reloadStyles() })

    // Subscribe to settings changes
    connection.subscribe(UISettingsListener.TOPIC, UISettingsListener { reloadStyles() })

    // Subscribe to chat zoom level changes
    // connection.subscribe(
    //     AppSettingsTopics.CHAT_ZOOM_LEVEL_CHANGED,
    //     object : AppSettingsChangeListener {
    //       override fun onChatZoomLevelChanged(newZoomLevel: Int) {
    //         reloadStyles()
    //       }
    //     })

    // Subscribe to external browser loaded events
    // connection.subscribe(
    //     ExternalBrowserTopics.EXTERNAL_BROWSER_LOADED,
    //     object : ExternalBrowserLoadListener {
    //       override fun onExternalBrowserLoaded() {
    //         reloadStyles()
    //       }
    //     })
  }

  /** Helper function to configure handlers for external links */
  private fun configureBrowserNavigation(browser: Disposable) {

    val localhostPrefix = "http://127.0.0.1"
    val blobPrefix = "blob:"

    // Script to handle window.open calls
    val openExternalLinkScript =
        """
        const originalWindowOpen = window.open;
        window.open = (url, target) => {
            if (!url.startsWith('$localhostPrefix') && !url.startsWith('$blobPrefix')) {
                window.openGenericUrl(url);
                return null;
            }
            return originalWindowOpen(url, target);
        };
    """
            .trimIndent()

    // Handle regular link clicks
    when (browser) {
      // is ExternalJcefBrowser ->
      //     browser.addRequestHandler(
      //         object : CefRequestHandlerAdapter() {
      //           override fun onBeforeBrowse(
      //               browser: CefBrowser?,
      //               frame: CefFrame?,
      //               request: CefRequest?,
      //               user_gesture: Boolean,
      //               is_redirect: Boolean
      //           ): Boolean {
      //             val url = request?.url
      //             if (url != null &&
      //                 !url.startsWith(localhostPrefix) &&
      //                 !url.startsWith(blobPrefix)) {
      //               BrowserUtil.browse(url)
      //               return true
      //             }
      //             return false
      //           }
      //         })
      is JBCefBrowser ->
          (cefClient as JBCefClient).addRequestHandler(
              object : CefRequestHandlerAdapter() {
                override fun onBeforeBrowse(
                    browser: CefBrowser?,
                    frame: CefFrame?,
                    request: CefRequest?,
                    user_gesture: Boolean,
                    is_redirect: Boolean
                ): Boolean {
                  val url = request?.url
                  if (url != null &&
                      !url.startsWith(localhostPrefix) &&
                      !url.startsWith(blobPrefix)) {
                    BrowserUtil.browse(url)
                    return true
                  }
                  return false
                }
              },
              browser.cefBrowser)
    }
    // Handle window.open calls
    when (browser) {
      // is ExternalJcefBrowser ->
      //     browser.addLoadHandler(
      //         object : CefLoadHandlerAdapter() {
      //           override fun onLoadEnd(
      //               browser: CefBrowser?,
      //               frame: CefFrame?,
      //               httpStatusCode: Int
      //           ) {
      //             if (frame?.isMain == true) {
      //               frame.executeJavaScript(openExternalLinkScript, frame.url, 0)
      //             }
      //           }
      //         })
      is JBCefBrowser ->
          browser.jbCefClient.addLoadHandler(
              object : CefLoadHandlerAdapter() {
                override fun onLoadEnd(
                    browser: CefBrowser?,
                    frame: CefFrame?,
                    httpStatusCode: Int
                ) {
                  if (frame?.isMain == true) {
                    frame.executeJavaScript(openExternalLinkScript, frame.url, 0)
                  }
                }
              },
              browser.cefBrowser)
    }
  }

  fun sendToWebView(map: Map<String, Any>) {
    val gson = Gson()
    when (browser) {
      // is ExternalJcefBrowser ->
      //     browser.cefBrowser?.executeJavaScript(
      //         "window.postMessage(${gson.toJson(map)})", browser.cefBrowser?.url ?: "", 0)
      is JBCefBrowser ->
          browser.cefBrowser.executeJavaScript(
              "window.postMessage(${gson.toJson(map)})", browser.cefBrowser.url, 0)
      else -> throw IllegalArgumentException("Unsupported browser type: ${browser::class}")
    }
  }

  /** Adds a callback so the chat window can communicate to extension */
  private fun addChatCallBack(query: Any?, name: String, handler: (String) -> Unit) {
    when (query) {
      is JBCefJSQuery -> {
        query.addHandler { message ->
          handler(message)
          null
        }
        when (browser) {
          is JBCefBrowser ->
              browser.cefBrowser.executeJavaScript(
                  "window.$name = function(message) {" + query.inject("message") + "};",
                  browser.cefBrowser.url,
                  0)
          else -> throw IllegalArgumentException("JB JSQuery requires JBCefBrowser")
        }
      }
      null -> throw IllegalArgumentException("Query cannot be null")
      else -> throw IllegalArgumentException("Unsupported query type: ${query!!::class}")
    }
  }

  /** Execute the given JavaScript in the browser. */
  private fun execute(@Language("javascript") script: String) {
    when (browser) {
      // is ExternalJcefBrowser ->
      //     browser.cefBrowser?.executeJavaScript(script, browser.cefBrowser?.url ?: "", 0)
      is JBCefBrowser -> browser.cefBrowser.executeJavaScript(script, browser.cefBrowser.url, 0)
      else -> throw IllegalArgumentException("Unsupported browser type: ${browser::class}")
    }
  }

  val content: JComponent
    get() =
        when (browser) {
          // is ExternalJcefBrowser -> browser.uiComponent
          is JBCefBrowser -> browser.component
          else -> throw IllegalArgumentException("Unsupported browser type: ${browser::class}")
        }

  /** Load the Chat client and attach the appropriate URL search params. */
  private fun loadClient() {
    when (browser) {
      // is ExternalJcefBrowser -> browser.loadURL(clientUrl)
      is JBCefBrowser -> browser.loadURL(clientUrl)
      else -> throw IllegalArgumentException("Unsupported browser type: ${browser::class}")
    }
  }

  /** Reload the Chat client. */
  fun reload() {
    browserLoadedState.value = false
    loadClient()
  }

  override fun dispose() {
    browser.dispose()
  }

  fun focusEditor() {
    when (browser) {
      // is ExternalJcefBrowser -> browser.cefBrowser?.setFocus(true)
      is JBCefBrowser -> browser.cefBrowser.setFocus(true)
      else -> throw IllegalArgumentException("Unsupported browser type: ${browser::class}")
    }
  }

  /** Focuses the chat window and ensures the editor loses focus. */
  fun toggleFocusToBrowser() {
    ApplicationManager.getApplication().invokeLater {
      try {
        val currentFocused = IdeFocusManager.getInstance(project).focusOwner
        if (currentFocused is EditorComponentImpl) {
          // Request focus for browser
          when (browser) {
            // is ExternalJcefBrowser ->
            //     IdeFocusManager.getInstance(project).requestFocus(browser.uiComponent, true)
            is JBCefBrowser ->
                IdeFocusManager.getInstance(project).requestFocus(browser.component, true)
          }
        }
      } catch (e: Exception) {
        logger.warn("Error focusing chat window: $e")
      }
    }
  }

  private fun reloadStyles() {
    val colors = EditorColorsManager.getInstance().schemeForCurrentUITheme

    // Color to RGBA string - converts a Color object to an RGBA string representation for use in
    // CSS styling
    fun colorToRgb(color: Color?): String? {
      if (color == null) {
        return null
      }
      return "rgba(${color.red}, ${color.green}, ${color.blue}, ${color.alpha / 255.0})"
    }

    /**
     * Converts a font size in points to a scaled pixel value.
     *
     * @param fontSizePt The font size in points.
     * @return The scaled pixel value.
     */
    fun getScaledPixelValue(fontSizePt: Float): Float {
      val basePx = fontSizePt * 4f / 3f // Convert pt to px
      // return JBUIScale.scale(basePx) * (AppSettingsState.instance.chatZoomLevel) / 100
      return JBUIScale.scale(basePx) // Simplified for now
    }

    /**
     * Gets the current font size in rem.
     *
     * @return The current font size in rem.
     */
    fun getCurrentFontSize(): Float {
      val editorColorsScheme = EditorColorsManager.getInstance().globalScheme // Get global scheme
      val fontSizeInPoints = editorColorsScheme.getFont(EditorFontType.PLAIN).size2D //
      val fontSizeInPixels = getScaledPixelValue(fontSizeInPoints) // Convert pt to px
      return fontSizeInPixels
    }

    // Check if font size is too large and show scaling notification if needed
    fun checkFontSizeAndNotify(project: Project) {
      val currentFontSize = getCurrentFontSize()
      if (currentFontSize >= FONT_SIZE_THRESHOLD) {
        // askToOpenSettings(project, NotificationContentType.SCALING_ISSUE)
        logger.warn("Font size too large: $currentFontSize")
      }
    }

    class FontInfo(val bodyPx: Float, val rootPx: Float) {}

    /**
     * Computes font sizes for the chat panel's root HTML element and the body HTML element. This
     * relies on some hardcoded ratios that match the style setup in Windsurf, where the default is
     * a 16px root and 13px body.
     */
    fun getFontInfo(): FontInfo {
      val rootPx = getCurrentFontSize() * 12f / 13f // scale factor here is aesthetic
      val bodyPx = rootPx * 13f / 16f // match Windurf's ratio
      return FontInfo(bodyPx, rootPx)
    }

    val fontInfo = getFontInfo()
    checkFontSizeAndNotify(project)

    val isLightTheme = JBColor.isBright()
    val scrollbarColor = if (isLightTheme) "rgb(100 100 100 / 0.4)" else "rgb(121 121 121 / 0.4)"

    // We'll use a translucent gray that tints the background slightly
    val buttonSecondaryBackground =
        if (isLightTheme) "rgb(128 128 128 / 0.4)" else "rgb(100 100 100 / 0.4)"

    val cssColorVarMap =
        mapOf(
            "--codeium-theme-type" to if (isLightTheme) "light" else "dark",
            "--codeium-scrollbarSlider-background" to scrollbarColor,
            "--codeium-chat-background" to colorToRgb(JBColor.PanelBackground),
            "--codeium-editor-background" to colorToRgb(colors.defaultBackground),
            "--codeium-editor-color" to colorToRgb(colors.defaultForeground),
            "--codeium-text-color" to colorToRgb(JBColor.foreground()),
            "--codeium-link-color" to
                colorToRgb(
                    colors.getAttributes(EditorColors.REFERENCE_HYPERLINK_COLOR).foregroundColor),
            "--codeium-link-hover-color" to
                colorToRgb(
                    colors.getAttributes(EditorColors.REFERENCE_HYPERLINK_COLOR).foregroundColor),
            "--codeium-caption-color" to colorToRgb(Color(180, 180, 180)),
            "--codeium-message-block-user-background" to
                colorToRgb(JBUI.CurrentTheme.List.Selection.background(true)),
            "--codeium-message-block-user-color" to
                colorToRgb(JBUI.CurrentTheme.List.Selection.foreground(true)),
            "--codeium-message-block-bot-background" to
                colorToRgb(JBUI.CurrentTheme.List.Selection.background(false)),
            "--codeium-message-block-bot-color" to colorToRgb(JBColor.foreground()),
            "--codeium-input-color" to colorToRgb(UIUtil.getTextFieldForeground()),
            "--codeium-input-placeholder" to
                colorToRgb(UIUtil.getInactiveTextFieldBackgroundColor()),
            "--codeium-input-background" to colorToRgb(UIUtil.getTextFieldBackground()),
            "--codeium-tooltip-background" to
                colorToRgb(JBUI.CurrentTheme.List.Selection.background(false)),
            "--codeium-tooltip-color" to colorToRgb(JBColor.foreground()),
            "--codeium-active-selection-background" to
                colorToRgb(JBUI.CurrentTheme.List.Selection.background(true)),
            "--codeium-active-selection-color" to
                colorToRgb(JBUI.CurrentTheme.List.Selection.foreground(true)),
            "--codeium-badge-background" to
                colorToRgb(JBUI.CurrentTheme.NotificationInfo.backgroundColor()),
            "--codeium-badge-color" to
                colorToRgb(JBUI.CurrentTheme.NotificationInfo.foregroundColor()),
            "--codeium-editor-line-number-color" to
                colorToRgb(colors.getColor(EditorColors.LINE_NUMBER_ON_CARET_ROW_COLOR)),
            "--codeium-button-secondary-hover-background" to
                colorToRgb(JBUI.CurrentTheme.Button.focusBorderColor(false)),
            "--codeium-button-secondary-background" to buttonSecondaryBackground,
            "--codeium-button-secondary-color" to colorToRgb(JBColor.foreground()),
            "--codeium-button-hover-background" to
                colorToRgb(JBUI.CurrentTheme.Button.focusBorderColor(true)),
            "--codeium-button-background" to
                colorToRgb(JBUI.CurrentTheme.GotItTooltip.buttonBackgroundContrast()),
            "--codeium-button-color" to
                colorToRgb(JBUI.CurrentTheme.GotItTooltip.buttonForegroundContrast()),
            "--codeium-notificationsWarningIcon-foreground" to
                colorToRgb(JBUI.CurrentTheme.NotificationInfo.foregroundColor()),
            "--codeium-icon-color" to colorToRgb(JBColor.foreground()),
            "font-size" to "${fontInfo.rootPx}px",
            "--codeium-font-size" to "${fontInfo.bodyPx}px")

    // Set CSS variables in the browser context.
    val jsCommands = StringBuilder()
    for ((key, value) in cssColorVarMap) {
      if (value != null) {
        jsCommands.append("document.documentElement.style.setProperty('${key}', '${value}');\n")
        jsCommands.append(
            "document.body.style.setProperty('font-size', 'var(--codeium-font-size)');\n")
      }
    }
    val js = jsCommands.toString()
    execute(js)
  }
}
