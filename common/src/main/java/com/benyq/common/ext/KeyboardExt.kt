package com.benyq.common.ext

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService

/**
 * @author benyq
 * @emil 1520063035@qq.com
 * create at 2020/1/12
 * description:
 */

fun hideKeyBoard(context: Context, v: View?) {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(v?.windowToken, 0)
}