package com.benyq.benyqlife

import android.app.Application
import com.benyq.common.BaseApplication

/**
 * @author benyq
 * @emil 1520063035@qq.com
 * create at 2020/1/11
 * description:
 */
class App : BaseApplication() {

    companion object {
        //java中，子类不继承父类的static变量，但能访问
        fun getInstance(): Application {
            return sInstance
        }
    }

    override fun onCreate() {
        super.onCreate()
    }
}