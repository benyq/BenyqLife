package com.benyq.common.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.benyq.common.ext.getClass

/**
 * @author benyq
 * @emil 1520063035@qq.com
 * create at 2020/1/11
 * description:
 */
open class BaseViewModel<T : BaseRepository> : ViewModel(){


    val mRepository : T by lazy {
        (getClass<T>(this)).getDeclaredConstructor()
            .newInstance()
    }

    /**
     * 如果要添加别的Repository，要覆写onCleared
     */
    override fun onCleared() {
        super.onCleared()
        mRepository.unSubscribe()
    }
}