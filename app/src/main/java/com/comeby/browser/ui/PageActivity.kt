package com.comeby.browser.ui

import android.content.Intent
import android.graphics.Bitmap
import android.text.SpannableStringBuilder
import android.view.KeyEvent
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.core.view.drawToBitmap
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.comeby.browser.App
import com.comeby.browser.R
import com.comeby.browser.databinding.PageActivityBinding
import com.comeby.browser.extensions.hideSoftInput
import com.comeby.browser.extensions.setOnClick
import com.comeby.browser.extensions.toast
import com.comeby.browser.firebase.FirebaseEventUtil
import com.comeby.browser.model.WebPage
import com.comeby.browser.utils.Utils
import com.comeby.browser.manage.SweetWebManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PageActivity : BaseActivity<PageActivityBinding>() {

    private val adapter by lazy { PageTabAdapter() }

    override fun buildLayoutBinding(): PageActivityBinding {
        return PageActivityBinding.inflate(layoutInflater)
    }

    override fun initView() {
        binding.run {
            root.viewTreeObserver.addOnGlobalLayoutListener {
                updateWebPageDraw()
                binding.root.viewTreeObserver.removeOnGlobalLayoutListener { }
            }

            searchView.addTextChangedListener {
                val inputText = binding.searchView.text.toString().trim()
                if (inputText.isEmpty()) {
                    btnSearch.isVisible = true
                    btnDelete.isVisible = false
                } else {
                    btnSearch.isVisible = false
                    btnDelete.isVisible = true
                }
            }

            searchView.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    binding.searchView.setText("")
                    stopLoading()
                    SweetWebManager.currentWebStopLoad()
                    setWebViewVisible(false)
                }
            }

            searchView.setOnEditorActionListener(object : TextView.OnEditorActionListener {
                override fun onEditorAction(textView: TextView, actionId: Int, p2: KeyEvent?): Boolean {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        textView.hideSoftInput()
                        val inputText = binding.searchView.text.toString().trim()
                        if (inputText.isNullOrEmpty()) {
                            toast(R.string.search_input_tips)
                        } else {
                            FirebaseEventUtil.searchEvent(inputText)
                            startBrowser(inputText)
                        }
                        return true
                    }
                    return false
                }
            })

            setOnClick(btnDelete, btnBack, btnGo, btnClean, btnCount, btnSetting) {
                binding.searchView.hideSoftInput()
                when (this) {
                    btnDelete -> {
                        FirebaseEventUtil.event("sweet_clean")
                        binding.searchView.setText("")
                    }

                    btnBack -> {
                        callBack()
                    }

                    btnGo -> {
                        callGo()
                    }

                    btnClean -> {
                        showClearDialog {
                            startActivity(Intent(this@PageActivity, ClearActivity::class.java))
                        }
                    }

                    btnCount -> {
                        startActivity(Intent(this@PageActivity, PageListActivity::class.java))
                    }

                    btnSetting -> {
                        startActivity(Intent(this@PageActivity, SettingActivity::class.java))
                    }
                }
            }
        }

        adapter.dataList.clear()
        adapter.dataList.addAll(App.TAB_LIST)
        adapter.itemClickCallback = { item, _ ->
            binding.searchView.hideSoftInput()
            startBrowser(item.ip)
            FirebaseEventUtil.newLinkEvent(item.name)
        }
        binding.navRecyclerView.layoutManager = GridLayoutManager(this, 4)
        binding.navRecyclerView.adapter = adapter

        SweetWebManager.myWebListener = object : SweetWebManager.MyWebListener {
            override fun onProgressChanged(progress: Int, index: Int) {
                if (SweetWebManager.currentWebLinks.index == index) {
                    binding.progressBar.isVisible = progress < 100
                    binding.progressBar.progress = progress
                    if (progress >= 100) {
                        setWebViewVisible(true)
                        if (binding.webContainer.childCount >= 1) {
                            val view = binding.webContainer.getChildAt(0)
                            if (view != SweetWebManager.currentWebLinks.webView) {
                                binding.webContainer.removeAllViews()
                                (SweetWebManager.currentWebLinks.webView.parent as? ViewGroup)?.removeAllViews()
                                binding.webContainer.addView(SweetWebManager.currentWebLinks.webView)
                            }
                        } else {
                            binding.webContainer.removeAllViews()
                            (SweetWebManager.currentWebLinks.webView.parent as? ViewGroup)?.removeAllViews()
                            binding.webContainer.addView(SweetWebManager.currentWebLinks.webView)
                        }

                        lifecycleScope.launch {
                            delay(1000)
                            val webView = SweetWebManager.currentWebLinks.webView
                            if (webView.isLaidOut) {
                                val bitmap = webView.drawToBitmap(Bitmap.Config.ARGB_8888)
                                SweetWebManager.updateCurrentWebBitMap(bitmap)
                            }
                        }

                        updateBottomTools()
                        binding.searchView.setText("")
                    }
                }
            }

            override fun addWeb(webLinks: WebPage) {
                stopLoading()
                updateBottomTools()
                binding.webContainer.removeAllViews()
                binding.webContainer.isVisible = false
                binding.tvCount.text = "${SweetWebManager.webPageLists.size}"
            }

            override fun removeWeb(webLinks: WebPage) {
                lifecycleScope.launchWhenResumed {
                    binding.tvCount.text = "${SweetWebManager.webPageLists.size}"
                    updateBottomTools()
                    if (SweetWebManager.currentWebLinks.webView.isLoadingFinish) {
                        binding.webContainer.removeAllViews()
                        binding.webContainer.addView(SweetWebManager.currentWebLinks.webView)
                        setWebViewVisible(true)
                        return@launchWhenResumed
                    }
                    if (SweetWebManager.currentWebLinks.webView.isLoading) {
                        binding.progressBar.isVisible = true
                        binding.searchView.text =
                            SpannableStringBuilder(SweetWebManager.currentWebLinks.inputText)
                        setWebViewVisible(false)
                        return@launchWhenResumed
                    }
                    if (SweetWebManager.currentWebLinks.webView.isStopped || SweetWebManager.currentWebLinks.webView.isIdea) {
                        setWebViewVisible(false)
                        binding.progressBar.isVisible = false
                        binding.searchView.text = SpannableStringBuilder("")
                    }
                }
            }

            override fun onWebChanged(webLinks: WebPage) {
                lifecycleScope.launchWhenResumed {
                    if (webLinks.webView.isLoadingFinish) {
                        setWebViewVisible(true)
                        binding.progressBar.isVisible = false
                        binding.webContainer.removeAllViews()
                        binding.webContainer.addView(webLinks.webView)
                    } else {
                        binding.searchView.text = SpannableStringBuilder(webLinks.inputText)
                        binding.progressBar.isVisible = webLinks.webView.isLoading
                        setWebViewVisible(false)
                    }
                    updateBottomTools()
                }
            }

            override fun clean() {
                lifecycleScope.launchWhenResumed {
                    SweetWebManager.currentWebStopLoad()
                    SweetWebManager.currentWebLinks.webView.clearHistories()
                    setWebViewVisible(false)
                    updateBottomTools()
                    binding.progressBar.isVisible = false
                    binding.progressBar.progress = 0
                    binding.searchView.text = SpannableStringBuilder("")
                    SweetWebManager.currentWebLinks.inputText = ""
                    binding.tvCount.text = "${SweetWebManager.webPageLists.size}"
                }
            }

        }
    }

    override fun onStart() {
        super.onStart()
        if (App.INSTANCE.isForeground) {
            FirebaseEventUtil.event("sweet_show")
        }
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onResume() {
        super.onResume()
    }

    private fun startBrowser(content: String) {
        FirebaseEventUtil.event("sweet_newSearch")
        binding.searchView.hideSoftInput()
        binding.searchView.clearFocus()
        SweetWebManager.currentWebLinks.inputText = content
        SweetWebManager.currentWebLinks.webView.startLoad(content)
    }

    private fun setWebViewVisible(isVisible: Boolean) {
        binding.webContainer.isVisible = isVisible
        if (isVisible) {

        } else {
            binding.webContainer.removeAllViews()
        }
    }

    private fun stopLoading() {
        binding.progressBar.isVisible = false
        binding.searchView.setText("")
    }

    private fun updateBottomTools() {
        if (binding.webContainer.isVisible) {
            binding.btnBack.isEnabled = true
            binding.btnBack.setImageResource(R.mipmap.opt_back)
            if (SweetWebManager.currentWebLinks.webView.canGoForward()) {
                binding.btnGo.isEnabled = true
                binding.btnGo.setImageResource(R.mipmap.opt_go)
            } else {
                binding.btnGo.isEnabled = false
                binding.btnGo.setImageResource(R.mipmap.opt_go)
            }
        } else {
            SweetWebManager.currentWebLinks.webView.clearHistories()
            binding.btnBack.isEnabled = false
            binding.btnGo.isEnabled = false
            binding.webContainer.removeAllViews()
            binding.btnBack.setImageResource(R.mipmap.opt_back)
            binding.btnGo.setImageResource(R.mipmap.opt_go)
        }
    }

    private fun callBack() {
        if (SweetWebManager.currentWebLinks.webView.canGoBack()) {
            SweetWebManager.currentWebLinks.webView.goBack()
            return
        }
        SweetWebManager.currentWebStopLoad()
        setWebViewVisible(false)
        stopLoading()
        updateBottomTools()
    }

    private fun callGo() {
        if (SweetWebManager.currentWebLinks.webView.canGoForward()) {
            SweetWebManager.currentWebLinks.webView.goForward()
        }
        updateBottomTools()
    }

    /**
     * 更新网页快照
     */
    private fun updateWebPageDraw() {
        SweetWebManager.updateCurrentWebBitMap(binding.root.drawToBitmap())
    }

    override fun onBackPressed() {
        if (binding.webContainer.isVisible) {
            binding.btnBack.callOnClick()
            return
        }
        moveTaskToBack(true)
    }


}
