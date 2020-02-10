package com.benyq.common

import android.app.Application
import com.tencent.mmkv.MMKV

/**
 * @author benyq
 * @emil 1520063035@qq.com
 * create at 2020/1/11
 * description:
 */
open class BaseApplication : Application(){

    companion object {
         lateinit var sInstance : Application
    }

    override fun onCreate() {
        super.onCreate()
        sInstance = this
        MMKV.initialize(this)
    }
}