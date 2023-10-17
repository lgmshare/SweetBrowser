package com.comeby.browser.model

import android.graphics.Bitmap
import com.comeby.browser.views.SweetWebView

data class WebPage(var path: String, var bitmap: Bitmap?, val index: Int, val webView: SweetWebView) {
    var inputText = ""
}