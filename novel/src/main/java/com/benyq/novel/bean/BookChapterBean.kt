package com.benyq.novel.bean

/**
 * @author benyq
 * @emil 1520063035@qq.com
 * create at 2020/2/2
 * description:
 */
data class BookChapterBean(
    val id: String,
    val chapterName: String,
    /**
     * 章节正文网址
     */
    val chapterLink: String
)