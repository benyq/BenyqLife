package com.benyq.novel.book.page

import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.Log
import androidx.core.content.ContextCompat
import com.benyq.common.ext.ioClose
import com.benyq.common.net.RxScheduler
import com.benyq.novel.ReadSettingConfig
import com.benyq.novel.StringUtils
import com.benyq.novel.local.database.LocalBookRepository
import com.benyq.novel.local.database.enity.BookInfoEntity
import com.benyq.novel.local.database.enity.BookRecordEntity
import com.blankj.utilcode.util.SizeUtils
import com.blankj.utilcode.util.TimeUtils.date2String
import io.reactivex.*
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.IOException
import java.util.*

/**
 * @author benyq
 * @emil 1520063035@qq.com
 * create at 2020/2/6
 * description: 页面加载器基类，子类为 网络 与本地
 */
abstract class PageLoader(var mPageView: PageView? = null,
                          /**
                           * 书本对象
                           */
                          protected val mCollBook : BookInfoEntity) {
    // 当前页面的状态
    val STATUS_LOADING = 1         // 正在加载
    val STATUS_FINISH = 2          // 加载完成
    val STATUS_ERROR = 3           // 加载错误 (一般是网络加载情况)
    val STATUS_EMPTY = 4           // 空数据
    val STATUS_PARING = 5          // 正在解析 (装载本地数据)
    val STATUS_PARSE_ERROR = 6     // 本地文件解析错误(暂未被使用)
    val STATUS_CATEGORY_EMPTY = 7  // 获取到的目录为空

    // 默认的显示参数配置
    private val DEFAULT_MARGIN_HEIGHT = 28F
    private val DEFAULT_MARGIN_WIDTH = 15F
    private val DEFAULT_TIP_SIZE = 12f
    private val EXTRA_TITLE_SIZE = 4f

    // 当前章节列表
    protected var mChapterList: MutableList<TxtChapter> ? = null
    // 监听器
    protected var mPageChangeListener: OnPageChangeListener? = null

    private lateinit var mContext: Context
    // 当前显示的页
    private lateinit var mCurPage: TxtPage
    // 上一章的页面列表缓存
    private var mPrePageList: MutableList<TxtPage>? = null
    // 当前章节的页面列表
    private var mCurPageList: MutableList<TxtPage>? = null
    // 下一章的页面列表缓存
    private var mNextPageList: MutableList<TxtPage>? = null

    // 绘制电池的画笔
    private lateinit var mBatteryPaint: Paint
    // 绘制提示的画笔
    private lateinit var mTipPaint: Paint
    // 绘制标题的画笔
    private lateinit var mTitlePaint: Paint
    // 绘制背景颜色的画笔(用来擦除需要重绘的部分)
    private lateinit var mBgPaint: Paint
    // 绘制小说内容的画笔
    private lateinit var mTextPaint: TextPaint
    // 被遮盖的页，或者认为被取消显示的页
    private var mCancelPage: TxtPage? = null
    // 存储阅读记录类
    private lateinit var mBookRecord: BookRecordEntity

    private var mPreLoadDisp: Disposable? = null

    /*****************params，一些参数**************************/
    // 当前的状态
    protected var mStatus = STATUS_LOADING
    // 判断章节列表是否加载完成
    protected var isChapterListPrepare: Boolean = false

    // 是否打开过章节
    private var isChapterOpen: Boolean = false
    private var isFirstOpen = true
    private var isClose: Boolean = false
    // 页面的翻页效果模式
    private var mPageMode: PageMode? = null
    // 加载器的颜色主题
    private var mPageStyle: PageStyle? = null
    //当前是否是夜间模式
    private var isNightMode: Boolean = false
    //书籍绘制区域的宽高
    private var mVisibleWidth: Int = 0
    private var mVisibleHeight: Int = 0
    //应用的宽高
    private var mDisplayWidth: Int = 0
    private var mDisplayHeight: Int = 0
    //间距
    private var mMarginWidth: Int = 0
    private var mMarginHeight: Int = 0
    //字体的颜色
    private var mTextColor: Int = 0
    //标题的大小
    private var mTitleSize: Int = 0
    //字体的大小
    private var mTextSize: Int = 0
    //行间距
    private var mTextInterval: Int = 0
    //标题的行间距
    private var mTitleInterval: Int = 0
    //段落距离(基于行间距的额外距离)
    private var mTextPara: Int = 0
    private var mTitlePara: Int = 0
    //电池的百分比
    private var mBatteryLevel: Int = 0
    //当前页面的背景
    private var mBgColor: Int = 0

    // 当前章
    protected var mCurChapterPos = 0
    //上一章的记录
    private var mLastChapterPos = 0


    init {
        mContext = mPageView!!.context
        mChapterList = arrayListOf()
        //初始化参数
        initParams()
        //初始化画笔
        initPaint()
        //初始化pageView
        initPageView()

    }

    private fun initParams() {
        mPageMode = ReadSettingConfig.turnPageMode
        mPageStyle = ReadSettingConfig.pageStyle
        mMarginWidth = SizeUtils.dp2px(DEFAULT_MARGIN_HEIGHT)
        mMarginHeight = SizeUtils.dp2px(DEFAULT_MARGIN_HEIGHT)

        setUpTextParams(ReadSettingConfig.fontSize)
    }

    private fun initPaint() {
        //提示画笔
        mTipPaint = Paint().apply {
            color = mTextColor
            textAlign = Paint.Align.LEFT // 绘制的起始点
            textSize = SizeUtils.dp2px(DEFAULT_TIP_SIZE).toFloat() // Tip默认的字体大小
            isAntiAlias = true
            isSubpixelText = true
        }


        // 绘制页面内容的画笔
        mTextPaint = TextPaint().apply {
            color = mTextColor
            textSize = mTextSize.toFloat()
            isAntiAlias = true
        }


        // 绘制标题的画笔
        mTitlePaint = TextPaint().apply {
            color = mTextColor
            textSize = mTitleSize.toFloat()
            style = Paint.Style.FILL_AND_STROKE
            typeface = Typeface.DEFAULT_BOLD
            isAntiAlias = true
        }


        // 绘制背景的画笔
        mBgPaint = Paint()
        mBgPaint.color = mBgColor

        // 绘制电池的画笔
        mBatteryPaint = Paint().apply {
            isAntiAlias = true
            isDither = true
        }

        //初始化页面样式
        setNightMode(ReadSettingConfig.isNightMode)

    }

    private fun initPageView() {
        //配置参数
        mPageView?.setPageMode(mPageMode!!)
        mPageView?.setBgColor(mBgColor)
    }

    /****************************** public method***************************/
    /**
     * 翻页动画
     *
     * @param pageMode:翻页模式
     * @see PageMode
     */
    fun setPageMode(pageMode: PageMode) {
        mPageMode = pageMode

        mPageView?.setPageMode(pageMode)
        ReadSettingConfig.turnPageMode = pageMode

        // 重新绘制当前页
        mPageView?.drawCurPage(false)
    }

    /**
     * 设置夜间模式
     */
    fun setNightMode(nightMode: Boolean) {
        ReadSettingConfig.isNightMode = nightMode
        isNightMode = nightMode

        if (isNightMode) {
            mBatteryPaint.color = Color.WHITE
            setPageStyle(PageStyle.NIGHT)
        } else {
            mBatteryPaint.color = Color.BLACK
            setPageStyle(mPageStyle!!)
        }
    }

    /**
     * 设置页面样式
     * @param pageStyle
     */
    fun setPageStyle(pageStyle: PageStyle) {
        if (pageStyle != PageStyle.NIGHT) {
            mPageStyle = pageStyle
            ReadSettingConfig.pageStyle = pageStyle
        }

        if (isNightMode && pageStyle !== PageStyle.NIGHT) {
            return
        }

        // 设置当前颜色样式
        mTextColor = ContextCompat.getColor(mContext, pageStyle.fontColor)
        mBgColor = ContextCompat.getColor(mContext, pageStyle.bgColor)

        mTipPaint.color = mTextColor
        mTitlePaint.color = mTextColor
        mTextPaint.color = mTextColor

        mBgPaint.color = mBgColor

        mPageView?.drawCurPage(false)
    }

    /**
     * 跳转到上一章
     */
    fun skipPreChapter(): Boolean {
        if (!hasPrevChapter()) {
            return false
        }

        //载入上一章
        if (parsePrevChapter()) {
            mCurPage = getCurPage(0)
        } else {
            mCurPage = TxtPage()
        }
        mPageView?.drawCurPage(false)
        return true
    }

    /**
     * 跳转到下一章
     */
    fun skipNextChapter(): Boolean {
        if (!hasNextChapter()) {
            return false
        }

        //判断是否达到章节的终止点
        if (parseNextChapter()) {
            mCurPage = getCurPage(0)
        } else {
            mCurPage = TxtPage()
        }
        mPageView?.drawCurPage(false)
        return true
    }


    /**
     * 跳转到指定章节
     *
     * @param pos:从 0 开始。
     */
    fun skipToChapter(pos: Int) {
        //设置参数
        mCurChapterPos = pos
        // 将上一章的缓存设置为null
        mPrePageList = null
        // 如果当前下一章缓存正在执行，则取消
        mPreLoadDisp?.dispose()

        // 将下一章缓存设置为null
        mNextPageList = null

        // 打开指定章节
        openChapter()
    }

    /**
     * 翻到上一页
     *
     * @return
     */
    fun skipToPrePage(): Boolean {
        return mPageView?.autoPrevPage() ?: false
    }

    /**
     * 翻到下一页
     *
     * @return
     */
    fun skipToNextPage(): Boolean {
        return mPageView?.autoNextPage() ?: false
    }

    /**
     * 更新时间
     */
    fun updateTime() {
        if (!mPageView!!.isRunning()) {
            mPageView?.drawCurPage(true)
        }
    }

    /**
     * 更新电量
     *
     * @param level
     */
    fun updateBattery(level: Int) {
        mBatteryLevel = level

        if (!mPageView!!.isRunning()) {
            mPageView?.drawCurPage(true)
        }
    }

    /**
     * 设置提示的文字大小
     *
     * @param textSize:单位为 px。
     */
    fun setTipTextSize(textSize: Int) {
        mTipPaint?.textSize = textSize.toFloat()

        // 如果屏幕大小加载完成
        mPageView?.drawCurPage(false)
    }

    /**
     * 设置文字大小
     *
     * @param textSize
     */
    fun setTextSize(textSize: Int) {
        // 设置文字相关参数
        setUpTextParams(textSize)

        // 设置画笔的字体大小
        mTextPaint.textSize = mTextSize.toFloat()
        // 设置标题的字体大小
        mTitlePaint.textSize = mTitleSize.toFloat()
        // 存储文字大小
        ReadSettingConfig.fontSize = textSize
        // 取消缓存
        mPrePageList = null
        mNextPageList = null

        // 如果当前已经显示数据
        if (isChapterListPrepare && mStatus == STATUS_FINISH) {
            // 重新计算当前页面
            dealLoadPageList(mCurChapterPos)

            // 防止在最后一页，通过修改字体，以至于页面数减少导致崩溃的问题
            if (mCurPage.position >= mCurPageList!!.size) {
                mCurPage.position = mCurPageList!!.size - 1
            }

            // 重新获取指定页面
            mCurPage = mCurPageList!![mCurPage.position]
        }

        mPageView?.drawCurPage(false)
    }

    /**
     * 打开指定章节
     */
    fun openChapter() {
        isFirstOpen = false

        if (!mPageView!!.isPrepare) {
            return
        }

        // 如果章节目录没有准备好
        if (!isChapterListPrepare) {
            mStatus = STATUS_LOADING
            mPageView?.drawCurPage(false)
            return
        }

        // 如果获取到的章节目录为空
        if (mChapterList.isNullOrEmpty()) {
            mStatus = STATUS_CATEGORY_EMPTY
            mPageView?.drawCurPage(false)
            return
        }

        if (parseCurChapter()) {
            // 如果章节从未打开
            if (!isChapterOpen) {
                var position = mBookRecord.pagePos

                // 防止记录页的页号，大于当前最大页号
                if (position >= mCurPageList!!.size) {
                    position = mCurPageList!!.size - 1
                }
                mCurPage = getCurPage(position)
                mCancelPage = mCurPage
                // 切换状态
                isChapterOpen = true
            } else {
                mCurPage = getCurPage(0)
            }
        } else {
            mCurPage = TxtPage()
        }

        mPageView?.drawCurPage(false)
    }

    fun chapterError() {
        //加载错误
        mStatus = STATUS_ERROR
        mPageView?.drawCurPage(false)
    }

    /**
     * 设置页面切换监听
     *
     * @param listener
     */
    fun setOnPageChangeListener(listener: OnPageChangeListener) {
        mPageChangeListener = listener

        // 如果目录加载完之后才设置监听器，那么会默认回调
        if (isChapterListPrepare) {
            mPageChangeListener?.onCategoryFinish(mChapterList!!)
        }
    }

    /**
     * 获取当前页的状态
     *
     * @return
     */
    fun getPageStatus(): Int {
        return mStatus
    }

    /**
     * 获取书籍信息
     *
     * @return
     */
    fun getCollBook(): BookInfoEntity {
        return mCollBook
    }

    /**
     * 获取章节目录。
     *
     * @return
     */
    fun getChapterCategory(): List<TxtChapter>? {
        return mChapterList
    }

    /**
     * 获取当前页的页码
     *
     * @return
     */
    fun getPagePos(): Int {
        return mCurPage.position
    }

    /**
     * 获取当前章节的章节位置
     *
     * @return
     */
    fun getChapterPos(): Int {
        return mCurChapterPos
    }

    /**
     * 获取距离屏幕的高度
     *
     * @return
     */
    fun getMarginHeight(): Int {
        return mMarginHeight
    }

    /**
     * 保存阅读记录
     */
    fun saveRecord() {

        if (mChapterList.isNullOrEmpty()) {
            return
        }

        mBookRecord.bookId = mCollBook.id
        mBookRecord.chapter = mCurChapterPos

        mBookRecord.pagePos = mCurPage.position

        //存储到数据库
        LocalBookRepository.saveBookRecord(mBookRecord)
    }

    /**
     * 初始化书籍
     */
    private fun prepareBook() {

        mBookRecord = LocalBookRepository.getBookRecord(mCollBook.id) ?: BookRecordEntity()

        mCurChapterPos = mBookRecord.chapter
        mLastChapterPos = mCurChapterPos
    }

    fun closeBook() {
        isChapterListPrepare = false
        isClose = true

        mPreLoadDisp?.dispose()

        clearList(mChapterList)
        clearList(mCurPageList)
        clearList(mNextPageList)

        mChapterList = null
        mCurPageList = null
        mNextPageList = null
        mPageView = null
//        mCurPage = null
    }

    /***********************************default method***********************************************/
    private fun hasPrevChapter(): Boolean {
        //判断是否上一章节为空
        return mCurChapterPos - 1 >= 0
    }

    private fun parsePrevChapter(): Boolean {
        // 加载上一章数据
        val preChapter = mCurChapterPos - 1
        mLastChapterPos = mCurChapterPos
        mCurChapterPos = preChapter

        // 当前章缓存为下一章
        mNextPageList = mCurPageList

        if (mPrePageList != null) {
            mCurPageList = mPrePageList
            mPrePageList = null
            // 回调
            chapterChangeCallback()
        }else {
            dealLoadPageList(preChapter)
        }
        return mCurPageList != null
    }

    private fun getCurPage(pos: Int): TxtPage {
        mPageChangeListener?.onPageChange(pos)
        return mCurPageList!![pos]
    }

    private fun hasNextChapter(): Boolean {
        // 判断是否到达目录最后一章
        return mCurChapterPos + 1 < mChapterList?.size ?: 0
    }

    private fun parseNextChapter(): Boolean {
        val nextChapter = mCurChapterPos + 1
        mLastChapterPos = mCurChapterPos
        mCurChapterPos = nextChapter

        // 将当前章的页面列表，作为上一章缓存
        mPrePageList = mCurPageList

        // 是否下一章数据已经预加载了
        if (mNextPageList != null) {
            mCurPageList = mNextPageList
            mNextPageList = null
            // 回调
            chapterChangeCallback()
        } else {
            // 处理页面解析
            dealLoadPageList(nextChapter)
        }
        // 预加载下一页面
        preLoadNextChapter()
        return mCurPageList != null
    }

    private fun chapterChangeCallback() {
        mPageChangeListener?.run {
            onChapterChange(mCurChapterPos)
            onPageCountChange(mCurPageList?.size ?: 0)
        }
    }

    private fun preLoadNextChapter() {
        val nextChapter = mCurChapterPos + 1
        // 如果不存在下一章，且下一章没有数据，则不进行加载。
        if (!hasNextChapter() || !hasChapterData(mChapterList!![nextChapter])) {
            return
        }
        //如果之前正在加载则取消
        mPreLoadDisp?.dispose()
        mPreLoadDisp = Observable.create(ObservableOnSubscribe<List<TxtPage>> { emitter ->
            val txtPages = loadPageList(nextChapter)
            if (txtPages != null) {
                emitter.onNext(txtPages)
            }else {
                emitter.onError(Throwable("内容加载失败"))
            }
            emitter.onComplete()
        }).compose(RxScheduler.rxScheduler()).subscribe({
            mNextPageList = it as MutableList<TxtPage>?
        }, {
            Log.e("benyq", "preLoadNextChapter ${it.message}")
        })
    }

    private fun setUpTextParams(textSize: Int) {
        // 文字大小
        mTextSize = textSize
        mTitleSize = mTextSize + SizeUtils.dp2px(EXTRA_TITLE_SIZE)
        // 行间距(大小为字体的一半)
        mTextInterval = mTextSize / 2
        mTitleInterval = mTitleSize / 2
        // 段落间距(大小为字体的高度)
        mTextPara = mTextSize
        mTitlePara = mTitleSize
    }


    /**
     * 加载页面列表
     *
     * @param chapterPos:章节序号
     * @return
     */
    @Throws(Exception::class)
    private fun loadPageList(chapterPos: Int): MutableList<TxtPage>? {
        // 获取章节
        val chapter = mChapterList!![chapterPos]
        // 判断章节是否存在
        if (!hasChapterData(chapter)) {
            return null
        }
        // 获取章节的文本流
        val reader = getChapterReader(chapter)

        return loadPages(chapter, reader)
    }

    private fun dealLoadPageList(chapterPos: Int) {
        try {
            mCurPageList = loadPageList(chapterPos)
            if (mCurPageList != null) {
                if (mCurPageList.isNullOrEmpty()) {
                    mStatus = STATUS_EMPTY

                    // 添加一个空数据
                    val page = TxtPage()
                    page.lines = listOf()
                    mCurPageList!!.add(page)
                } else {
                    mStatus = STATUS_FINISH
                }
            } else {
                mStatus = STATUS_LOADING
            }
        } catch (e: Exception) {
            e.printStackTrace()

            mCurPageList = null
            mStatus = STATUS_ERROR
        }


        // 回调
        chapterChangeCallback()
    }

    private fun parseCurChapter(): Boolean {
        // 解析数据
        dealLoadPageList(mCurChapterPos)
        // 预加载下一页面
        preLoadNextChapter()
        return mCurPageList != null
    }

    /**
     * 将章节数据，解析成页面列表
     *
     * @param chapter：章节信息
     * @param br：章节的文本流
     * @return
     */
    private fun loadPages(chapter: TxtChapter, br: BufferedReader): MutableList<TxtPage> {
        //生成的页面
        val pages = ArrayList<TxtPage>()
        //使用流的方式加载
        val lines = ArrayList<String>()
        var rHeight = mVisibleHeight
        var titleLinesCount = 0
        var showTitle = true // 是否展示标题
        var paragraph = chapter.title//默认展示标题
        try {
            while (showTitle || br.readLine().also { paragraph = it } != null) {
//                paragraph = StringUtils.convertCC(paragraph, mContext)
                // 重置段落
                if (!showTitle) {
                    paragraph = paragraph.replace("\\s".toRegex(), "")
                    // 如果只有换行符，那么就不执行
                    if (paragraph == "") continue
                    paragraph = StringUtils.halfToFull("  $paragraph\n")
                } else {
                    //设置 title 的顶部间距
                    rHeight -= mTitlePara
                }
                var wordCount: Int
                var subStr: String?
                while (paragraph.isNotEmpty()) {
                    //当前空间，是否容得下一行文字
                    if (showTitle) {
                        rHeight -= mTitlePaint.textSize.toInt()
                    } else {
                        rHeight -= mTextPaint.textSize.toInt()
                    }
                    // 一页已经填充满了，创建 TextPage
                    if (rHeight <= 0) {
                        // 创建Page
                        val page = TxtPage()
                        page.position = pages.size
//                        page.title = StringUtils.convertCC(chapter.getTitle(), mContext)
                        page.title = chapter.title
                        page.lines = ArrayList(lines)
                        page.titleLines = titleLinesCount
                        pages.add(page)
                        // 重置Lines
                        lines.clear()
                        rHeight = mVisibleHeight
                        titleLinesCount = 0

                        continue
                    }

                    //测量一行占用的字节数
                    if (showTitle) {
                        wordCount = mTitlePaint.breakText(
                            paragraph,
                            true, mVisibleWidth.toFloat(), null
                        )
                    } else {
                        wordCount = mTextPaint.breakText(
                            paragraph,
                            true, mVisibleWidth.toFloat(), null
                        )
                    }

                    subStr = paragraph.substring(0, wordCount)
                    if (subStr != "\n") {
                        //将一行字节，存储到lines中
                        lines.add(subStr)

                        //设置段落间距
                        if (showTitle) {
                            titleLinesCount += 1
                            rHeight -= mTitleInterval
                        } else {
                            rHeight -= mTextInterval
                        }
                    }
                    //裁剪
                    paragraph = paragraph.substring(wordCount)
                }

                //增加段落的间距
                if (!showTitle && lines.size != 0) {
                    rHeight = rHeight - mTextPara + mTextInterval
                }

                if (showTitle) {
                    rHeight = rHeight - mTitlePara + mTitleInterval
                    showTitle = false
                }
            }

            if (lines.size != 0) {
                //创建Page
                val page = TxtPage()
                page.position = pages.size
//                page.title = StringUtils.convertCC(chapter.getTitle(), mContext)
                page.title = chapter.title
                page.lines = ArrayList(lines)
                page.titleLines = titleLinesCount
                pages.add(page)
                //重置Lines
                lines.clear()
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            ioClose(br)
        }
        return pages
    }

    private fun clearList(list: MutableList<*>?) {
        list?.clear()
    }

    fun prev(): Boolean {
        // 以下情况禁止翻页
        if (!canTurnPage()) {
            return false
        }

        if (mStatus == STATUS_FINISH) {
            // 先查看是否存在上一页
            val prevPage = getPrevPage()
            if (prevPage != null) {
                mCancelPage = mCurPage
                mCurPage = prevPage
                mPageView?.drawNextPage()
                return true
            }
        }

        if (!hasPrevChapter()) {
            return false
        }

        mCancelPage = mCurPage
        if (parsePrevChapter()) {
            mCurPage = getPrevLastPage()
        } else {
            mCurPage = TxtPage()
        }
        mPageView?.drawNextPage()
        return true

    }
    fun next(): Boolean {
        // 以下情况禁止翻页
        if (!canTurnPage()) {
            return false
        }

        if (mStatus == STATUS_FINISH) {
            // 先查看是否存在下一页
            val nextPage = getNextPage()
            if (nextPage != null) {
                mCancelPage = mCurPage
                mCurPage = nextPage
                mPageView?.drawNextPage()
                return true
            }
        }

        if (!hasNextChapter()) {
            return false
        }

        mCancelPage = mCurPage
        // 解析下一章数据
        if (parseNextChapter()) {
            mCurPage = mCurPageList!![0]
        } else {
            mCurPage = TxtPage()
        }
        mPageView?.drawNextPage()
        return true
    }

    fun pageCancel() {
        if (mCurPage.position == 0 && mCurChapterPos > mLastChapterPos) { // 加载到下一章取消了
            if (mPrePageList != null) {
                cancelNextChapter()
            } else {
                if (parsePrevChapter()) {
                    mCurPage = getPrevLastPage()
                } else {
                    mCurPage = TxtPage()
                }
            }
        } else if (mCurPageList == null || mCurPage.position == mCurPageList!!.size - 1 && mCurChapterPos < mLastChapterPos) {  // 加载上一章取消了

            if (mNextPageList != null) {
                cancelPreChapter()
            } else {
                if (parseNextChapter()) {
                    mCurPage = mCurPageList!![0]
                } else {
                    mCurPage = TxtPage()
                }
            }
        } else {
            // 假设加载到下一页，又取消了。那么需要重新装载。
            mCurPage = mCancelPage!!
        }
    }

    private fun cancelNextChapter() {
        val temp = mLastChapterPos
        mLastChapterPos = mCurChapterPos
        mCurChapterPos = temp

        mNextPageList = mCurPageList
        mCurPageList = mPrePageList
        mPrePageList = null

        chapterChangeCallback()

        mCurPage = getPrevLastPage()
        mCancelPage = null
    }

    private fun cancelPreChapter() {
        // 重置位置点
        val temp = mLastChapterPos
        mLastChapterPos = mCurChapterPos
        mCurChapterPos = temp
        // 重置页面列表
        mPrePageList = mCurPageList
        mCurPageList = mNextPageList
        mNextPageList = null

        chapterChangeCallback()

        mCurPage = getCurPage(0)
        mCancelPage = null
    }

    /**
     * 根据当前状态，决定是否能够翻页
     *
     * @return
     */
    private fun canTurnPage(): Boolean {

        if (!isChapterListPrepare) {
            return false
        }

        if (mStatus == STATUS_PARSE_ERROR || mStatus == STATUS_PARING) {
            return false
        } else if (mStatus == STATUS_ERROR) {
            mStatus = STATUS_LOADING
        }
        return true
    }

    /**
     * @return:获取下一的页面
     */
    private fun getNextPage(): TxtPage? {
        val pos = mCurPage.position + 1
        if (pos >= mCurPageList?.size ?: 0) {
            return null
        }
        mPageChangeListener?.onPageChange(pos)
        return mCurPageList!![pos]
    }

    /**
     * @return:获取上一个页面
     */
    private fun getPrevPage(): TxtPage? {
        val pos = mCurPage.position - 1
        if (pos < 0) {
            return null
        }
        mPageChangeListener?.onPageChange(pos)
        return mCurPageList?.get(pos)
    }

    /**
     * @return:获取上一个章节的最后一页
     */
    private fun getPrevLastPage(): TxtPage {
        val pos = mCurPageList!!.size - 1

        mPageChangeListener?.onPageChange(pos)

        return mCurPageList!![pos]
    }


    fun prepareDisplay(w: Int, h: Int) {
        // 获取PageView的宽高
        mDisplayWidth = w
        mDisplayHeight = h

        // 获取内容显示位置的大小
        mVisibleWidth = mDisplayWidth - mMarginWidth * 2
        mVisibleHeight = mDisplayHeight - mMarginHeight * 2

        // 重置 PageMode
        mPageView?.setPageMode(mPageMode!!)

        if (!isChapterOpen) {
            // 展示加载界面
            mPageView?.drawCurPage(false)
            // 如果在 display 之前调用过 openChapter 肯定是无法打开的。
            // 所以需要通过 display 再重新调用一次。
            if (!isFirstOpen) {
                // 打开书籍
                openChapter()
            }
        } else {
            // 如果章节已显示，那么就重新计算页面
            if (mStatus == STATUS_FINISH) {
                dealLoadPageList(mCurChapterPos)
                // 重新设置文章指针的位置
                mCurPage = getCurPage(mCurPage.position)
            }
            mPageView?.drawCurPage(false)
        }
    }

    fun drawPage(nextBitmap: Bitmap, isUpdate: Boolean) {
        drawBackground(mPageView!!.getBgBitmap(), isUpdate)
        if (isUpdate) {
            drawContent(nextBitmap)
        }
        //更新绘制
        mPageView?.invalidate()
    }

    private fun drawBackground(bitmap: Bitmap, isUpdate: Boolean) {
        val canvas = Canvas(bitmap)
        val tipMarginHeight = SizeUtils.dp2px(3f)
        if (!isUpdate) {
            /****绘制背景 */
            canvas.drawColor(mBgColor)

            if (!mChapterList.isNullOrEmpty()) {
                /*****初始化标题的参数 */
                //需要注意的是:绘制text的y的起始点是text的基准线的位置，而不是从text的头部的位置
                val tipTop = tipMarginHeight - mTipPaint.fontMetrics.top
                //根据状态不一样，数据不一样
                if (mStatus != STATUS_FINISH) {
                    if (isChapterListPrepare) {
                        canvas.drawText(
                            mChapterList!![mCurChapterPos].title,
                            mMarginWidth.toFloat(),
                            tipTop,
                            mTipPaint
                        )
                    }
                } else {
                    canvas.drawText(mCurPage.title, mMarginWidth.toFloat(), tipTop, mTipPaint)
                }

                /******绘制页码 */
                // 底部的字显示的位置Y
                val y =
                    mDisplayHeight.toFloat() - mTipPaint.fontMetrics.bottom - tipMarginHeight.toFloat()
                // 只有finish的时候采用页码
                if (mStatus == STATUS_FINISH) {
                    val percent = "${mCurPage.position + 1}/${mCurPageList?.size}"
                    canvas.drawText(percent, mMarginWidth.toFloat(), y, mTipPaint)
                }
            }
        } else {
            //擦除区域
            mBgPaint.color = mBgColor
            canvas.drawRect(
                (mDisplayWidth / 2).toFloat(),
                (mDisplayHeight - mMarginHeight + SizeUtils.dp2px(2f)).toFloat(),
                mDisplayWidth.toFloat(),
                mDisplayHeight.toFloat(),
                mBgPaint
            )
        }

        /******绘制电池 */

        val visibleRight = mDisplayWidth - mMarginWidth
        val visibleBottom = mDisplayHeight - tipMarginHeight

        val outFrameWidth = mTipPaint.measureText("xxx").toInt()
        val outFrameHeight = mTipPaint.getTextSize().toInt()

        val polarHeight = SizeUtils.dp2px(6f)
        val polarWidth = SizeUtils.dp2px(2f)
        val border = 1
        val innerMargin = 1

        //电极的制作
        val polarLeft = visibleRight - polarWidth
        val polarTop = visibleBottom - (outFrameHeight + polarHeight) / 2
        val polar = Rect(
            polarLeft, polarTop, visibleRight,
            polarTop + polarHeight - SizeUtils.dp2px(2f)
        )

        mBatteryPaint.style = Paint.Style.FILL
        canvas.drawRect(polar, mBatteryPaint)

        //外框的制作
        val outFrameLeft = polarLeft - outFrameWidth
        val outFrameTop = visibleBottom - outFrameHeight
        val outFrameBottom = visibleBottom - SizeUtils.dp2px(2f)
        val outFrame = Rect(outFrameLeft, outFrameTop, polarLeft, outFrameBottom)

        mBatteryPaint.style = Paint.Style.STROKE
        mBatteryPaint.strokeWidth = border.toFloat()
        canvas.drawRect(outFrame, mBatteryPaint)

        //内框的制作
        val innerWidth = (outFrame.width() - innerMargin * 2 - border) * (mBatteryLevel / 100.0f)
        val innerFrame = RectF(
            (outFrameLeft + border + innerMargin).toFloat(),
            (outFrameTop + border + innerMargin).toFloat(),
            outFrameLeft.toFloat() + border.toFloat() + innerMargin.toFloat() + innerWidth,
            (outFrameBottom - border - innerMargin).toFloat()
        )

        mBatteryPaint.style = Paint.Style.FILL
        canvas.drawRect(innerFrame, mBatteryPaint)

        /******绘制当前时间 */
        //底部的字显示的位置Y
        val y =
            mDisplayHeight.toFloat() - mTipPaint.fontMetrics.bottom - tipMarginHeight.toFloat()
        val time = date2String(Date(), "HH:mm")
        val x = outFrameLeft - mTipPaint.measureText(time) - SizeUtils.dp2px(4f)
        canvas.drawText(time, x, y, mTipPaint)
    }

    private fun drawContent(bitmap: Bitmap) {
        val canvas = Canvas(bitmap)

        if (mPageMode === PageMode.SCROLL) {
            canvas.drawColor(mBgColor)
        }
        /******绘制内容 */

        if (mStatus != STATUS_FINISH) {
            //绘制字体
            var tip = ""
            when (mStatus) {
                STATUS_LOADING -> tip = "正在拼命加载中..."
                STATUS_ERROR -> tip = "加载失败(点击边缘重试)"
                STATUS_EMPTY -> tip = "文章内容为空"
                STATUS_PARING -> tip = "正在排版请等待..."
                STATUS_PARSE_ERROR -> tip = "文件解析错误"
                STATUS_CATEGORY_EMPTY -> tip = "目录列表为空"
            }

            //将提示语句放到正中间
            val fontMetrics = mTextPaint.fontMetrics
            val textHeight = fontMetrics.top - fontMetrics.bottom
            val textWidth = mTextPaint.measureText(tip)
            val pivotX = (mDisplayWidth - textWidth) / 2
            val pivotY = (mDisplayHeight - textHeight) / 2
            canvas.drawText(tip, pivotX, pivotY, mTextPaint)
        } else {
            var top: Float

            if (mPageMode === PageMode.SCROLL) {
                top = -mTextPaint.fontMetrics.top
            } else {
                top = mMarginHeight - mTextPaint.fontMetrics.top
            }

            //设置总距离
            val interval = mTextInterval + mTextPaint.textSize.toInt()
            val para = mTextPara + mTextPaint.textSize.toInt()
            val titleInterval = mTitleInterval + mTitlePaint.textSize.toInt()
            val titlePara = mTitlePara + mTextPaint.textSize.toInt()
            var str: String? = null

            //对标题进行绘制
            for (i in 0 until mCurPage.titleLines) {
                str = mCurPage.lines[i]

                //设置顶部间距
                if (i == 0) {
                    top += mTitlePara.toFloat()
                }

                //计算文字显示的起始点
                val start = (mDisplayWidth - mTitlePaint.measureText(str)).toInt() / 2
                //进行绘制
                canvas.drawText(str, start.toFloat(), top, mTitlePaint)

                //设置尾部间距
                if (i == mCurPage.titleLines - 1) {
                    top += titlePara.toFloat()
                } else {
                    //行间距
                    top += titleInterval.toFloat()
                }
            }

            //对内容进行绘制
            for (i in mCurPage.titleLines until mCurPage.lines.size) {
                str = mCurPage.lines[i]

                canvas.drawText(str, mMarginWidth.toFloat(), top, mTextPaint)
                if (str.endsWith("\n")) {
                    top += para.toFloat()
                } else {
                    top += interval.toFloat()
                }
            }
        }
    }

    /*******************************abstract method***************************************/

    /**
     * 刷新章节列表
     */
    abstract fun refreshChapterList()

    /**
     * 获取章节的文本流
     *
     * @param chapter
     * @return
     */
    @Throws(Exception::class)
    protected abstract fun getChapterReader(chapter: TxtChapter): BufferedReader

    /**
     * 章节数据是否存在
     *
     * @return
     */
    protected abstract fun hasChapterData(chapter: TxtChapter): Boolean

    /*****************************************interface */

    interface OnPageChangeListener {
        /**
         * 作用：章节切换的时候进行回调
         *
         * @param pos:切换章节的序号
         */
        fun onChapterChange(pos: Int)

        /**
         * 作用：请求加载章节内容
         *
         * @param requestChapters:需要下载的章节列表
         */
        fun requestChapters(requestChapters: List<TxtChapter>)

        /**
         * 作用：章节目录加载完成时候回调
         *
         * @param chapters：返回章节目录
         */
        fun onCategoryFinish(chapters: List<TxtChapter>)

        /**
         * 作用：章节页码数量改变之后的回调。==> 字体大小的调整，或者是否关闭虚拟按钮功能都会改变页面的数量。
         *
         * @param count:页面的数量
         */
        fun onPageCountChange(count: Int)

        /**
         * 作用：当页面改变的时候回调
         *
         * @param pos:当前的页面的序号
         */
        fun onPageChange(pos: Int)
    }
}