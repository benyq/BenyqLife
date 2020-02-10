package com.benyq.novel.book.page

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import com.benyq.novel.book.animation.*
import com.benyq.novel.local.database.enity.BookInfoEntity

/**
 * @author benyq
 * @emil 1520063035@qq.com
 * create at 2020/2/6
 * description:
 */
class PageView(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) :
    View(context, attributeSet, defStyleAttr) {

    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)
    constructor(context: Context) : this(context, null)

    private var mViewWidth = 0 // 当前View的宽
    private var mViewHeight = 0 // 当前View的高

    private var mStartX = 0
    private var mStartY = 0
    private var isMove = false
    // 初始化参数
    private var mBgColor = -0x313d64
    private var mPageMode = PageMode.NONE
    // 是否允许点击
    private var canTouch = true
    // 唤醒菜单的区域
    private var mCenterRect: RectF? = null
    var isPrepare: Boolean = false
    // 动画类
    private var mPageAnim: PageAnimation? = null

    // 动画监听类
    private val mPageAnimListener = object : PageAnimation.OnPageChangeListener {
        override fun hasPrev(): Boolean {
            return this@PageView.hasPrevPage()
        }

        override fun hasNext(): Boolean {
            return this@PageView.hasNextPage()
        }

        override fun pageCancel() {
            this@PageView.pageCancel()
        }
    }

    //点击监听
    private lateinit var mTouchListener: PageTouchListener
    //内容加载器
    private var mPageLoader: PageLoader? = null

    /**
     * 判断是否存在上一页
     *
     * @return
     */
    private fun hasPrevPage(): Boolean {
        mTouchListener.prePage()
        return mPageLoader?.prev() ?: false
    }

    /**
     * 判断是否下一页存在
     *
     * @return
     */
    private fun hasNextPage(): Boolean {
        mTouchListener.nextPage()
        return mPageLoader?.next() ?: false
    }

    private fun pageCancel() {
        mTouchListener.cancel()
        mPageLoader?.pageCancel()
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mViewWidth = w
        mViewHeight = h
        isPrepare = true
        mPageLoader?.prepareDisplay(w, h)
    }


    fun setPageMode(pageMode: PageMode) {
        mPageMode = pageMode
        //视图未初始化的时候，禁止调用
        if (mViewWidth == 0 || mViewHeight == 0) return
        when (mPageMode) {
            PageMode.NONE -> mPageAnim =
                NonePageAnim(mViewWidth, mViewHeight, this, mPageAnimListener)
            PageMode.COVER -> mPageAnim =
                CoverPageAnim(mViewWidth, mViewHeight, this, mPageAnimListener)
            else -> NonePageAnim(mViewWidth, mViewHeight, this, mPageAnimListener)

        }
    }

    fun getNextBitmap(): Bitmap {
        return mPageAnim!!.getNextBitmap()
    }

    fun getBgBitmap(): Bitmap {
        return mPageAnim!!.getBgBitmap()
    }

    private fun startPageAnim(direction: PageAnimation.Direction) {
        mTouchListener.run {
            //取消动画
            abortAnimation()
            if (direction == PageAnimation.Direction.NEXT) {
                val x = mViewWidth
                val y = mViewHeight
                //初始化动画
                mPageAnim?.setStartPoint(x.toFloat(), y.toFloat())
                //设置点击点
                mPageAnim?.setTouchPoint(x.toFloat(), y.toFloat())
                //设置方向
                val hasNext = hasNextPage()
                mPageAnim?.setDirection(direction)
                if (!hasNext) {
                    return
                }
            } else {
                val x = 0
                val y = mViewHeight
                //初始化动画
                mPageAnim?.setStartPoint(x.toFloat(), y.toFloat())
                //设置点击点
                mPageAnim?.setTouchPoint(x.toFloat(), y.toFloat())
                mPageAnim?.setDirection(direction)
                //设置方向方向
                val hashPrev = hasPrevPage()
                if (!hashPrev) {
                    return
                }

            }
            mPageAnim?.startAnim()
            postInvalidate()
        }
    }

    //背景颜色
    fun setBgColor(color: Int) {
        mBgColor = color
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //绘制背景
        canvas.drawColor(mBgColor)

        //绘制动画
        mPageAnim?.draw(canvas)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event)
        if (!canTouch && event.action != MotionEvent.ACTION_DOWN) return true
        val x = event.x.toInt()
        val y = event.y.toInt()

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mStartX = x
                mStartY = y
                isMove = false
                canTouch = mTouchListener.onTouch()
                mPageAnim?.onTouchEvent(event)
            }
            MotionEvent.ACTION_MOVE -> {
                // 判断是否大于最小滑动值。
                val slop = ViewConfiguration.get(context).scaledTouchSlop
                if (!isMove) {
                    isMove =
                        Math.abs(mStartX - event.x) > slop || Math.abs(mStartY - event.y) > slop
                }

                // 如果滑动了，则进行翻页。
                if (isMove) {
                    mPageAnim?.onTouchEvent(event)
                }
            }
            MotionEvent.ACTION_UP -> {
                if (!isMove) {
                    //设置中间区域范围
                    if (mCenterRect == null) {
                        mCenterRect = RectF(
                            (mViewWidth / 5).toFloat(), (mViewHeight / 3).toFloat(),
                            (mViewWidth * 4 / 5).toFloat(), (mViewHeight * 2 / 3).toFloat()
                        )
                    }

                    //是否点击了中间
                    if (mCenterRect!!.contains(x.toFloat(), y.toFloat())) {
                        mTouchListener.center()
                        return true
                    }
                }
                mPageAnim?.onTouchEvent(event)
            }
        }
        return true

    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mPageAnim?.abortAnim()
        mPageAnim?.clear()

        mPageLoader = null
        mPageAnim = null
    }

    //如果滑动状态没有停止就取消状态，重新设置Anim的触碰点
    fun abortAnimation() {
        mPageAnim?.abortAnim()
    }


    fun isRunning(): Boolean {
        return if (mPageAnim == null) {
            false
        } else mPageAnim?.isRunning ?: false
    }

    fun setPageTouchListener(mTouchListener: PageTouchListener) {
        this.mTouchListener = mTouchListener
    }

    fun drawNextPage() {
        if (!isPrepare) return

        if (mPageAnim is HorizonPageAnim) {
            (mPageAnim as HorizonPageAnim).changePage()
        }
        mPageLoader?.drawPage(getNextBitmap(), false)
    }

    /**
     * 绘制当前页。
     *
     * @param isUpdate
     */
    fun drawCurPage(isUpdate: Boolean) {
        if (!isPrepare) return

//        if (!isUpdate) {
//            if (mPageAnim is ScrollPageAnim) {
//                (mPageAnim as ScrollPageAnim).resetBitmap()
//            }
//        }

        mPageLoader?.drawPage(getNextBitmap(), isUpdate)
    }

    fun autoPrevPage(): Boolean {
        //滚动暂时不支持自动翻页
        if (mPageAnim is ScrollPageAnim) {
            return false
        } else {
            startPageAnim(PageAnimation.Direction.PRE)
            return true
        }
    }

    fun autoNextPage(): Boolean {
        if (mPageAnim is ScrollPageAnim) {
            return false
        } else {
            startPageAnim(PageAnimation.Direction.NEXT)
            return true
        }
    }

    fun getPageLoader(bookInfo: BookInfoEntity): PageLoader {
        // 判是否已经存在
        if (mPageLoader != null) {
            return mPageLoader!!
        }
        // 根据书籍类型，获取具体的加载器
        if (bookInfo.isLocal) {
            mPageLoader = LocalPageLoader(this, bookInfo)
        } else {
            mPageLoader = NetPageLoader(this, bookInfo)
        }
        // 判断是否 PageView 已经初始化完成
        if (mViewWidth != 0 || mViewHeight != 0) {
            // 初始化 PageLoader 的屏幕大小
            mPageLoader!!.prepareDisplay(mViewWidth, mViewHeight)
        }

        return mPageLoader!!
    }


    interface PageTouchListener {
        fun onTouch(): Boolean

        fun center()

        fun prePage()

        fun nextPage()

        fun cancel()
    }
}