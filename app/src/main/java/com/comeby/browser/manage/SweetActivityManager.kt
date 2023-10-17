package com.comeby.browser.manage

import android.app.Activity
import java.lang.ref.WeakReference
import java.util.Stack


object SweetActivityManager {

    private val mActivityStack: Stack<WeakReference<Activity>> = Stack()

    /**
     * 检查弱引用是否释放，若释放，则从栈中清理掉该元素
     */
    fun checkWeakReference() {
        if (mActivityStack != null) {
            // 使用迭代器进行安全删除
            val it = mActivityStack.iterator()
            while (it.hasNext()) {
                val activityReference = it.next()
                val temp = activityReference.get()
                if (temp == null) {
                    it.remove()
                }
            }
        }
    }

    /**
     * 获取当前Activity（栈中最后一个压入的[栈顶]）
     */
    fun getCurrentActivity(): Activity? {
        checkWeakReference()
        return if (mActivityStack != null && !mActivityStack.isEmpty()) {
            mActivityStack.lastElement().get()
        } else null
    }

    /**
     * 添加Activity到栈中
     */
    fun pushActivity(activity: Activity?) {
        if (activity != null) {
            mActivityStack.push(WeakReference(activity))
        }
    }

    /**
     * 移除指定的Activity
     */
    fun popActivity(activity: Activity?) {
        if (activity != null && mActivityStack != null) {
            // 使用迭代器进行安全删除
            val it = mActivityStack.iterator()
            while (it.hasNext()) {
                val activityReference = it.next()
                val temp = activityReference.get()
                // 清理掉已经释放的activity
                if (temp == null) {
                    it.remove()
                    continue
                }
                if (temp === activity) {
                    it.remove()
                }
            }
        }
    }

    /**
     * 结束当前Activity（栈中最后一个压入的[栈顶]）
     */
    fun finishCurrentActivity() {
        val activity = getCurrentActivity()
        activity?.let { finishActivity(it) }
    }

    /**
     * 结束指定Activity
     *
     * @param activity
     */
    fun finishActivity(activity: Activity?) {
        if (activity != null && mActivityStack != null) {
            // 使用迭代器进行安全删除
            val it = mActivityStack.iterator()
            while (it.hasNext()) {
                val activityReference = it.next()
                val temp = activityReference.get()
                // 清理掉已经释放的activity
                if (temp == null) {
                    it.remove()
                    continue
                }
                if (temp === activity) {
                    it.remove()
                }
            }
            activity.finish()
        }
    }

    /**
     * 结束指定类名的Activity
     *
     * @param clazz
     */
    fun finishActivity(clazz: Class<*>) {
        if (mActivityStack != null) {
            // 使用迭代器进行安全删除
            val it = mActivityStack.iterator()
            while (it.hasNext()) {
                val activityReference = it.next()
                val activity = activityReference.get()
                // 清理掉已经释放的activity
                if (activity == null) {
                    it.remove()
                    continue
                }
                if (activity.javaClass == clazz) {
                    it.remove()
                    activity.finish()
                }
            }
        }
    }

    /**
     * 结束此Activity在栈前的所有Activity,不包含此Activity
     *
     * @param clazz 标记activity
     */
    fun finishTopActivity(clazz: Class<*>?) {
        if (clazz == null) {
            return
        }
        while (true) {
            val activity = getCurrentActivity()
            if (activity == null || activity.javaClass == clazz) {
                break
            }
            finishActivity(activity)
        }
    }

    /**
     * 结束所有Activity
     */
    fun finishAllActivity() {
        while (!mActivityStack.isEmpty()) {
            finishActivity(getCurrentActivity())
        }
        mActivityStack.clear()
    }

    fun isHasActivityStack(clazz: Class<*>?): Boolean {
        if (clazz == null) {
            return false
        }
        val it = mActivityStack.iterator()
        while (it.hasNext()) {
            val activityReference = it.next()
            val activity = activityReference.get()
            // 清理掉已经释放的activity
            if (activity == null) {
                it.remove()
                continue
            }
            if (activity.javaClass == clazz) {
                return true
            }
        }
        return false
    }

}