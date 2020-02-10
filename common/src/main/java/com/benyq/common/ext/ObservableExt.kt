package com.benyq.common.ext

import com.benyq.common.net.LifeResponse
import com.benyq.common.net.RxScheduler
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

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