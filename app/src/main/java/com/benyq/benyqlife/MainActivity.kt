package com.benyq.benyqlife

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.benyq.common.net.DefaultObserver
import com.benyq.common.net.LifeApiManager
import com.benyq.common.net.LifeResponse
import com.benyq.common.net.RxScheduler
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val service = LifeApiManager.createService(WanAdnroidService::class.java)
        var dispose = service.hotKey()
            .compose(RxScheduler.rxScheduler())
            .subscribe({
                Log.e("benyq", it.data?.size.toString())
            }, {

            })
    }
}
