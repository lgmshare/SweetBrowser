package com.comeby.browser.ui

import android.content.Intent
import com.comeby.browser.BuildConfig
import com.comeby.browser.R
import com.comeby.browser.databinding.SettingActivityBinding
import com.comeby.browser.extensions.copyToClipboard
import com.comeby.browser.extensions.jumpGooglePlayStore
import com.comeby.browser.extensions.jumpShare
import com.comeby.browser.extensions.toast
import com.comeby.browser.firebase.FirebaseEventUtil
import com.comeby.browser.manage.SweetWebManager

class SettingActivity : BaseActivity<SettingActivityBinding>() {

    override fun buildLayoutBinding(): SettingActivityBinding {
        return SettingActivityBinding.inflate(layoutInflater)
    }

    override fun initView() {
        binding.run {
            toolbar.setNavigationOnClickListener {
                finish()
            }

            btnNew.setOnClickListener {
                FirebaseEventUtil.newTabEvent("setting")
                createNewWebTab()
                finish()
            }
            btnShare.setOnClickListener {
                FirebaseEventUtil.event("sweet_share")
                val path = if (SweetWebManager.currentWebLinks.webView.isIdea || SweetWebManager.currentWebLinks.webView.isStopped) {
                    "https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"
                } else {
                    SweetWebManager.currentWebLinks.webView.url ?: ""
                }
                jumpShare(path)
            }
            btnCopy.setOnClickListener {
                FirebaseEventUtil.event("sweet_copy")
                val path = if (SweetWebManager.currentWebLinks.webView.isIdea || SweetWebManager.currentWebLinks.webView.isStopped) {
                    ""
                } else {
                    SweetWebManager.currentWebLinks.webView.url ?: ""
                }
                copyToClipboard(path)
                toast(R.string.page_copy_tips)
            }
            btnRate.setOnClickListener {
                jumpGooglePlayStore()
            }
            btnPrivacy.setOnClickListener {
                startActivity(Intent(this@SettingActivity, PrivacyActivity::class.java))
            }
        }
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }
}