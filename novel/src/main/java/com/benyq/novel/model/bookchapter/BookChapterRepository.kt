package com.benyq.novel.model.bookchapter

import androidx.lifecycle.MutableLiveData
import com.benyq.common.model.BaseRepository
import com.benyq.novel.local.database.enity.BookChapterEntity

/**
 * @author benyq
 * @emil 1520063035@qq.com
 * create at 2020/1/12
 * description:
 */
class BookChapterRepository : BaseRepository() {

    /**
     * 这个应该从本地数据库中查找的
     */
    fun getBookChapters(bookChaptersData: MutableLiveData<List<BookChapterEntity>>) {

    }
}