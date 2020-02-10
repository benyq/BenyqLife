package com.benyq.common.net

import com.google.gson.annotations.SerializedName

/**
 * @author benyq
 * @emil 1520063035@qq.com
 * create at 2020/1/11
 * description:
 */
data class LifeResponse<T>(val code: Int, val msg: String, val data: T?,
                             val count: Int = 0){

    companion object{
        fun <T> error(errorMsg: String): LifeResponse<T> {
            return LifeResponse(0, errorMsg, null)
        }

        fun <T> success(data: T, msg: String="success"): LifeResponse<T> {
            return LifeResponse(1, msg, data)
        }
    }

}