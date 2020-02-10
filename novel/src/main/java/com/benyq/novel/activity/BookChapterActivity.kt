package com.benyq.novel.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.benyq.common.ui.LifecycleActivity
import com.benyq.novel.NovelIntentExtra
import com.benyq.novel.R
import com.benyq.novel.adapter.BookChapterAdapter
import com.benyq.novel.model.bookchapter.BookChapterViewModel
import kotlinx.android.synthetic.main.novel_activity_book_chapter.*

/**
 * @author benyq
 * @emil 1520063035@qq.com
 * create at 2020/1/12
 * description:章节目录
 */
class BookChapterActivity : LifecycleActivity<BookChapterViewModel>() {

    private val mChapterAdapter by lazy { BookChapterAdapter() }
    private var mBookId: Int = -1
    private var mChapterId: Int = -1

    companion object {
        fun goto(context: Context, bookId: Int, chapterId: Int) {
            context.startActivity(Intent(context, BookChapterActivity::class.java).apply {
                putExtra(NovelIntentExtra.chapterId, chapterId)
                    .putExtra(NovelIntentExtra.bookId, bookId)
            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBookId = intent.getIntExtra(NovelIntentExtra.bookId, -1)
        mChapterId = intent.getIntExtra(NovelIntentExtra.chapterId, -1)
    }

    override fun getLayoutId() = R.layout.novel_activity_book_chapter

    override fun initView() {
        super.initView()
        llRefresh.setOnClickListener {
            mViewModel.refreshBookChapter(mBookId)
        }

        llSort.setOnClickListener {

        }

        rvBookChapter.layoutManager = LinearLayoutManager(this)
        rvBookChapter.adapter = mChapterAdapter
        mChapterAdapter.setOnItemClickListener { adapter, view, position ->
            val intent = Intent()
//            intent.putExtra(NovelIntentExtra.chapterId, mChapterAdapter.data[position].id)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    override fun dataObserver() {
        with(mViewModel) {
            mBookChaptersData.observe(this@BookChapterActivity, Observer {
                mChapterAdapter.setNewData(it)
            })
        }
    }


    override fun initData() {
        mViewModel.getBookChapters(mBookId)
    }


}
