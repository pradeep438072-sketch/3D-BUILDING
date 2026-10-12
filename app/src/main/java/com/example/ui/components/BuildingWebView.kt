package com.example.ui.components

import android.annotation.SuppressLint
import android.content.Context
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

class WebAppInterface(
    private val onReady: () -> Unit,
    private val onRoomSelected: (name: String, type: String, floor: Int) -> Unit,
    private val onFloorSelected: (floor: Int) -> Unit,
    private val onExportReady: (objData: String) -> Unit
) {
    @JavascriptInterface
    fun onViewerReady() {
        onReady()
    }

    @JavascriptInterface
    fun onRoomSelected(roomName: String, roomType: String, floorIndex: Int) {
        onRoomSelected(roomName, roomType, floorIndex)
    }

    @JavascriptInterface
    fun onFloorSelected(floorIndex: Int) {
        onFloorSelected(floorIndex)
    }

    @JavascriptInterface
    fun onExportReady(objData: String) {
        onExportReady(objData)
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun BuildingWebView(
    buildingJson: String,
    modifier: Modifier = Modifier,
    onWebViewCreated: (WebView) -> Unit = {},
    onRoomSelected: (name: String, type: String, floor: Int) -> Unit = { _, _, _ -> },
    onFloorSelected: (floor: Int) -> Unit = {},
    onExportReady: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val webView = remember {
        WebView(context).apply {
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                loadWithOverviewMode = true
                useWideViewPort = true
                allowFileAccess = true
                allowContentAccess = true
                cacheMode = WebSettings.LOAD_NO_CACHE
            }
            webChromeClient = WebChromeClient()
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    view?.evaluateJavascript("window.loadBuildingPlan($buildingJson);", null)
                }
            }
            addJavascriptInterface(
                WebAppInterface(
                    onReady = {
                        post {
                            evaluateJavascript("window.loadBuildingPlan($buildingJson);", null)
                        }
                    },
                    onRoomSelected = onRoomSelected,
                    onFloorSelected = onFloorSelected,
                    onExportReady = onExportReady
                ),
                "AndroidBridge"
            )
            loadUrl("file:///android_asset/web/building_viewer.html")
        }
    }

    DisposableEffect(buildingJson) {
        webView.evaluateJavascript("window.loadBuildingPlan($buildingJson);", null)
        onDispose { }
    }

    AndroidView(
        factory = {
            onWebViewCreated(webView)
            webView
        },
        modifier = modifier.fillMaxSize()
    )
}
