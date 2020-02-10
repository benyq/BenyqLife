package com.benyq.novel.model.bookshelf

import androidx.lifecycle.MutableLiveData
import com.benyq.common.model.BaseViewModel
import com.benyq.novel.local.database.enity.BookInfoEntity

/**
 * @author benyq
 * @emil 1520063035@qq.com
 * create at 2020/1/12
 * description:
 */
class BookShelfViewModel : BaseViewModel<BookShelfRepository>(){

    val mShelfBooksData by lazy { MutableLiveData<List<BookInfoEntity>>() }

    fun getShelfBooks(){
        mRepository.getShelfBooks(mShelfBooksData)
    }
}