package com.benyq.novel.book.page

/**
 * @author benyq
 * @emil 1520063035@qq.com
 * create at 2020/2/6
 * description:
 */
data class TxtChapter(
    val title: String,
    /**
     * 网络小说缓存再本地的文件
     */
    val fileName: String = "",
    /**
     * 章节内容在文章中的起始位置(本地)
     */
    var start: Long = 0,
    /**
     * 章节内容在文章中的终止位置(本地)
     */
    var end: Long = 0
)