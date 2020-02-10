package com.benyq.benyqlife

import com.benyq.common.net.LifeResponse
import io.reactivex.Observable
import retrofit2.http.GET

/**
 * @author benyq
 * @emil 1520063035@qq.com
 * create at 2020/1/11
 * description:
 */
interface WanAdnroidService {

    @GET("wxarticle/chapters/json")
    fun hotKey(): Observable<LifeResponse<List<HotkeyEntity>>>
}