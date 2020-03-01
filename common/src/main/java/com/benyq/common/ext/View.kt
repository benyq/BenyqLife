package com.benyq.common.ext

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

/**
 *
 * @author benyq
 * @time 2020/3/1
 * @e-mail 1520063035@qq.com
 * @note 有关View的拓展方法
 */

// 关闭软键盘
fun View.hideKeyBoard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

// 显示软键盘
fun View.showKeyboard() {
    val imm: InputMethodManager =
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    this.requestFocus()
    imm.showSoftInput(this, 0)
}

// 设置双击事件
fun View.setDoubleClickListener(block: () -> Unit) {
    this.setOnClickListener { ClickUtil.interval(block) }
}


fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

/**
 *
 * @author benyq
 * @time 2020/3/1
 * @e-mail 1520063035@qq.com
 * @note
 */
object ClickUtil {

    private var clickTime: Long = 0

    /**
     *  双击事件
     *  @param duration 两次间隔时间
     */
    fun interval(block: () -> Unit, duration: Int = 1000) {
        val nowTime = System.currentTimeMillis()
        if (nowTime - clickTime > duration) {
            clickTime = nowTime
        } else {
            block()
        }
    }
}