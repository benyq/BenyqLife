package com.benyq.novel.local.database

import android.content.Context
import com.benyq.novel.local.database.enity.BookChapterEntity
import com.benyq.novel.local.database.enity.BookInfoEntity
import com.benyq.novel.local.database.enity.BookRecordEntity
import com.benyq.novel.local.database.enity.MyObjectBox
import io.objectbox.Box
import io.objectbox.BoxStore
import io.objectbox.kotlin.boxFor

/**
 * @author benyq
 * @emil 1520063035@qq.com
 * create at 2020/2/5
 * description:
 */
object ObjectBox {
    lateinit var boxStore: BoxStore
        private set

    fun init(context: Context) {
        boxStore = MyObjectBox.builder()
            .androidContext(context.applicationContext)
            .build()
    }
}
