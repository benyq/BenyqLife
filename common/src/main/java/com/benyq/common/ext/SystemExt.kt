package com.benyq.common.ext

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager

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
