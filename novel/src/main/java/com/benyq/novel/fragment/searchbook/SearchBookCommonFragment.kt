package com.benyq.novel.fragment.searchbook

import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.benyq.common.ext.getClass
import com.benyq.common.ui.LifecycleFragment
import com.benyq.novel.R
import com.benyq.novel.adapter.HotBookAdapter
import com.benyq.novel.adapter.SearchAutoCompleteAdapter
import com.benyq.novel.adapter.SearchHistoryAdapter
import com.benyq.novel.bean.AutoCompleteBean
import com.benyq.novel.local.NovelLocalStorage
import com.benyq.novel.local.database.ObjectBox
import com.benyq.novel.local.database.enity.BookChapterEntity
import com.benyq.novel.model.searchbook.SearchBookViewModel
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import io.objectbox.Box
import io.objectbox.kotlin.boxFor
import kotlinx.android.synthetic.main.novel_fragment_search_book_common.*

/**
 * @author benyq
 * @emil 1520063035@qq.com
 * create at 2020/1/17
 * description: 打开SearchBookActivity时默认打开的Fragment
 */
class SearchBookCommonFragment : LifecycleFragment<SearchBookViewModel>() {

    private val mSearchHistoryAdapter by lazy { SearchHistoryAdapter() }
    private val mHotBookAdapter by lazy { HotBookAdapter() }
    private val mSearchAutoCompleteAdapter by lazy { SearchAutoCompleteAdapter() }
    private val mSearchHistory = NovelLocalStorage.searchHistory

    override fun getLayoutId() = R.layout.novel_fragment_search_book_common

    override fun initView() {
        super.initView()
        initRecyclerView()

        tvHistoryClear.setOnClickListener {
            clearSearchHistory()
            mSearchHistoryAdapter.notifyDataSetChanged()
        }
    }

    override fun dataObserver() {
        mViewModel.mHotBookData.observe(this, Observer {
            mHotBookAdapter.setNewData(it)
        })

        mViewModel.mAutoCompleteData.observe(this, Observer {
            nsvCommon.visibility = View.GONE
        })

        mViewModel.mSearchBackData.observe(this, Observer {
            nsvCommon.visibility = View.VISIBLE
        })

        mViewModel.mSearchHistoryData.observe(this, Observer {
            addSearchHistory(it)
        })
    }

    override fun initData() {
        val box: Box<BookChapterEntity> = ObjectBox.boxStore.boxFor()
        val entity = box.query().build().find()
        Log.e("benyq", "data $entity")
        mViewModel.hotBook()
        mSearchHistoryAdapter.setNewData(mSearchHistory)


    }

    override fun initViewModel() {
        val activity: AppCompatActivity = mContext as AppCompatActivity
        mViewModel = ViewModelProvider(activity).get(getClass(this))
    }

    override fun onDestroy() {
        super.onDestroy()
        NovelLocalStorage.searchHistory = mSearchHistory
    }

    private fun initRecyclerView() {
        //热门书籍
        rvHotBook.layoutManager = FlexboxLayoutManager(mContext).apply {
            flexWrap = FlexWrap.WRAP
            justifyContent = JustifyContent.FLEX_START
        }
        mHotBookAdapter.setOnItemClickListener { _, _, position ->
            searchBook(mHotBookAdapter.data[position])
        }
        rvHotBook.adapter = mHotBookAdapter

        //搜索历史
        rvSearchHistory.layoutManager = LinearLayoutManager(mContext)
        rvSearchHistory.adapter = mSearchHistoryAdapter
        mSearchHistoryAdapter.setOnItemClickListener { _, _, position ->
            mSearchHistoryAdapter.data[position].run {
                searchBook(searchKey, searchType)
            }
        }

        //搜索补全
        rvSearchAutoComplete.layoutManager = LinearLayoutManager(mContext)
        mSearchAutoCompleteAdapter.setOnItemClickListener { _, _, position ->
            mSearchAutoCompleteAdapter.data[position].run {
                searchBook(searchKey, searchType)
            }
        }
        rvSearchAutoComplete.adapter = mSearchAutoCompleteAdapter
    }

    private fun searchBook(key: String, searchType: Int = AutoCompleteBean.TYPE_BOOK) {
        mViewModel.mSearchBookKey.value = key
        addSearchHistory(key, searchType)
        mSearchHistoryAdapter.notifyDataSetChanged()
    }


    private fun addSearchHistory(searchKey: String, searchType: Int = AutoCompleteBean.TYPE_BOOK) {
        val entity = mSearchHistory.find {
            it.searchKey == searchKey
        }
        entity?.let {
            mSearchHistory.remove(it)
        }
        val newEntity = AutoCompleteBean(searchKey, searchType)
        mSearchHistory.add(0, newEntity)
    }

    private fun clearSearchHistory() {
        mSearchHistory.clear()
    }

}