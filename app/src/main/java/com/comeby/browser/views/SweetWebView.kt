package com.comeby.browser.views

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.AttributeSet
import android.util.Patterns
import android.view.View
import android.webkit.*
import androidx.annotation.RequiresApi
import androidx.core.view.drawToBitmap
import com.comeby.browser.App
import com.comeby.browser.firebase.FirebaseEventUtil
import com.comeby.browser.manage.SweetWebManager

class SweetWebView @JvmOverloads constructor(context: Context = App.INSTANCE, attributeSet: AttributeSet? = null) : WebView(context, attributeSet) {

    enum class WebState {
        IDEA, LOADING, FINISH, STOPPED
    }

    init {
        settings.apply {
            setSupportZoom(false)
            javaScriptEnabled = true
            javaScriptCanOpenWindowsAutomatically = true
            domStorageEnabled = true
            useWideViewPort = false
            loadWithOverviewMode = true
            defaultTextEncodingName = "utf-8"
            loadsImagesAutomatically = true
            allowContentAccess = true
            allowFileAccess = true
            builtInZoomControls = true
            layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            setLayerType(View.LAYER_TYPE_HARDWARE, null)
        }
        webChromeClient = MyWebChromeClient()
        webViewClient = MyWebClient()
    }

    private var isClearHistory = false

    private var loadState: WebState = WebState.IDEA

    val isIdea: Boolean
        get() {
            return loadState == WebState.IDEA
        }

    val isStopped: Boolean
        get() {
            return loadState == WebState.STOPPED
        }

    val isLoadingFinish: Boolean
        get() {
            return loadState == WebState.FINISH
        }

    val isLoading: Boolean
        get() {
            return loadState == WebState.LOADING
        }

    var startTimes = 0L

    fun startLoad(path: String) {
        loadState = WebState.LOADING
        if (Patterns.WEB_URL.matcher(path).matches()) {
            loadUrl(path)
        } else {
            SweetWebManager.currentWebLinks.webView.startLoad("https://www.google.com/search?q=${path}")
        }
    }

    fun stopLoad() {
        loadState = WebState.STOPPED
        stopLoading()
    }

    fun clearHistories() {
        isClearHistory = true
    }

    inner class MyWebChromeClient : WebChromeClient() {
        override fun onProgressChanged(view: WebView, newProgress: Int) {
            if (newProgress >= 100) {
                if (isClearHistory) {
                    isClearHistory = false
                    view.clearHistory()
                }
            }
            if (loadState != WebState.STOPPED) {
                loadState = if (newProgress >= 100) {
                    if (startTimes != 0L) {
                        val time = (System.currentTimeMillis() - startTimes) / 1000
                        FirebaseEventUtil.event("sweet_reload", Bundle().apply {
                            putLong("bro", if (time < 1) 1 else time)
                        })
                        startTimes = 0L
                    }

                    WebState.FINISH
                } else {
                    WebState.LOADING
                }
                SweetWebManager.myWebListener?.onProgressChanged(newProgress, SweetWebManager.getWebIndex(this@SweetWebView))
            }
        }
    }

    inner class MyWebClient : WebViewClient() {
        override fun onLoadResource(view: WebView?, url: String?) {
            super.onLoadResource(view, url)
        }

        override fun onPageStarted(view: WebView?, url: String, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            loadState = WebState.LOADING
            startTimes = System.currentTimeMillis()
        }

        override fun onPageFinished(view: WebView?, url: String) {
            super.onPageFinished(view, url)
            if (loadState != WebState.STOPPED) {
                loadState = WebState.FINISH
            }

            val webView = SweetWebManager.currentWebLinks.webView
            if (webView.isLaidOut) {
                val bitmap = webView.drawToBitmap(Bitmap.Config.ARGB_8888)
                SweetWebManager.updateCurrentWebBitMap(bitmap)
            }
        }

        @RequiresApi(Build.VERSION_CODES.O_MR1)
        override fun onSafeBrowsingHit(view: WebView?, request: WebResourceRequest?, threatType: Int, callback: SafeBrowsingResponse?) {
            callback?.backToSafety(true)
        }

        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            return super.shouldOverrideUrlLoading(view, request)
        }

        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            return super.shouldOverrideUrlLoading(view, url)
        }
    }
}