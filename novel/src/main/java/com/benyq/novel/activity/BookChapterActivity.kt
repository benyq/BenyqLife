package com.benyq.novel.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.benyq.common.ext.loge
import com.benyq.common.ui.LifecycleActivity
import com.benyq.novel.NovelIntentExtra
import com.benyq.novel.R
import com.benyq.novel.adapter.BookChapterAdapter
import com.benyq.novel.local.database.LocalBookRepository
import com.benyq.novel.local.database.enity.BookChapterEntity
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
    private var mBookId: Long = -1
    private var mChapterPos: Int = -1
    /**
     * 是否倒叙
     */
    private var isReverse = false

    companion object {
        fun goto(context: Activity, bookId: Long, chapterPos: Int, requestCode: Int = -1) {
            context.startActivityForResult(Intent(context, BookChapterActivity::class.java).apply {
                putExtra(NovelIntentExtra.chapterPos, chapterPos)
                    .putExtra(NovelIntentExtra.bookId, bookId)
            }, requestCode)
        }
    }

    override fun getLayoutId() = R.layout.novel_activity_book_chapter

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        mBookId = intent.getLongExtra(NovelIntentExtra.bookId, -1)
        mChapterPos = intent.getIntExtra(NovelIntentExtra.chapterPos, -1)

        LocalBookRepository.getBookInfo(mBookId)?.run {
            tvBookName.text = name
            if (isLocal) {
                llRefresh.isEnabled = false
            }
        }

        toolbarBack.setOnClickListener {
            finish()
        }

        llRefresh.setOnClickListener {
            mViewModel.refreshBookChapter(mBookId)
        }

        llSort.setOnClickListener {
            isReverse = !isReverse
            rvBookChapter.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, isReverse)
            if (isReverse) {
                tvSort.text = "正序"
            }else {
                tvSort.text = "倒序"
            }
            moveToCurrentIndex()
        }

        rvBookChapter.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, isReverse)
        rvBookChapter.adapter = mChapterAdapter
        mChapterAdapter.currentChapterPos = mChapterPos
        mChapterAdapter.setOnItemClickListener { adapter, view, position ->
            val intent = Intent()
            intent.putExtra(NovelIntentExtra.chapterPos, position)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    override fun dataObserver() {
        with(mViewModel) {
            mBookChaptersData.observe(this@BookChapterActivity, Observer {
                mChapterAdapter.setNewData(it as List<BookChapterEntity>)
                if (mChapterPos >= 0) {
                    moveToCurrentIndex()
                }
                loge("chapters $it")
                val value = "共${it.size}章"
                tvBookChapterNum.text = value
            })
        }
    }


    override fun initData() {
        mViewModel.getBookChapters(mBookId)
    }

    private fun moveToCurrentIndex() {
        rvBookChapter.smoothScrollToPosition(mChapterPos)
        val mLayoutManager = rvBookChapter.layoutManager as LinearLayoutManager
        mLayoutManager.scrollToPositionWithOffset(mChapterPos, 0)
    }
}
