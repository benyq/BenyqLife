package com.benyq.novel

import com.benyq.common.net.LifeApiManager

/**
 * @author benyq
 * @emil 1520063035@qq.com
 * create at 2020/1/17
 * description:
 */
object BookApi {

    val bookApi by lazy { LifeApiManager.createService(BookService::class.java) }
}