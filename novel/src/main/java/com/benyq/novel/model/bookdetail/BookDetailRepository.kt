package com.benyq.novel.model.bookdetail

import com.benyq.common.model.BaseRepository
import com.benyq.common.net.RxScheduler
import com.benyq.novel.BookApi
import com.benyq.novel.bean.BookDetailBean
import io.reactivex.Observable

/**
 * @author benyq
 * @emil 1520063035@qq.com
 * create at 2020/1/12
 * description:
 */
class BookDetailRepository : BaseRepository(){

    fun bookDetail(bookId: String): Observable<BookDetailBean> {
        return BookApi.bookApi.bookDetail(bookId)
            .compose(RxScheduler.handleResult())
    }
}