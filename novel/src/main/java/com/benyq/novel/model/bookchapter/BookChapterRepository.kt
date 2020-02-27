package com.benyq.novel.model.bookchapter

import androidx.lifecycle.MutableLiveData
import com.benyq.common.ext.loge
import com.benyq.common.model.BaseRepository
import com.benyq.novel.local.database.LocalBookRepository
import com.benyq.novel.local.database.enity.BookChapterEntity
import com.benyq.novel.local.database.enity.BookInfoEntity

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
    fun getBookChapters(bookId: Long, bookChaptersData: MutableLiveData<List<BookChapterEntity>>) {
        //ObjectBox 的一对多关系和ViewModel搭配有点坑爹
        LocalBookRepository.getBookInfo(bookId)?.run {
            val bookInfo: List<BookChapterEntity> = bookChapters!!
            val data = mutableListOf<BookChapterEntity>()
            for (chapter in bookInfo) {
                data.add(chapter)
            }
            bookChaptersData.value = data
        }

    }
}