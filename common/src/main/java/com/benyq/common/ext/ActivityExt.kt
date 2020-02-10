package com.benyq.common.ext

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment

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
    if (!navHostFragment.childFragmentManager.fragments.isEmpty()) {
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