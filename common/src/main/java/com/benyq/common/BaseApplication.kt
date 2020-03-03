package com.benyq.common

import android.app.Activity
import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.tencent.mmkv.MMKV
import kotlin.reflect.KProperty

/**
 * @author benyq
 * @emil 1520063035@qq.com
 * create at 2020/1/11
 * description:
 */
open class BaseApplication : Application(), ViewModelStoreOwner{

    companion object {
         lateinit var sInstance : Application
    }

    //TODO tip：可借助 Application 来管理一个应用级 的 SharedViewModel，
    // 实现全应用范围内的 生命周期安全 且 事件源可追溯的 视图控制器 事件通知。
    private lateinit var mAppViewModelStore: ViewModelStore
    private lateinit var mFactory: ViewModelProvider.Factory


    override fun onCreate() {
        super.onCreate()
        sInstance = this
        mAppViewModelStore = ViewModelStore()
        mFactory = ViewModelProvider.AndroidViewModelFactory.getInstance(this)
        MMKV.initialize(this)
        registerActivityLifecycleCallbacks(ActivityLifecycleCallbacks)
    }

        override fun getViewModelStore() = mAppViewModelStore

    open fun getAppViewModelProvider(): ViewModelProvider? {
        return ViewModelProvider(this, getAppFactory())
    }

    private fun getAppFactory(): ViewModelProvider.Factory {
        return mFactory
    }
}