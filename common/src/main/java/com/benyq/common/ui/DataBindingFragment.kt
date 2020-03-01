package com.benyq.common.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.benyq.common.model.BaseViewModel

/**
 *
 * @author benyq
 * @time 2020/2/27
 * @e-mail 1520063035@qq.com
 * @note 
 */
abstract class DataBindingFragment <DB: ViewDataBinding, T : BaseViewModel<*>> : LifecycleFragment<T>() {

    protected lateinit var dataBind: DB

    override fun onCreate(savedInstanceState: Bundle?) {
        dataBindingEnabled = true
        super.onCreate(savedInstanceState)
    }

    override fun initDataBinging(inflater: LayoutInflater, container: ViewGroup?) : View{
        dataBind = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        return dataBind.root
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        dataBind.lifecycleOwner = this
    }

    override fun initData() {
    }
}