package com.benyq.common.model

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * @author benyq
 * @emil 1520063035@qq.com
 * create at 2020/1/12
 * description:
 */
open class BaseRepository {

    private val mCompositeDisposable: CompositeDisposable by lazy { CompositeDisposable() }

    fun subscribe(disposable: Disposable) = mCompositeDisposable.add(disposable)

    fun unSubscribe() = mCompositeDisposable.dispose()
}