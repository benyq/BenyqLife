package com.benyq.novel.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import com.benyq.common.ui.LifecycleActivity
import com.benyq.novel.R
import com.benyq.novel.model.bookdetail.BookDetailViewModel
import kotlinx.android.synthetic.main.novel_activity_book_detail.*

class BookDetailActivity : LifecycleActivity<BookDetailViewModel>() {

    companion object {
        private const val BOOK_ID: String = "bookId"

        fun goto(context: Context, bookId: String) {
            context.startActivity(Intent(context, BookDetailActivity::class.java).apply {
                putExtra(BOOK_ID, bookId)
            })
        }
    }

    private var mBookId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBookId = intent.getStringExtra(BOOK_ID)
        if (mBookId == null) {

        }
    }

    override fun getLayoutId() = R.layout.novel_activity_book_detail

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        headerView.setOnBackListener {
            finish()
        }
        llSubscribeBook.setOnClickListener {
            //加入书架，后台与本地,通知书架修改（或者返回时刷新都可以）
        }
        tvRead.setOnClickListener {
//            ReadNovelActivity.goto(this, mBookId!!)
        }
    }

    override fun dataObserver() {
        mViewModel.mBookDetailData.observe(this, Observer {

        })
    }

    override fun initData() {

        mViewModel.bookDetail(mBookId!!)
    }

}
