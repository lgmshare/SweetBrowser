package com.comeby.browser.firebase

import android.os.Bundle
import android.util.Log
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.comeby.browser.utils.Utils

class FirebaseEventUtil {

    companion object {

        fun setProperty(country: String?) {
            if (!country.isNullOrEmpty()) {
                Firebase.analytics.setUserProperty("ay_pe", country)
            }
        }

        fun event(tag: String, params: Bundle? = null) {
            if (params == null) {
                Utils.log("firebase埋点:$tag")
            } else {
                Utils.log("firebase埋点:$tag $params")
            }
            Firebase.analytics.logEvent(tag, params)
        }

        fun newLinkEvent(site: String) {
            event("sweet_guid", Bundle().apply {
                putString("bro", site)
            })
        }

        fun searchEvent(text: String) {
            event("sweet_search", Bundle().apply {
                putString("bro", text)
            })
        }

        fun newTabEvent(pos: String) {
            event("sweet_newTab", Bundle().apply {
                putString("bro", pos)
            })
        }
    }
}