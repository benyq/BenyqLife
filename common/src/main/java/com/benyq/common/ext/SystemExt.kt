package com.benyq.common.ext

import android.app.Activity
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.TypedValue

/**
 * @author benyq
 * @emil 1520063035@qq.com
 * create at 2020/1/11
 * description: 系统拓展方法
 */


fun Context.versionCode(): String {
    val packageManager = packageManager
    val packageInfo: PackageInfo
    var versionCode = ""
    try {
        packageInfo = packageManager.getPackageInfo(packageName, 0)
        versionCode = packageInfo.versionCode.toString()
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }

    return versionCode
}

private val handler = Handler(Looper.getMainLooper())
fun runUnUiThread(block: () -> Unit) {
    if (Looper.myLooper() == Looper.getMainLooper()) {
        block()
    } else {
        handler.post {
            block()
        }
    }
}


fun getScreenWidth(context: Context):Int {
    val dm = DisplayMetrics()
    (context as Activity).windowManager.defaultDisplay.getMetrics(dm)
    return dm.widthPixels
}

fun getScreenHeight(context: Context): Int {
    val dm = DisplayMetrics()
    (context as Activity).windowManager.defaultDisplay.getMetrics(dm)
    return dm.heightPixels
}



val Float.dp2px: Float
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        Resources.getSystem().displayMetrics
    )

val Int.dp2px: Int
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    ).toInt()


val Float.sp2px: Float
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        this,
        Resources.getSystem().displayMetrics
    )

val Int.sp2px: Int
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    ).toInt()


fun Int.px2dp(): Int = (this / (Resources.getSystem().displayMetrics.scaledDensity) + 0.5f).toInt()

fun Int.px2sp(): Int = (this / (Resources.getSystem().displayMetrics.scaledDensity) + 0.5f).toInt()