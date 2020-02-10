package com.benyq.novel.model.searchbook

import com.benyq.common.model.BaseRepository
import com.benyq.common.net.RxScheduler
import com.benyq.novel.BookApi
import com.benyq.novel.bean.AutoCompleteBean
import com.benyq.novel.bean.SearchBookBean
import io.reactivex.Observable

/**
 * @author benyq
 * @emil 1520063035@qq.com
 * create at 2020/1/12
 * description:
 */
class SearchBookRepository : BaseRepository(){

    fun autoComplete(query: String): Observable<List<AutoCompleteBean>> {
        return BookApi.bookApi.autoComplete(query)
            .compose(RxScheduler.handleResult())
    }

    fun searchBook(query: String): Observable<List<SearchBookBean>> {
        return BookApi.bookApi.searchBook(query)
            .compose(RxScheduler.handleResult())
    }

    fun hotBook(): Observable<List<String>> {
        return BookApi.bookApi.hotBook()
            .compose(RxScheduler.handleResult())
    }

}