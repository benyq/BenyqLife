package com.benyq.novel.book.page

/**
 * @author benyq
 * @emil 1520063035@qq.com
 * create at 2020/2/6
 * description: 每个页面里的内容
 */
data class TxtPage(
    var position: Int = 0,
    var title: String = "",
    var titleLines: Int = 0,
    var lines: List<String> = arrayListOf()
)