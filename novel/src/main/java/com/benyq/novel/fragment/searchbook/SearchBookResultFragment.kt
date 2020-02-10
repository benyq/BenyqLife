package com.benyq.novel.fragment.searchbook

import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.benyq.common.ui.LifecycleFragment
import com.benyq.novel.R
import com.benyq.novel.activity.BookDetailActivity
import com.benyq.novel.adapter.SearchResultAdapter
import com.benyq.novel.model.searchbook.SearchBookViewModel
import kotlinx.android.synthetic.main.novel_fragment_search_book_result.*

/**
 * @author benyq
 * @emil 1520063035@qq.com
 * create at 2020/1/17
 * description:
 */
class SearchBookResultFragment : LifecycleFragment<SearchBookViewModel>() {

    private val mSearchResultAdapter : SearchResultAdapter by lazy { SearchResultAdapter() }

    override fun getLayoutId() = R.layout.novel_fragment_search_book_result

    override fun initView() {
        super.initView()
        rvSearchResult.layoutManager = LinearLayoutManager(mContext)
        rvSearchResult.adapter = mSearchResultAdapter
        mSearchResultAdapter.setOnItemClickListener { adapter, view, position ->
            //跳转到小说详情界面
            BookDetailActivity.goto(mContext, mSearchResultAdapter.data[position].bookId)
        }
    }

    override fun dataObserver() {
        mViewModel.mSearchBookData.observe(this, Observer {
            mSearchResultAdapter.setNewData(it)
        })
    }

    override fun initData() {
    }

}