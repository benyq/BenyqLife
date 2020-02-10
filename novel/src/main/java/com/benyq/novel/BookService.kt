package com.benyq.novel

import com.benyq.common.net.LifeResponse
import com.benyq.novel.bean.AutoCompleteBean
import com.benyq.novel.bean.BookDetailBean
import com.benyq.novel.bean.SearchBookBean
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * @author benyq
 * @emil 1520063035@qq.com
 * create at 2020/1/12
 * description:
 */
interface BookService {


    /**
     * 关键词补全
     */
    @GET("auto-complete")
    fun autoComplete(@Query("query") query: String): Observable<LifeResponse<List<AutoCompleteBean>>>

    /**
     * 搜索小说
     */
    @GET("search-book")
    fun searchBook(@Query("query") query: String) : Observable<LifeResponse<List<SearchBookBean>>>

    /**
     * 热门书籍
     */
    @GET("hot-book")
    fun hotBook() : Observable<LifeResponse<List<String>>>


    @GET("book-detail")
    fun bookDetail(@Query("bookId") bookId: String): Observable<LifeResponse<BookDetailBean>>
}