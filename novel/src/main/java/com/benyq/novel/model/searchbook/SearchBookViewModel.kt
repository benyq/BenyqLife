package com.benyq.novel.model.searchbook

import androidx.lifecycle.MutableLiveData
import com.benyq.common.ext.execute
import com.benyq.common.model.BaseViewModel
import com.benyq.common.net.DefaultObserver
import com.benyq.novel.bean.AutoCompleteBean
import com.benyq.novel.bean.SearchBookBean

/**
 * @author benyq
 * @emil 1520063035@qq.com
 * create at 2020/1/12
 * description:
 */
class SearchBookViewModel : BaseViewModel<SearchBookRepository>() {

    /**
     * 发送事件给activity  搜索
     */
    val mSearchBookKey by lazy { MutableLiveData<String>() }
    /**
     * 发送事件给activity  恢复界面
     */
    val mSearchBackData by lazy { MutableLiveData<String>() }
    /**
     * 发送事件给activity  修改搜索历史
     */
    val mSearchHistoryData by lazy { MutableLiveData<String>() }

    val mAutoCompleteData by lazy { MutableLiveData<List<AutoCompleteBean>>() }
    val mSearchBookData by lazy { MutableLiveData<List<SearchBookBean>>() }
    val mHotBookData by lazy { MutableLiveData<List<String>>() }

    fun autoComplete(query: String) {
        mRepository.autoComplete(query)
            .execute(DefaultObserver(mAutoCompleteData, mRepository))

        mAutoCompleteData.value = listOf()
    }

    fun searchBook(query: String) {
        mRepository.searchBook(query)
            .execute(DefaultObserver(mSearchBookData, mRepository))
    }

    fun hotBook() {
        mRepository.hotBook()
            .execute(DefaultObserver(mHotBookData, mRepository))
        mHotBookData.value = listOf("奥世界冲天", "我是你的爹地", "从哪里来就滚回哪里去", "hello world", "牧神记")
    }
}