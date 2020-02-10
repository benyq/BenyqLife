package com.benyq.novel.model.bookshelf

import androidx.lifecycle.MutableLiveData
import com.benyq.common.ext.execute
import com.benyq.common.model.BaseRepository
import com.benyq.common.net.DefaultObserver
import com.benyq.common.net.RxScheduler
import com.benyq.novel.BookApi
import com.benyq.novel.local.database.LocalBookRepository
import com.benyq.novel.local.database.enity.BookInfoEntity

/**
 * @author benyq
 * @emil 1520063035@qq.com
 * create at 2020/1/12
 * description:
 */
class BookShelfRepository : BaseRepository(){

    fun getShelfBooks(liveData: MutableLiveData<List<BookInfoEntity>>){
        LocalBookRepository.getShelfBooks()
            .execute(DefaultObserver(liveData, this))
    }

}