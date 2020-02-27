package com.benyq.novel.local.database

import android.util.Log
import com.benyq.novel.local.database.enity.*
import io.objectbox.Box
import io.objectbox.android.AndroidScheduler
import io.objectbox.kotlin.boxFor
import io.objectbox.rx.RxQuery
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers

/**
 * @author benyq
 * @emil 1520063035@qq.com
 * create at 2020/2/8
 * description:
 */
object LocalBookRepository {

    var chapterBox : Box<BookChapterEntity> = ObjectBox.boxStore.boxFor()
    var bookInfoBox : Box<BookInfoEntity> = ObjectBox.boxStore.boxFor()
    var bookRecordBox : Box<BookRecordEntity> = ObjectBox.boxStore.boxFor()

    fun saveBookRecord(record: BookRecordEntity) {
        bookRecordBox.put(record)
    }

    fun getBookRecord(bookId: Long): BookRecordEntity? {
        return bookRecordBox.query().equal(BookRecordEntity_.bookId, bookId).build().findFirst()
    }


    fun checkLocalBookIn(bookId: String): Boolean{
        val book = bookInfoBox.query().equal(BookInfoEntity_.bookId, bookId).build().findFirst()
        return book != null
    }

    fun addBooks(books: List<BookInfoEntity>) {
        bookInfoBox.put(books)
    }

    fun getShelfBooks(): Observable<List<BookInfoEntity>> {
        return RxQuery.observable(bookInfoBox.query().equal(BookInfoEntity_.imported, true)
            .orderDesc(BookInfoEntity_.readTime)
            .build())
    }

    fun getBookInfo(bookId: Long): BookInfoEntity? {
        return bookInfoBox.get(bookId)
    }
}