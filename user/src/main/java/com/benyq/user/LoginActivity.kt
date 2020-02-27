package com.benyq.user

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.benyq.common.ui.DataBindingActivity
import com.benyq.common.ui.LifecycleActivity
import com.benyq.user.databinding.UserActivityLoginBinding
import com.benyq.user.model.LoginViewModel
import kotlinx.android.synthetic.main.user_activity_login.*

class LoginActivity : DataBindingActivity<UserActivityLoginBinding, LoginViewModel>() {

    override fun getLayoutId() = R.layout.user_activity_login

    override fun initView() {
        super.initView()
        dataBind.vm = mViewModel
        userBtnLogin.setOnClickListener {
            mViewModel.login()
        }
    }

    override fun dataObserver() {

    }

    override fun initData() {
    }


}
