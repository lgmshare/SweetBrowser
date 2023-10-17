package com.comeby.browser.utils

import android.content.Context
import android.content.SharedPreferences
import com.comeby.browser.App

object SharePrefUtils {

    private var sharePref: SharedPreferences = App.INSTANCE.getSharedPreferences("browser", Context.MODE_PRIVATE)

    var isFirstLaunch: Boolean
        get() = sharePref.getBoolean("isFirstLaunch", true)
        set(value) = sharePref.edit().putBoolean("isFirstLaunch", value).apply()

    var country: String?
        get() = sharePref.getString("country", "")
        set(value) = sharePref.edit().putString("country", value).apply()

    var adDataDate: String?
        get() = sharePref.getString("adDataDate", "")
        set(value) = sharePref.edit().putString("adDataDate", value).apply()

    var adShowCount: Int
        get() = sharePref.getInt("adShowCount", 0)
        set(value) = sharePref.edit().putInt("adShowCount", value).apply()

    var adClickCount: Int
        get() = sharePref.getInt("adClickCount", 0)
        set(value) = sharePref.edit().putInt("adClickCount", value).apply()
}