package com.benyq.novel.book.animation

import android.graphics.Canvas
import android.graphics.Rect
import android.view.View

/**
 *
 * @author benyq
 * @time 2020/2/26
 * @e-mail 1520063035@qq.com
 * @note
 */
class SlidePageAnim(w: Int, h: Int, view: View, listener: OnPageChangeListener): HorizonPageAnim(w, h, view, listener){

    private val mSrcRect: Rect = Rect(0, 0, mViewWidth, mViewHeight)
    private  var mDestRect:android.graphics.Rect = Rect(0, 0, mViewWidth, mViewHeight)
    private  var mNextSrcRect:android.graphics.Rect = Rect(0, 0, mViewWidth, mViewHeight)
    private  var mNextDestRect:android.graphics.Rect =
        Rect(0, 0, mViewWidth, mViewHeight)

    override fun drawStatic(canvas: Canvas) {
        if (isCancel) {
            canvas.drawBitmap(mCurBitmap, 0f, 0f, null)
        } else {
            canvas.drawBitmap(mNextBitmap, 0f, 0f, null)
        }
    }

    override fun drawMove(canvas: Canvas) {
        var dis = 0
        when (mDirection) {
            Direction.NEXT -> {
                //左半边的剩余区域
                dis = (mScreenWidth - mStartX + mTouchX).toInt()
                if (dis > mScreenWidth) {
                    dis = mScreenWidth
                }
                //计算bitmap截取的区域
                mSrcRect.left = mScreenWidth - dis
                //计算bitmap在canvas显示的区域
                mDestRect.right = dis
                //计算下一页截取的区域
                mNextSrcRect.right = mScreenWidth - dis
                //计算下一页在canvas显示的区域
                mNextDestRect.left = dis
                canvas.drawBitmap(mNextBitmap, mNextSrcRect, mNextDestRect, null)
                canvas.drawBitmap(mCurBitmap, mSrcRect, mDestRect, null)
            }
            else -> {
                dis = (mTouchX - mStartX).toInt()
                if (dis < 0) {
                    dis = 0
                    mStartX = mTouchX
                }
                mSrcRect.left = mScreenWidth - dis
                mDestRect.right = dis
                //计算下一页截取的区域
                mNextSrcRect.right = mScreenWidth - dis
                //计算下一页在canvas显示的区域
                mNextDestRect.left = dis
                canvas.drawBitmap(mCurBitmap, mNextSrcRect, mNextDestRect, null)
                canvas.drawBitmap(mNextBitmap, mSrcRect, mDestRect, null)
            }
        }
    }

    override fun startAnim() {
        super.startAnim()
        var dx = 0
        when (mDirection) {
            Direction.NEXT -> if (isCancel) {
                var dis = (mScreenWidth - mStartX + mTouchX).toInt()
                if (dis > mScreenWidth) {
                    dis = mScreenWidth
                }
                dx = mScreenWidth - dis
            } else {
                dx = (-(mTouchX + (mScreenWidth - mStartX))).toInt()
            }
            else -> dx = if (isCancel) {
                (-Math.abs(mTouchX - mStartX)).toInt()
            } else {
                (mScreenWidth - (mTouchX - mStartX)).toInt()
            }
        }
        //滑动速度保持一致
        val duration = 400 * Math.abs(dx) / mScreenWidth
        mScroller.startScroll(mTouchX.toInt(), 0, dx, 0, duration)
    }
}