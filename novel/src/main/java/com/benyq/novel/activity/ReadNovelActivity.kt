package com.benyq.novel.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SeekBar
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.benyq.benyqbookreader.ui.fragment.ReadSettingDialogFragment
import com.benyq.common.ext.hideKeyBoard
import com.benyq.common.ui.LifecycleActivity
import com.benyq.novel.R
import com.benyq.novel.book.page.PageLoader
import com.benyq.novel.book.page.PageView
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

    private var isShowController = true
    private lateinit var mBookInfo: BookInfoEntity
    private lateinit var mPageLoader: PageLoader

    private val mReadSettingDialog by lazy {
        ReadSettingDialogFragment.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bookInfo : BookInfoEntity? = intent.getParcelableExtra(BOOK_INFO)
        if (bookInfo == null) {
            Toast.makeText(this, "缺少参数", Toast.LENGTH_SHORT).show()
            finish()
        }else {
            mBookInfo = bookInfo
        }
    }

    override fun getLayoutId() = R.layout.novel_activity_read_novel

    override fun initView() {
        super.initView()
        mPageLoader = pageView.getPageLoader(mBookInfo)
        pageView.setPageTouchListener(object : PageView.PageTouchListener {
            override fun onTouch(): Boolean {
                dismissController()
                return true
            }

            override fun center() {
                if (isShowController){
                    dismissController()
                }else {
                    showController()
                }
            }

            override fun prePage() {
            }

            override fun nextPage() {
            }

            override fun cancel() {
            }

        })

//        tvLastChapter.setOnClickListener {
//            val lastChapter = mChapterList.indexOf(mCurrentChapter)
//            if (lastChapter == 0){
//                toast(R.string.current_first_chapter)
//            }else {
//                changeCurrentChapter(lastChapter - 1)
//            }
//        }
//
//        tvNextChapter.setOnClickListener {
//            val nextChapter = mChapterList.indexOf(mCurrentChapter)
//            if (nextChapter == mChapterList.size - 1){
//                toast(R.string.current_final_chapter)
//            }else {
//                changeCurrentChapter(nextChapter + 1)
//            }
//        }

        llBookChapter.setOnClickListener {
            BookChapterActivity.goto(this, 0, 0)
        }

        chapterSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
//                val bookChapterIndex = chapterSeekBar.progress * mChapterList.size / 100
//                changeCurrentChapter(bookChapterIndex, true)
            }

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            }
        })

        llSettings.setOnClickListener {
            mReadSettingDialog.showDialog(supportFragmentManager)
            dismissController()
        }
    }

    override fun dataObserver() {

    }


    override fun initData() {
    }

    private fun changeCurrentChapter(index: Int, progress: Boolean = false){
//        mCurrentIndex = index
//        mChapterList.forEachIndexed { i, booKChapterEntity ->
//            booKChapterEntity.selected = index == i
//        }
//        mCurrentChapter = mChapterList[index]
//        mViewModel.getChapterContent(mBookDetail.bookId, mCurrentChapter.linkUrl)
//        if (!progress){
//            chapterSeekBar.progress = mCurrentIndex * 100 / mChapterList.size
//        }
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

    private fun showController(){
        isShowController = !isShowController

        toolbar?.run {
            animate().setDuration(200).translationY(0f).start()
        }
        rlController.animate().setDuration(200).translationY(0f).start()
    }

    private fun dismissController(){
        isShowController = !isShowController

        toolbar?.run {
            animate().setDuration(200).translationY(-height.toFloat()).start()
        }
        rlController.animate().setDuration(200).translationY(rlController.height.toFloat()).start()
    }
}
