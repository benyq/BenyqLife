package com.benyq.common.ui

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.benyq.common.model.BaseViewModel

abstract class DataBindingActivity<DB: ViewDataBinding, T : BaseViewModel<*>> : LifecycleActivity<T>() {

    protected lateinit var dataBind: DB

    override fun onCreate(savedInstanceState: Bundle?) {
        dataBindingEnabled = true
        super.onCreate(savedInstanceState)
    }

    override fun initDataBinging() {
        dataBind = DataBindingUtil.setContentView(
            this, getLayoutId())
    }

    override fun initView() {
        super.initView()
        dataBind.lifecycleOwner = this
    }

    override fun initData() {
    }

}