package com.benyq.common.ext

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import java.io.Serializable

/**
 * @author benyq
 * @emil 1520063035@qq.com
 * create at 2020/1/21
 * description:
 */
/**
 * 获取navigation中当前fragment
 * @deprecated 根据navigation.xml 文件中的fragment标签下的label 和 NavController.currentDestination，
 * 可以区分当前fragment
 */
fun AppCompatActivity.getFragment(): Fragment?{
    val navHostFragment = this.supportFragmentManager.fragments.first() as NavHostFragment
    if (navHostFragment.childFragmentManager.fragments.isNotEmpty()) {
        return navHostFragment.childFragmentManager.fragments[0]
    }
    return null
}


fun <F : Fragment> AppCompatActivity.getFragment(fragmentClass: Class<F>): Fragment?{
    val navHostFragment = this.supportFragmentManager.fragments.first() as NavHostFragment

    navHostFragment.childFragmentManager.fragments.forEach {
        if (fragmentClass.isAssignableFrom(it.javaClass)) {
            return it
        }
    }
    return null
}

inline fun <reified T : Activity> Context.startActivity(vararg params: Pair<String, Any?>) {
    val intent = Intent(this, T::class.java)
    if (params.isNotEmpty()) fillIntentArguments(intent, params)
    this.startActivity(intent)
}

inline fun <reified T : Activity> Fragment.startActivity(vararg params: Pair<String, Any?>) {
    val intent = Intent(context, T::class.java)
    if (params.isNotEmpty()) fillIntentArguments(intent, params)
    activity?.startActivity(intent)
}

fun fillIntentArguments(intent: Intent, params: Array<out Pair<String, Any?>>) {
    params.forEach {
        when (val value = it.second) {
            null -> intent.putExtra(it.first, null as Serializable?)
            is Int -> intent.putExtra(it.first, value)
            is Long -> intent.putExtra(it.first, value)
            is CharSequence -> intent.putExtra(it.first, value)
            is String -> intent.putExtra(it.first, value)
            is Float -> intent.putExtra(it.first, value)
            is Double -> intent.putExtra(it.first, value)
            is Char -> intent.putExtra(it.first, value)
            is Short -> intent.putExtra(it.first, value)
            is Boolean -> intent.putExtra(it.first, value)
            is Serializable -> intent.putExtra(it.first, value)
            is Bundle -> intent.putExtra(it.first, value)
            is Parcelable -> intent.putExtra(it.first, value)
            is Array<*> -> when {
                value.isArrayOf<CharSequence>() -> intent.putExtra(it.first, value)
                value.isArrayOf<String>() -> intent.putExtra(it.first, value)
                value.isArrayOf<Parcelable>() -> intent.putExtra(it.first, value)
                else -> throw Exception("Intent extra ${it.first} has wrong type ${value.javaClass.name}")
            }
            is IntArray -> intent.putExtra(it.first, value)
            is LongArray -> intent.putExtra(it.first, value)
            is FloatArray -> intent.putExtra(it.first, value)
            is DoubleArray -> intent.putExtra(it.first, value)
            is CharArray -> intent.putExtra(it.first, value)
            is ShortArray -> intent.putExtra(it.first, value)
            is BooleanArray -> intent.putExtra(it.first, value)
            else -> throw Exception("Intent extra ${it.first} has wrong type ${value.javaClass.name}")
        }
        return@forEach
    }
}
