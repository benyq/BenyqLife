package com.benyq.novel.bean

/**
 * @author benyq
 * @emil 1520063035@qq.com
 * create at 2020/1/17
 * description:
 */
class SearchBookBean(
    val bookId: String,
    val bookName: String,
    val bookCover: String,
    val bookAuthor: String,
    /**
     * 更新时间
     */
    val updateedTime: String,
    val bookIntroduction: String,
    val bookCategory: String,
    /**
     * 连载状态
     * 0 连载 1  完结
     */
    val updateState: Int
)