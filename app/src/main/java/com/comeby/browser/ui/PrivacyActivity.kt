package com.comeby.browser.ui

import com.comeby.browser.databinding.PolicyActivityBinding

class PrivacyActivity : BaseActivity<PolicyActivityBinding>() {

    override fun buildLayoutBinding(): PolicyActivityBinding {
        return PolicyActivityBinding.inflate(layoutInflater)
    }

    override fun initView() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }
}