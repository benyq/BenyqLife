package com.benyq.common.ext

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import com.benyq.common.net.RxScheduler
import io.reactivex.Observable
import io.reactivex.Observer
import androidx.lifecycle.Observer as lifecycleObserver

/**
 * @author benyq
 * @emil 1520063035@qq.com
 * create at 2020/1/12
 * description: Rxjava2 拓展方法
 */
fun <T> Observable<T>.execute(observer: Observer<T>) {
    this.compose(RxScheduler.rxScheduler())
        .subscribe(observer)
}

fun <T> LifecycleOwner.lifecycleObserve(liveData: LiveData<T>, action: (it: T) -> Unit) {
    liveData.observe(this, lifecycleObserver<T> {
        action(it)
    })

}
