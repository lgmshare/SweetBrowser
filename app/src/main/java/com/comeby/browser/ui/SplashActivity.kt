package com.comeby.browser.ui

import android.animation.ValueAnimator
import android.content.Intent
import androidx.lifecycle.lifecycleScope
import com.comeby.browser.App
import com.comeby.browser.databinding.SplashActivityBinding
import com.comeby.browser.extensions.progressAnimation
import com.comeby.browser.firebase.FirebaseEventUtil
import com.comeby.browser.manage.SweetActivityManager
import com.comeby.browser.utils.SharePrefUtils
import com.comeby.browser.utils.Utils
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import java.util.Locale

class SplashActivity : BaseActivity<SplashActivityBinding>() {

    private var job: Job? = null

    private var progressAnimator: ValueAnimator? = null

    override fun buildLayoutBinding(): SplashActivityBinding {
        return SplashActivityBinding.inflate(layoutInflater)
    }

    override fun initView() {
        if (SharePrefUtils.isFirstLaunch) {
            FirebaseEventUtil.event("sweet_first")
            SharePrefUtils.isFirstLaunch = false
            SharePrefUtils.country = Locale.getDefault().country
        }
    }

    override fun onStart() {
        super.onStart()
        if (!SweetActivityManager.isHasActivityStack(PageActivity::class.java)) {
            FirebaseEventUtil.event("sweet_cold")
        } else {
            FirebaseEventUtil.event("sweet_hot")
        }
        Utils.log("firebase属性:country=${SharePrefUtils.country}")
        FirebaseEventUtil.setProperty(SharePrefUtils.country)

        binding.progressbar.progress = 0
        job = lifecycleScope.launch {
            kotlin.runCatching {
                withTimeoutOrNull(14000) {
                    startProgressAnimation(3800)
                    launch {
                        delay(2800)
                    }
                }
            }.onSuccess {
                startProgressAnimation(1000)
                if (!SweetActivityManager.isHasActivityStack(PageActivity::class.java)) {
                    createNewWebTab()
                    startActivity(Intent(this@SplashActivity, PageActivity::class.java))
                }
                finish()
            }.onFailure {
            }
        }
    }

    override fun onStop() {
        super.onStop()
        stopProgressAnimation()
        job?.cancel()
    }

    override fun onBackPressed() {
    }

    private fun startProgressAnimation(duration: Long) {
        progressAnimator?.cancel()
        progressAnimator = binding.progressbar.progressAnimation(duration, binding.progressbar.progress)
    }

    private fun stopProgressAnimation() {
        progressAnimator?.cancel()
        progressAnimator = null
    }
}
