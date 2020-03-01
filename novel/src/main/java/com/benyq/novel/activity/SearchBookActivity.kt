package com.benyq.novel.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.benyq.common.ext.getFragment
import com.benyq.common.ui.LifecycleActivity
import com.benyq.novel.R
import com.benyq.novel.fragment.searchbook.SearchBookResultFragment
import com.benyq.novel.model.searchbook.SearchBookViewModel
import kotlinx.android.synthetic.main.novel_activity_search_book.*

class SearchBookActivity : LifecycleActivity<SearchBookViewModel>() {

    companion object {
        fun goto(context: Context) {
            context.startActivity(Intent(context, SearchBookActivity::class.java))
        }
    }

    private val mNavController: NavController by lazy {
        NavHostFragment.findNavController(
            searchBookNav
        )
    }

    override fun getLayoutId() = R.layout.novel_activity_search_book

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        toolbarBack.setOnClickListener {
            finish()
        }
        tvSearch.setOnClickListener {
            val key = etQuery.text.toString().trim()
            if (key.isNotEmpty()) {
                jumpToSearchResult()
                mViewModel.searchBook(key)
                mViewModel.mSearchHistoryData.value = key
            }
        }

        etQuery.addTextChangedListener {
            it?.run {
                if (mNavController.currentDestination?.label == "search_book_result") {
                    mNavController.popBackStack()
                }
                if (length > 0) {
                    mViewModel.autoComplete(toString())
                } else {
                    //隐藏补全界面
                    mViewModel.mSearchBackData.value = "hide"
                }
            }
        }
    }

    override fun dataObserver() {
        with(mViewModel) {
            mAutoCompleteData.observe(this@SearchBookActivity, Observer {
                //展示补全界面
            })

            mSearchBookData.observe(this@SearchBookActivity, Observer {
                //跳转到另一个fragment
                jumpToSearchResult()
            })

            mSearchBookKey.observe(this@SearchBookActivity, Observer {
                etQuery.setText(it)
                etQuery.setSelection(it.length)
            })
        }
    }

    override fun initData() {

    }


    private fun jumpToSearchResult() {
        if (getFragment(SearchBookResultFragment::class.java) == null) {
            mNavController.navigate(R.id.action_search_book_result)
        }
    }
}
