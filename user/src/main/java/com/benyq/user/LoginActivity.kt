package com.benyq.user

import android.os.Bundle
import androidx.lifecycle.Observer
import com.benyq.common.ext.loge
import com.benyq.common.ui.DataBindingActivity
import com.benyq.user.databinding.UserActivityLoginBinding
import com.benyq.user.model.LoginViewModel

class LoginActivity : DataBindingActivity<UserActivityLoginBinding, LoginViewModel>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun getLayoutId() = R.layout.user_activity_login

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        dataBind.vm = mViewModel
        dataBind.click = ClickProxy()
    }

    override fun dataObserver() {
        mViewModel.loginAccount.observe(this, Observer {
            loge("数据变化 loginAccount $it")
        })
    }

    override fun initData() {
    }

    inner class ClickProxy {

        fun login() {
            mViewModel.login()
        }
    }

}
