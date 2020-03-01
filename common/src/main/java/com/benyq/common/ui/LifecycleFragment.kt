package com.benyq.common.ui

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.benyq.common.ext.getClass
import com.benyq.common.model.BaseViewModel

/**
 * @author benyq
 * @emil 1520063035@qq.com
 * create at 2020/1/12
 * description:
 */
abstract class LifecycleFragment<T : BaseViewModel<*>> : BaseFragment(){

    lateinit var mViewModel : T


    override fun initView(savedInstanceState: Bundle?) {
        initViewModel()
        //设置view的observer
        dataObserver()

    }


    abstract fun dataObserver()

    open fun initViewModel() {
        mViewModel =  ViewModelProvider(this).get(getClass(this))
    }
}