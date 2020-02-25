package com.benyq.novel.activity

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.benyq.common.NormalProgressDialogManager
import com.benyq.common.ext.md5
import com.benyq.common.net.RxScheduler
import com.benyq.common.ui.BaseActivity
import com.benyq.novel.R
import com.benyq.novel.adapter.LocalBooksAdapter
import com.benyq.novel.bean.LocalBookBean
import com.benyq.novel.local.database.LocalBookRepository
import com.benyq.novel.local.database.enity.BookInfoEntity
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.novel_activity_local_book.*


/**
 * @author benyq
 * @emil 1520063035@qq.com
 * create at 2020/2/9
 * description: 导入本地书籍
 */
class LocalBookActivity : BaseActivity() {

    companion object {
        fun goto(context: Context) {
            context.startActivity(Intent(context, LocalBookActivity::class.java))
        }
    }

    private val mAdapter by lazy { LocalBooksAdapter() }
    private var mDisposable: Disposable? = null

    override fun getLayoutId() = R.layout.novel_activity_local_book


    override fun initView() {
        headerView.run {
            setOnBackListener {
                finish()
            }
            setOnOperateListener {
                scanLocalBook()
            }
            setOperateTitle("扫描")
        }
        rvLocalBooks.layoutManager = LinearLayoutManager(this)
        rvLocalBooks.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            ).apply {
                ContextCompat.getDrawable(this@LocalBookActivity, R.color.color_divier)?.run {
                    setDrawable(this)
                }
            })
        mAdapter.setOnItemClickListener { _, _, position ->
            mAdapter.getItem(position)?.run {
                checked = !checked
                mAdapter.notifyItemChanged(position)
            }
        }
        (rvLocalBooks.itemAnimator as DefaultItemAnimator).supportsChangeAnimations = false
        rvLocalBooks.adapter = mAdapter

        btnImportBook.setOnClickListener {
            //保存到本地
            val dataList = mAdapter.getSelectedBooks().map {
                BookInfoEntity().apply {
                    bookId = it.filePath.md5()
                    name = it.bookName
                    isLocal = true
                    cateLog = "本地小说"
                    coverUrl = it.filePath
                    imported = true
                }
            }
            LocalBookRepository.addBooks(dataList)
            finish()
        }

    }

    override fun initData() {
//        scanLocalBook()
        val dataList = LocalBookRepository.bookInfoBox.query().build().find()
        Log.e("benyq", "onComplete $dataList")

    }

    override fun onDestroy() {
        super.onDestroy()
        mDisposable?.dispose()
    }

    private fun scanLocalBook() {
        Log.e("benyq", "Uri ${MediaStore.Files.getContentUri("external")}")
        NormalProgressDialogManager.showLoading(this, false)
        val projection = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.SIZE,
            MediaStore.Files.FileColumns.DATE_MODIFIED
        )
        val sql =
            "(" + MediaStore.Files.FileColumns.DATA + " LIKE '%.txt'" + " and " + MediaStore.Files.FileColumns.SIZE + " > 102400)"
        mDisposable = Observable.create(ObservableOnSubscribe<Cursor> { emitter ->
            val cursor = contentResolver.query(
                MediaStore.Files.getContentUri("external"),
                projection,
                sql,
                null,
                null
            )
            if (cursor == null) {
                emitter.onError(Throwable("查不到数据"))
            } else {
                emitter.onNext(cursor)
            }
            emitter.onComplete()
        }).compose(RxScheduler.rxScheduler())
            .doFinally {
                NormalProgressDialogManager.dismissLoading()
            }
            .subscribe({
                Log.e("benyq", "onComplete ${it.count}")
                val dataList = mutableListOf<LocalBookBean>()
                if (it.moveToFirst()) {
                    do {
                        val path =
                            it.getString(it.getColumnIndex(MediaStore.Files.FileColumns.DATA))
                        val name = path.substringAfterLast("/")
                        val md5Id = path.md5()
                        val bookBean =
                            LocalBookBean(name, path, LocalBookRepository.checkLocalBookIn(md5Id))
                        Log.e("benyq", "book ${bookBean}")
                        dataList.add(bookBean)
                    } while (it.moveToNext())
                }
                it.close()
                val text = "共扫描到 ${dataList.size} 本"
                tvBookSize.text = text
                mAdapter.setNewData(dataList)
            }, {
                Log.e("benyq", "error ${it.message}")
            })
    }
}
