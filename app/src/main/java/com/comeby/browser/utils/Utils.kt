package com.comeby.browser.utils

import android.util.Log
import com.comeby.browser.BuildConfig

class Utils {

    companion object {

        fun log(msg: String?) {
            if (BuildConfig.DEBUG) {
                if (!msg.isNullOrEmpty()) {
                    Log.d("LogHelper", msg)
                }
            }
        }

        fun logE(msg: String?) {
            if (BuildConfig.DEBUG) {
                if (!msg.isNullOrEmpty()) {
                    Log.e("LogHelper", msg)
                }
            }
        }
    }
}