package com.comeby.browser

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.comeby.browser.extensions.getCurrentProcessName
import com.comeby.browser.manage.SweetActivityManager
import com.comeby.browser.ui.SplashActivity
import com.comeby.browser.model.WebTab
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings

class App : Application(), LifecycleEventObserver {

    var startCount: Int = 0
    var isForeground: Boolean = false

    companion object {
        lateinit var INSTANCE: App

        val TAB_LIST = mutableListOf<WebTab>().apply {
            add(WebTab(0, "facebook", "Facebook", "https://www.facebook.com", R.mipmap.web_facebook))
            add(WebTab(1, "google", "Google", "https://www.google.com", R.mipmap.web_google))
            add(WebTab(2, "youtube", "Youtube", "https://www.youtube.com", R.mipmap.web_youtube))
            add(WebTab(3, "twitter", "Twitter", "https://www.twitter.com", R.mipmap.web_twitter))
            add(WebTab(4, "instagram", "Instagram", "https://www.instagram.com", R.mipmap.web_ins))
            add(WebTab(5, "amazon", "Amazon", "https://www.amazon.com", R.mipmap.web_amazon))
            add(WebTab(6, "tiktok", "Tiktok", "https://www.tiktok.com", R.mipmap.web_tiktok))
            add(WebTab(7, "yahoo", "Yahoo", "https://www.yahoo.com", R.mipmap.web_yahoo))
        }
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this

        if (getCurrentProcessName() == BuildConfig.APPLICATION_ID) {
            ProcessLifecycleOwner.get().lifecycle.addObserver(this)
            registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
                override fun onActivityCreated(p0: Activity, p1: Bundle?) {
                    SweetActivityManager.pushActivity(p0)
                }

                override fun onActivityStarted(activity: Activity) {
                    startCount++
                    if (startCount == 1) {
                        if (activity !is SplashActivity) {
                            activity.startActivity(Intent(activity, SplashActivity::class.java))
                        }
                    }
                }

                override fun onActivityResumed(p0: Activity) {
                }

                override fun onActivityPaused(p0: Activity) {
                }

                override fun onActivityStopped(p0: Activity) {
                    startCount--
                }

                override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
                }

                override fun onActivityDestroyed(p0: Activity) {
                    SweetActivityManager.popActivity(p0)
                }
            })

            //初始化
            Firebase.initialize(this)
            val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
            remoteConfig.setConfigSettingsAsync(remoteConfigSettings {
                minimumFetchIntervalInSeconds = 3600
            })
            remoteConfig.fetchAndActivate()
        }
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_START -> {
                isForeground = true
            }

            Lifecycle.Event.ON_STOP -> {
                isForeground = false
            }

            else -> {
            }
        }
    }

}