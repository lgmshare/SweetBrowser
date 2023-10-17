package com.comeby.browser.ui

import androidx.recyclerview.widget.GridLayoutManager
import com.comeby.browser.databinding.PageListActivityBinding
import com.comeby.browser.extensions.setOnClick
import com.comeby.browser.firebase.FirebaseEventUtil
import com.comeby.browser.model.WebPage
import com.comeby.browser.manage.SweetWebManager

class PageListActivity : BaseActivity<PageListActivityBinding>() {

    private val adapter by lazy { PageAdapter() }

    override fun buildLayoutBinding(): PageListActivityBinding {
        return PageListActivityBinding.inflate(layoutInflater)
    }

    override fun initView() {
        FirebaseEventUtil.event("sweet_showTab")
        binding.run {
            setOnClick(btnBack, btnAdd) {
                when (this) {
                    btnAdd -> {
                        FirebaseEventUtil.newTabEvent("tab")
                        createNewWebTab()
                        finish()
                    }

                    btnBack -> {
                        finish()
                    }
                }
            }
        }

        adapter.itemClickCallback = { item, position ->
            SweetWebManager.currentWebLinks = item
            SweetWebManager.myWebListener?.onWebChanged(item)
            finish()
        }

        adapter.itemDeleteClickCallback = { item, position ->
            remoteWeb(item, position)
        }
        binding.tabsRecyclerView.layoutManager = GridLayoutManager(this, 2)
        binding.tabsRecyclerView.adapter = adapter

        updateData()
    }

    private fun remoteWeb(link: WebPage, position: Int) {
        SweetWebManager.removeWebLinks(link)
        if (SweetWebManager.currentWebLinks == link) {
            SweetWebManager.currentWebLinks = SweetWebManager.getMinWebLinks()
        }
        SweetWebManager.myWebListener?.removeWeb(link)

        updateData()
        adapter.notifyDataSetChanged()
    }

    private fun updateData() {
        val list = SweetWebManager.webPageLists.sortedByDescending { it.index }
        adapter.dataList.clear()
        adapter.dataList.addAll(list)
    }

    override fun onResume() {
        super.onResume()
    }
}