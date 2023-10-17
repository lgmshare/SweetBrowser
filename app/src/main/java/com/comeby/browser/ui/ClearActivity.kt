package com.comeby.browser.ui

import android.os.Bundle
import android.webkit.CookieManager
import androidx.lifecycle.lifecycleScope
import com.comeby.browser.R
import com.comeby.browser.databinding.CleanActivityBinding
import com.comeby.browser.extensions.toast
import com.comeby.browser.firebase.FirebaseEventUtil
import com.comeby.browser.manage.SweetWebManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

class ClearActivity : BaseActivity<CleanActivityBinding>() {

    private var job: Job? = null

    private var startTime = 0L

    override fun buildLayoutBinding(): CleanActivityBinding {
        return CleanActivityBinding.inflate(layoutInflater)
    }

    override fun initView() {
        startTime = System.currentTimeMillis()
    }

    override fun onStart() {
        super.onStart()

        job = lifecycleScope.launch {
            kotlin.runCatching {
                withTimeoutOrNull(14000) {
                    launch {
                        CookieManager.getInstance().removeAllCookies {
                        }
                        SweetWebManager.cleanAllWeb()
                    }

                    launch {
                        delay(2800)
                        val time = (System.currentTimeMillis() - startTime) / 1000
                        FirebaseEventUtil.event("sweet_clean_toast", Bundle().apply {
                            putLong("bro", if (time < 1) 1 else time)
                        })
                    }
                }
            }.onSuccess {
                toast(R.string.clear_successfully)
                FirebaseEventUtil.event("sweet_clean_end")
                finish()
            }.onFailure {
                finish()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        job?.cancel()
    }

    override fun onBackPressed() {
    }
}