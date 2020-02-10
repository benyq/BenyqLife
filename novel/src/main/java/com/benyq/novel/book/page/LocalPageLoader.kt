package com.benyq.novel.book.page

import android.util.Log
import com.benyq.common.ext.ioClose
import com.benyq.common.net.RxScheduler
import com.benyq.novel.book.ext.CustomerCharset
import com.benyq.novel.local.database.enity.BookChapterEntity
import com.benyq.novel.local.database.enity.BookInfoEntity
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.disposables.Disposable
import java.io.*
import java.nio.charset.Charset
import java.util.regex.Pattern

/**
 * @author benyq
 * @emil 1520063035@qq.com
 * create at 2020/2/9
 * description:
 */
class LocalPageLoader(pageView: PageView, bookInfo: BookInfoEntity) :
    PageLoader(pageView, bookInfo) {

    companion object {
        /**
         * 默认从文件中获取数据的长度
         */
        val BUFFER_SIZE = 512 * 1024
        /**
         * 没有标题的时候，每个章节的最大长度
         */
        val MAX_LENGTH_WITH_NO_CHAPTER = 10 * 1024

        //正则表达式章节匹配模式
        // "(第)([0-9零一二两三四五六七八九十百千万壹贰叁肆伍陆柒捌玖拾佰仟]{1,10})([章节回集卷])(.*)"
        val CHAPTER_PATTERNS = arrayOf(
            "^(.{0,8})(\u7b2c)([0-9\u96f6\u4e00\u4e8c\u4e24\u4e09\u56db\u4e94\u516d\u4e03\u516b\u4e5d\u5341\u767e\u5343\u4e07\u58f9\u8d30\u53c1\u8086\u4f0d\u9646\u67d2\u634c\u7396\u62fe\u4f70\u4edf]{1,10})([\u7ae0\u8282\u56de\u96c6\u5377])(.{0,30})$",
            "^(\\s{0,4})([\\(\u3010\u300a]?(\u5377)?)([0-9\u96f6\u4e00\u4e8c\u4e24\u4e09\u56db\u4e94\u516d\u4e03\u516b\u4e5d\u5341\u767e\u5343\u4e07\u58f9\u8d30\u53c1\u8086\u4f0d\u9646\u67d2\u634c\u7396\u62fe\u4f70\u4edf]{1,10})([\\.:\uff1a\u0020\u000C\t])(.{0,30})$",
            "^(\\s{0,4})([\\(\uff08\u3010\u300a])(.{0,30})([\\)\uff09\u3011\u300b])(\\s{0,2})$",
            "^(\\s{0,4})(\u6b63\u6587)(.{0,20})$",
            "^(.{0,4})(Chapter|chapter)(\\s{0,4})([0-9]{1,4})(.{0,30})$"
        )
    }

    private lateinit var mBookFile: File
    private var mLoadChapterDisposable: Disposable? = null
    private lateinit var mCharset: CustomerCharset
    private lateinit var mChapterPattern: Pattern

    init {
        mStatus = STATUS_PARING
    }

    override fun refreshChapterList() {
        /**
         * 在书架打开的时候判断是否存在file
         */
        mBookFile = File(mCollBook.coverUrl)

        //判断是否有缓存
        if (!mCollBook.isUpdate && mCollBook.bookChapters != null) {
            mChapterList = convertTxtChapter(mCollBook.bookChapters!!)

            isChapterListPrepare = true

            mPageChangeListener?.onCategoryFinish(mChapterList!!)

            // 加载并显示当前章节
            openChapter()
            return
        }

        mLoadChapterDisposable = Observable.create(ObservableOnSubscribe<Boolean> {
            loadChapters()
            it.onComplete()
        }).compose(RxScheduler.rxScheduler())
            .subscribe({

            }, {
                chapterError()
                Log.e("benyq", "file load error: ${it.message}")
            })
    }

    override fun getChapterReader(chapter: TxtChapter): BufferedReader {
        //从文件中获取数据
        val content = getChapterContent(chapter)
        val bais = ByteArrayInputStream(content)
        return BufferedReader(InputStreamReader(bais, mCharset.charsetName))
    }

    override fun hasChapterData(chapter: TxtChapter) = true

    private fun getChapterContent(chapter: TxtChapter): ByteArray {
        var bookStream: RandomAccessFile? = null
        try {
            bookStream = RandomAccessFile(mBookFile, "r")
            bookStream.seek(chapter.start)
            val extent: Int = (chapter.end - chapter.start).toInt()
            val content = ByteArray(extent)
            bookStream.read(content, 0, extent)
            return content
        } catch (e1: IOException) {
            e1.printStackTrace()
        } finally {
            ioClose(bookStream)
        }
        return ByteArray(0)
    }

    private fun convertTxtChapter(chapters: List<BookChapterEntity>): MutableList<TxtChapter> {
        return chapters.map {
            TxtChapter(it.chapterName, start = it.start, end = it.end)
        }.toMutableList()
    }

    @Throws(IOException::class)
    private fun loadChapters() {
        val chapters = mutableListOf<TxtChapter>()
        //获取文件流
        val bookStream = RandomAccessFile(mBookFile, "r")
        //寻找匹配文章标题的正则表达式，判断是否存在章节名
        val hasChapter = checkChapterType(bookStream)
        //加载章节
        val buffer = ByteArray(BUFFER_SIZE)
        //获取到的块起始点，在文件中的位置
        var curOffset: Long = 0
        //block的个数
        var blockPos = 0
        //读取的长度
        var length: Int

        //获取文件中的数据到buffer，直到没有数据为止
        while (bookStream.read(buffer, 0, buffer.size).apply { length = this } > 0) {
            blockPos++
            if (hasChapter) {
                val blockContent = String(buffer, 0, length, Charset.forName(mCharset.charsetName))
                //当前Block下使过的String的指针
                var seekPos = 0
                //进行正则匹配
                val matcher = mChapterPattern.matcher(blockContent)
            }
        }
    }


    /**
     * 1. 检查文件中是否存在章节名
     * 2. 判断文件中使用的章节名类型的正则表达式
     *
     * @return 是否存在章节名
     */
    @Throws(IOException::class)
    private fun checkChapterType(bookStream: RandomAccessFile) : Boolean{
        //首先获取128k数据
        val buffer = ByteArray(BUFFER_SIZE / 4)
        val length = bookStream.read(buffer, 0, buffer.size)
        CHAPTER_PATTERNS.forEach {
            val pattern = Pattern.compile(it, Pattern.MULTILINE)
            val matcher = pattern.matcher(String(buffer, 0, length, Charset.forName(mCharset.charsetName)))
            if (matcher.find()) {
                mChapterPattern = pattern
                //重置指针位置
                bookStream.seek(0)
                return true
            }
        }
        //重置指针位置
        bookStream.seek(0)
        return false
    }
}