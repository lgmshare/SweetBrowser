package com.comeby.browser.extensions

import android.view.View
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach

/**
 * 批量添加点击事件
 */
inline fun setOnClick(vararg v: View?, crossinline block: View.() -> Unit) {
    v.forEach {
        it?.run {
            setOnDebouncedClickListener {
                this.block()
            }
        }
    }
}


