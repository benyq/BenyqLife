package com.benyq.novel.fragment

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.benyq.common.ui.LifecycleFragment
import com.benyq.novel.R
import com.benyq.novel.activity.BookMoreFeatureActivity
import com.benyq.novel.activity.ReadNovelActivity
import com.benyq.novel.activity.SearchBookActivity
import com.benyq.novel.adapter.BookShelfAdapter
import com.benyq.novel.model.bookshelf.BookShelfViewModel
import kotlinx.android.synthetic.main.novel_fragment_book_shelf.*

/**
 * @author benyq
 * @emil 1520063035@qq.com
 * create at 2020/1/12
 * description:
 */
class BookshelfFragment : LifecycleFragment<BookShelfViewModel>(){

    companion object {
        fun newInstance() : BookshelfFragment {
            return BookshelfFragment()
        }
    }

    private val mAdapter: BookShelfAdapter by lazy { BookShelfAdapter() }

    override fun getLayoutId() = R.layout.novel_fragment_book_shelf

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        ivSearchBook.setOnClickListener {
            SearchBookActivity.goto(mContext)
        }

        ivMoreFunction.setOnClickListener {
            BookMoreFeatureActivity.goto(mContext)
        }

        rvShelfBooks.layoutManager = LinearLayoutManager(mContext)
        rvShelfBooks.adapter = mAdapter
        mAdapter.setOnItemClickListener { _, _, position ->
            mAdapter.getItem(position)?.run {
                ReadNovelActivity.goto(mContext, this)
            }
        }
        swipeLayout.isRefreshing = true
        swipeLayout.setOnRefreshListener(this::initData)
    }

    override fun dataObserver() {
        with(mViewModel) {
            mShelfBooksData.observe(viewLifecycleOwner, Observer {
                swipeLayout.isRefreshing = false
                mAdapter.setNewData(it)
            })
        }
    }

    override fun initData() {
        mViewModel.getShelfBooks()
    }


}