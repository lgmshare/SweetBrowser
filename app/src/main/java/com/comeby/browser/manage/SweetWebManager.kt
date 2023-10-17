package com.comeby.browser.manage

import android.graphics.Bitmap
import com.comeby.browser.model.WebPage
import com.comeby.browser.views.SweetWebView

object SweetWebManager {

    private val indexWebLinks = WebPage("", null, 1, SweetWebView())

    val webPageLists = mutableListOf<WebPage>().apply {
        sortBy { it.index }
    }

    var currentWebLinks = indexWebLinks

    fun addWeb(webLinks: WebPage) {
        currentWebLinks = webLinks
        webPageLists.add(webLinks)
        myWebListener?.addWeb(webLinks)
    }

    fun getMaxWeb(): Int {
        if (webPageLists.isEmpty()) {
            return 0
        }

        return webPageLists.sortedBy {
            it.index
        }.last().index
    }

    fun getWebLinks(index: Int): WebPage {
        return webPageLists.find {
            it.index == index
        } ?: indexWebLinks
    }

    fun getMinWebLinks(): WebPage {
        return webPageLists.sortedBy {
            -it.index
        }.getOrNull(0) ?: indexWebLinks
    }

    fun removeWebLinks(webLinks: WebPage) {
        webPageLists.remove(webLinks)
    }

    fun getWebIndex(webView: SweetWebView): Int {
        return webPageLists.find {
            it.webView == webView
        }?.index ?: 0
    }

    fun cleanAllWeb() {
        webPageLists.clear()
        webPageLists.add(indexWebLinks)
        currentWebLinks = indexWebLinks
        myWebListener?.clean()
    }

    fun currentWebStopLoad(){
        currentWebLinks.webView.stopLoad()
    }

    fun updateCurrentWebBitMap(bitMap: Bitmap?) {
        if (bitMap != null) {
            currentWebLinks.bitmap = bitMap
        }
    }

    var myWebListener: MyWebListener? = null

    interface MyWebListener {
        fun onProgressChanged(progress: Int, index: Int)
        fun addWeb(webLinks: WebPage)
        fun removeWeb(webLinks: WebPage)
        fun onWebChanged(webLinks: WebPage)
        fun clean() {}
    }
}