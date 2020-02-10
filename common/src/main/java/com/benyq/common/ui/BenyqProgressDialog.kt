package com.benyq.common.ui

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.benyq.common.R

/**
 * @author benyq
 * @emil 1520063035@qq.com
 * create at 2020/2/4
 * description:
 */
class BenyqProgressDialog(context: Context) : AlertDialog(context, R.style.dialog_style){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.progress_dialog)
    }
}