package com.benyq.common.net

import com.benyq.common.Constants
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * @author benyq
 * @emil 1520063035@qq.com
 * create at 2020/1/11
 * description:
 */
object RxScheduler {

    /**
     * 统一线程处理
     *
     * @param <T>
     * @return
    </T> */
    fun <T> rxScheduler(): ObservableTransformer<T, T> {
        return ObservableTransformer { upstream ->
            upstream.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        }
    }

    /**
     * 统一返回结果处理
     *
     * @param <T>
     * @return
    </T> */
    fun <T> handleResult(): ObservableTransformer<LifeResponse<T>, T> {
        return ObservableTransformer { upstream ->
            upstream.flatMap { response ->
                if (response.code == Constants.responseSuccess && response.data != null) {
                    createData(response.data)
                }else {
                    Observable.error(ApiException(response.code, response.msg))
                }
            }
        }
    }


    /**
     * Observable
     * @param <T>
     * @return
    </T> */
    private fun <T> createData(t: T): Observable<T> {
        return Observable.create { emitter ->
            try {
                emitter.onNext(t)
                emitter.onComplete()
            } catch (e: Exception) {
//                Timber.tag("HttpError").e(e)
                emitter.onError(e)
            }
        }
    }
}