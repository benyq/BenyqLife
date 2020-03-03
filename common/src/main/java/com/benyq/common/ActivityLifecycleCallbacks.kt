package com.benyq.common

import android.app.Activity
import android.app.Application
import android.os.Bundle

/**
 * @author benyq
 * @time 2020/3/1
 * @e-mail 1520063035@qq.com
 * @note
 */
object ActivityLifecycleCallbacks : Application.ActivityLifecycleCallbacks {
    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityDestroyed(activity: Activity) {
        ActivityManager.remove(activity)
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        ActivityManager.add(activity)
    }

    override fun onActivityResumed(activity: Activity) {
    }
}