package com.benyq.novel.model.bookdetail

import androidx.lifecycle.MutableLiveData
import com.benyq.common.ext.execute
import com.benyq.common.model.BaseViewModel
import com.benyq.common.net.DefaultObserver
import com.benyq.novel.bean.BookDetailBean

/**
 * @author benyq
 * @emil 1520063035@qq.com
 * create at 2020/1/12
 * description:
 */
class BookDetailViewModel : BaseViewModel<BookDetailRepository>(){

    val mBookDetailData by lazy { MutableLiveData<BookDetailBean>() }

    fun bookDetail(bookId: String) {
        mRepository.bookDetail(bookId)
            .execute(DefaultObserver(mBookDetailData, mRepository))
    }
}