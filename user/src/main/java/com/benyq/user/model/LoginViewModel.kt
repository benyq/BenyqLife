package com.benyq.user.model

import androidx.lifecycle.MutableLiveData
import com.benyq.common.ext.loge
import com.benyq.common.model.BaseViewModel

class LoginViewModel : BaseViewModel<LoginRepository>(){

    val loginAccount by lazy { MutableLiveData<String>() }
    val loginPwd by lazy { MutableLiveData<String>() }

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