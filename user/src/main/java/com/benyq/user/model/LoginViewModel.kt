package com.benyq.user.model

import androidx.lifecycle.*
import com.benyq.common.ext.loge
import com.benyq.common.model.BaseViewModel
import org.jetbrains.annotations.NotNull


class LoginViewModel : BaseViewModel<LoginRepository>() , LifecycleObserver{

    val loginAccount by lazy { MutableLiveData<String>() }
    val loginPwd by lazy { MutableLiveData<String>() }

    val enableLogin = MediatorLiveData<Boolean>().apply {
        addSource(loginPwd, Observer {
            val pwd = loginPwd.value
            this.value = !pwd.isNullOrEmpty()
        })


    }


    fun login() {
        loge("开始登陆 ${loginAccount.value}  -- ${loginPwd.value}")
        if (loginAccount.value.isNullOrEmpty() || loginPwd.value.isNullOrEmpty()) {
            loginAccount.value = "你是哪颗葱"
            loginPwd.value = "登录个毛"
            return
        }
        loginAccount.value = "我要登陆了"
        loginPwd.value = "登录成功"
    }
}