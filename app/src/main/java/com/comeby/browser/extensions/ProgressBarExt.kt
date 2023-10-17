package com.comeby.browser.extensions

import android.animation.ValueAnimator
import android.view.animation.LinearInterpolator
import android.widget.ProgressBar

/**
 * @Author leo
 * @Date 2023/3/19
 */
fun ProgressBar.progressAnimation(duration: Long, starValue: Int = 0, endValue: Int = 100): ValueAnimator {
    val animator = ValueAnimator.ofInt(starValue, endValue)
    animator.duration = duration//时长
    animator.interpolator = LinearInterpolator()//线性插值器
    animator.addUpdateListener {
        progress = it.animatedValue as Int
    }//监听
    animator.start()
    return animator
}
