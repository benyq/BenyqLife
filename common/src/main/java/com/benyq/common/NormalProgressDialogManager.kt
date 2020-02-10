package com.benyq.common

import android.app.Activity
import android.content.Context
import com.benyq.common.ui.BenyqProgressDialog

/**
 * @author benyq
 * @emil 1520063035@qq.com
 * create at 2020/2/4
 * description:
 */
object NormalProgressDialogManager {

    private var mDialog: BenyqProgressDialog? = null

    @Synchronized
    fun showLoading(context: Context, cancelable: Boolean) {
        mDialog?.let {
            if (it.isShowing) {
                it.dismiss()
            }
        }
        if (context !is Activity) {
            return
        }
        mDialog = BenyqProgressDialog(context).apply {
            setCancelable(cancelable)
            if (!isShowing && !context.isFinishing) {
                show()
            }
        }

    }

    fun dismissLoading() {
        mDialog?.dismiss()
        mDialog = null
    }

}