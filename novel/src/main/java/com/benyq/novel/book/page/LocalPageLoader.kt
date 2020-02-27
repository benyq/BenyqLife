package com.benyq.novel.book.page

import android.util.Log
import com.benyq.common.ext.ioClose
import com.benyq.common.net.RxScheduler
import com.benyq.novel.book.ext.CustomerCharset
import com.benyq.novel.book.ext.getCharset
import com.benyq.novel.local.database.LocalBookRepository
import com.benyq.novel.local.database.ObjectBox
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
        //获取文件编码
        mCharset = getCharset(mBookFile.absolutePath)
        //判断是否有缓存
        LocalBookRepository.bookInfoBox.attach(mCollBook)
        if (!mCollBook.isUpdate && !mCollBook.bookChapters.isNullOrEmpty()) {
            mChapterList = convertTxtChapter(mCollBook.bookChapters!!)
            isChapterListPrepare = true
            Log.e("benyq", "$mCollBook 判断是否有缓存: $mChapterList")
            mPageChangeListener?.onCategoryFinish(mChapterList!!)

            // 加载并显示当前章节
            openChapter()
            return
        }

        mLoadChapterDisposable = Observable.create(ObservableOnSubscribe<Boolean> {
            loadChapters()
            it.onNext(true)
            it.onComplete()
        }).compose(RxScheduler.rxScheduler())
            .subscribe({
                isChapterListPrepare = true;

                // 提示目录加载完成
                mPageChangeListener?.onCategoryFinish(mChapterList!!)


                // 存储章节到数据库
                val bookChapters = mChapterList?.mapIndexed { index, chapter->
                    val entity = BookChapterEntity(chapterName = chapter.title, start = chapter.start, end = chapter.end,
                        chapterIndex = "$index", contentFile = "local")
                    entity.bookInfo?.setAndPutTarget(mCollBook)
                    entity
                }
                LocalBookRepository.chapterBox.put(bookChapters)

                // 加载并显示当前章节
                openChapter()
                Log.e("benyq", "获取结果: $it")
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
        while (bookStream.read(buffer, 0, buffer.size).apply {
                length = this
                Log.e("benyq", "loadChapters length : $length")
            } > 0) {
            ++blockPos
            Log.e("benyq", "loadChapters: $blockPos")
            //如果存在Chapter
            if (hasChapter) {
                //将数据转换成String
                val blockContent = String(buffer, 0, length, Charset.forName(mCharset.charsetName))
                //当前Block下使过的String的指针
                var seekPos = 0
                //进行正则匹配
                val matcher = mChapterPattern.matcher(blockContent)
                //如果存在相应章节
                while (matcher.find()) {
                    //获取匹配到的字符在字符串中的起始位置
                    val chapterStart = matcher.start()

                    //如果 seekPos == 0 && nextChapterPos != 0 表示当前block处前面有一段内容
                    //第一种情况一定是序章 第二种情况可能是上一个章节的内容
                    if (seekPos == 0 && chapterStart != 0) {
                        //获取当前章节的内容
                        val chapterContent = blockContent.substring(seekPos, chapterStart)
                        //设置指针偏移
                        seekPos += chapterContent.length

                        //如果当前对整个文件的偏移位置为0的话，那么就是序章
                        if (curOffset == 0L) {
                            //创建序章
                            val preChapter = TxtChapter("序章")
                            preChapter.start = 0
                            //获取String的byte值,作为最终值
                            preChapter.end =
                                chapterContent.toByteArray(Charset.forName(mCharset.charsetName)).size.toLong()

                            //如果序章大小大于30才添加进去
                            if (preChapter.end - preChapter.start > 30) {
                                chapters.add(preChapter)
                            }

                            //创建当前章节
                            val curChapter = TxtChapter(matcher.group())
                            curChapter.start = preChapter.end
                            chapters.add(curChapter)
                        } else {
                            //获取上一章节
                            val lastChapter = chapters[chapters.size - 1]
                            //将当前段落添加上一章去
                            lastChapter.end += chapterContent.toByteArray(Charset.forName(mCharset.charsetName)).size.toLong()

                            //如果章节内容太小，则移除
                            if (lastChapter.end - lastChapter.start < 30) {
                                chapters.remove(lastChapter)
                            }

                            //创建当前章节
                            val curChapter = TxtChapter(matcher.group())
                            curChapter.start = lastChapter.end
                            chapters.add(curChapter)
                        }//否则就block分割之后，上一个章节的剩余内容
                    } else {
                        //是否存在章节
                        if (chapters.size != 0) {
                            //获取章节内容
                            val chapterContent = blockContent.substring(seekPos, matcher.start())
                            seekPos += chapterContent.length

                            //获取上一章节
                            val lastChapter = chapters[chapters.size - 1]
                            lastChapter.end =
                                lastChapter.start + chapterContent.toByteArray(Charset.forName(mCharset.charsetName)).size.toLong()

                            //如果章节内容太小，则移除
                            if (lastChapter.end - lastChapter.start < 30) {
                                chapters.remove(lastChapter)
                            }

                            //创建当前章节
                            val curChapter = TxtChapter(matcher.group())
                            curChapter.start = lastChapter.end
                            chapters.add(curChapter)
                        } else {
                            val curChapter = TxtChapter(matcher.group())
                            curChapter.start = 0
                            chapters.add(curChapter)
                        }//如果章节不存在则创建章节
                    }
                }
            } else {
                //章节在buffer的偏移量
                var chapterOffset = 0
                //当前剩余可分配的长度
                var strLength = length
                //分章的位置
                var chapterPos = 0

                while (strLength > 0) {
                    ++chapterPos
                    //是否长度超过一章
                    if (strLength > MAX_LENGTH_WITH_NO_CHAPTER) {
                        //在buffer中一章的终止点
                        var end = length
                        //寻找换行符作为终止点
                        for (i in chapterOffset + MAX_LENGTH_WITH_NO_CHAPTER until length) {
                            if (buffer[i] == CustomerCharset.BLANK) {
                                end = i
                                break
                            }
                        }
                        val chapter = TxtChapter("第" + blockPos + "章" + "(" + chapterPos + ")")
                        chapter.start = curOffset + chapterOffset.toLong() + 1
                        chapter.end = curOffset + end
                        chapters.add(chapter)
                        //减去已经被分配的长度
                        strLength -= (end - chapterOffset)
                        //设置偏移的位置
                        chapterOffset = end
                    } else {
                        val chapter = TxtChapter("第" + blockPos + "章" + "(" + chapterPos + ")")
                        chapter.start = curOffset + chapterOffset.toLong() + 1
                        chapter.end = curOffset + length
                        chapters.add(chapter)
                        strLength = 0
                    }
                }
            }//进行本地虚拟分章

            //block的偏移点
            curOffset += length.toLong()

            if (hasChapter) {
                //设置上一章的结尾
                val lastChapter = chapters[chapters.size - 1]
                lastChapter.end = curOffset
            }

            //当添加的block太多的时候，执行GC
            if (blockPos % 15 == 0) {
                System.gc()
                System.runFinalization()
            }
        }

        mChapterList = chapters
        ioClose(bookStream)
        System.gc()
        System.runFinalization()
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