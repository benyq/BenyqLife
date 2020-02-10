package com.benyq.common.net

import android.util.Log
import com.benyq.common.BaseApplication
import com.benyq.common.Constants
import com.benyq.common.ext.isJson
import com.benyq.common.local.LocalStorage
import com.benyq.common.ext.versionCode
import com.google.gson.GsonBuilder
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * @author benyq
 * @emil 1520063035@qq.com
 * create at 2020/1/11
 * description: 网络 retrofit
 */
object LifeApiManager {

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder().run {

            addInterceptor(object : Interceptor {
                override fun intercept(chain: Interceptor.Chain): Response {
                    val builder = chain.request().newBuilder()
                    builder.addHeader("token", LocalStorage.token)
                    builder.addHeader("version-code", BaseApplication.sInstance.versionCode())
                    val request = builder.build()
                    return chain.proceed(request)
                }
            })

            //val cacheFile = File(Constants.PATH_CACHE)
            //val cache = Cache(cacheFile, (1024 * 1024 * 50).toLong())

//            val map = mapOf(
//                "token" to LocalStorage.token,
//                "version-code" to BaseApplication.sInstance.versionCode()
//            )
//            //添加公共请求参数,用这个方法之后,token不能再修改,因为存在BasicParamsInterceptor.map中的值不能再修改
//            val basicParamsInterceptor = BasicParamsInterceptor.Builder()
//                .addHeaderParamsMap(map) // 可以添加 Map 格式的参数
//                .build()

//            addInterceptor(basicParamsInterceptor)

            val loggingInterceptor = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
                override fun log(message: String) {
                    if (message.isJson()) {
                        Log.d("benyq", message)
                    } else {
                        Log.d("benyq", message)
                    }
                }
            })
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            addInterceptor(loggingInterceptor)

            //设置超时
            connectTimeout(30, TimeUnit.SECONDS)
            readTimeout(30, TimeUnit.SECONDS)
            writeTimeout(30, TimeUnit.SECONDS)
            //错误重连
            retryOnConnectionFailure(true)
            build()
        }
    }


    private val retrofitBuilder: Retrofit.Builder by lazy {
        Retrofit.Builder().run {
            val gs = GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create()
            baseUrl(Constants.baseUrl)
            client(okHttpClient)
            addConverterFactory(GsonConverterFactory.create(gs))
            addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        }
    }


    fun <T> createService(clz: Class<T>): T {
        return retrofitBuilder.build().create(clz)
    }


    fun <T> createService(clz: Class<T>, baseUrl: String): T {
        return retrofitBuilder.baseUrl(baseUrl).build().create(clz)
    }
}