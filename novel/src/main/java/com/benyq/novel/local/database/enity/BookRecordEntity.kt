package com.benyq.novel.local.database.enity

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

/**
 * @author benyq
 * @emil 1520063035@qq.com
 * create at 2020/2/7
 * description: 阅读记录
 */
@Entity
data class BookRecordEntity(
    @Id
    var bookId: Long = 0, var chapter: Int = 0, var pagePos: Int = 0
)