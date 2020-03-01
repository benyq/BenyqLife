package com.benyq.novel.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.SeekBar
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.benyq.novel.fragment.ReadSettingDialogFragment
import com.benyq.common.ui.LifecycleActivity
import com.benyq.novel.NovelIntentExtra
import com.benyq.novel.R
import com.benyq.novel.book.page.PageLoader
import com.benyq.novel.book.page.PageView
import com.benyq.novel.book.page.TxtChapter
import com.benyq.novel.local.database.enity.BookInfoEntity
import com.benyq.novel.model.readnovel.ReadNovelViewModel
import kotlinx.android.synthetic.main.novel_activity_read_novel.*

/**
* @author benyq
* @emil 1520063035@qq.com
* create at 2020/1/22
* description: 阅读小说
*/
class ReadNovelActivity : LifecycleActivity<ReadNovelViewModel>() {

    companion object {
        private const val BOOK_INFO: String = "bookId"

        fun goto(context: Context, book: BookInfoEntity) {
            context.startActivity(Intent(context, ReadNovelActivity::class.java).apply {
                putExtra(BOOK_INFO, book)
            })
        }
    }
    private val bookChapterRequestCode = 11
    private lateinit var mBookInfo: BookInfoEntity
    private lateinit var mPageLoader: PageLoader
    private lateinit var mTopInAnim: Animation
    private lateinit var mTopOutAnim: Animation
    private lateinit var mBottomInAnim: Animation
    private lateinit var mBottomOutAnim: Animation
    private var mChapterPos = 0
    private val mReadSettingDialog by lazy {
        ReadSettingDialogFragment.getInstance(mPageLoader)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val bookInfo : BookInfoEntity? = intent.getParcelableExtra(BOOK_INFO)
        if (bookInfo == null) {
            Toast.makeText(this, "缺少参数", Toast.LENGTH_SHORT).show()
            finish()
        }else {
            mBookInfo = bookInfo
        }
        super.onCreate(savedInstanceState)
    }

    override fun getLayoutId() = R.layout.novel_activity_read_novel

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        initMenuAnim()

//        rlController.visibility = GONE
//        headerView.visibility = GONE

        headerView.setOnBackListener {
            finish()
        }
        mPageLoader = pageView.getPageLoader(mBookInfo)
        mPageLoader.setOnPageChangeListener(object: PageLoader.OnPageChangeListener {
            override fun requestChapters(requestChapters: List<TxtChapter>) {

            }

            override fun onCategoryFinish(chapters: List<TxtChapter>) {
                hideAndShowController()
            }

            override fun onPageCountChange(count: Int) {
                chapterSeekBar.max = 0.coerceAtLeast(count - 1)
                chapterSeekBar.progress = 0
                // 如果处于错误状态，那么就冻结使用
                chapterSeekBar.isEnabled =
                    !(mPageLoader.getPageStatus() == PageLoader.STATUS_LOADING || mPageLoader.getPageStatus() == PageLoader.STATUS_ERROR)
                tvReadPageTip.visibility = GONE

            }

            override fun onPageChange(pos: Int) {
                chapterSeekBar.progress = pos
            }

            override fun onChapterChange(pos: Int) {
                mChapterPos = pos
            }

        })
        pageView.setPageTouchListener(object : PageView.PageTouchListener {
            override fun onTouch() = !dismissController()

            override fun center() {
                hideAndShowController()
            }

            override fun prePage() {
            }

            override fun nextPage() {
            }

            override fun cancel() {
            }

        })

        tvLastChapter.setOnClickListener {
           mPageLoader.skipPreChapter()
        }

        tvNextChapter.setOnClickListener {
            mPageLoader.skipNextChapter()
        }

        llBookChapter.setOnClickListener {
            BookChapterActivity.goto(this, mBookInfo.id, mChapterPos, bookChapterRequestCode)
            hideAndShowController()
        }

        chapterSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                //进行切换
                val pagePos = chapterSeekBar.progress
                if (pagePos != mPageLoader.getPagePos()) {
                    mPageLoader.skipToPage(pagePos)
                }
                //隐藏提示
                tvReadPageTip.visibility = GONE
            }

            @SuppressLint("SetTextI18n")
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (headerView.visibility == VISIBLE) {
                    //显示标题
                    tvReadPageTip.text = "${progress + 1}/${chapterSeekBar.max + 1}"
                    tvReadPageTip.visibility = VISIBLE
                }
            }
        })

        llSettings.setOnClickListener {
            mReadSettingDialog.showDialog(supportFragmentManager)
            hideAndShowController()
        }
    }

    override fun dataObserver() {

    }


    override fun initData() {
        if (mBookInfo.imported) {
            mPageLoader.refreshChapterList()
        }
    }

    override fun onPause() {
        super.onPause()
        if (mBookInfo.imported) {
            mPageLoader.saveRecord()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mPageLoader.closeBook()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == bookChapterRequestCode) {
            data?.getIntExtra(NovelIntentExtra.chapterPos, -1)?.apply {
                if (this >= 0) {
                    mPageLoader.skipToChapter(this)
                }
            }
        }
    }

    //初始化菜单动画
    private fun initMenuAnim() {
        mTopInAnim = AnimationUtils.loadAnimation(this, R.anim.slide_top_in)
        mTopOutAnim =
            AnimationUtils.loadAnimation(this, R.anim.slide_top_out)
        mBottomInAnim =
            AnimationUtils.loadAnimation(this, R.anim.slide_bottom_in)
        mBottomOutAnim =
            AnimationUtils.loadAnimation(this, R.anim.slide_bottom_out)
        //退出的速度要快
        mTopOutAnim.duration = 200
        mBottomOutAnim.duration = 200
    }

    private fun showWhetherAddShelf(){

        MaterialDialog.Builder(this)
            .title(R.string.dialog_title_tips)
            .content(R.string.novel_dialog_message_add_shelf)
            .positiveText(R.string.confirm)
            .negativeText(R.string.cancel)
            .onPositive { dialog, which ->
//                mViewModel.subscribeBook(mBookDetail.bookId)
            }
            .onNegative{ dialog, which ->
                finish()
            }.show()

    }
    private fun dismissController(): Boolean{
        if (rlController.visibility == VISIBLE) {
            hideAndShowController()
            return true
        }
        return false
    }

    private fun hideAndShowController() {
        if (rlController.visibility == VISIBLE) {

            headerView.startAnimation(mTopOutAnim)
            rlController.startAnimation(mBottomOutAnim)
            tvReadPageTip.visibility = GONE
            rlController.visibility = GONE
            headerView.visibility = GONE

        }else {
            rlController.visibility = VISIBLE
            headerView.visibility = VISIBLE
            headerView.startAnimation(mTopInAnim)
            rlController.startAnimation(mBottomInAnim)
        }

    }
}
