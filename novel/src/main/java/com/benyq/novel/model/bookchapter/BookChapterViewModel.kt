package com.benyq.novel.model.bookchapter

import androidx.lifecycle.MutableLiveData
import com.benyq.common.model.BaseViewModel
import com.benyq.novel.local.database.enity.BookChapterEntity

/**
 * @author benyq
 * @emil 1520063035@qq.com
 * create at 2020/1/12
 * description:
 */
class BookChapterViewModel : BaseViewModel<BookChapterRepository>() {

    val mBookChaptersData by lazy { MutableLiveData<List<BookChapterEntity>>() }

    fun getBookChapters(bookId: Long) {
        mRepository.getBookChapters(bookId, mBookChaptersData)
    }

    fun refreshBookChapter(bookId: Long) {
    }
}