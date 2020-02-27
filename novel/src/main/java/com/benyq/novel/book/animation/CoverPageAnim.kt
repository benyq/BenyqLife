package com.benyq.novel.book.animation

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.view.View
import com.benyq.common.ext.loge
import kotlin.math.abs

/**
 * @author benyq
 * @emil 1520063035@qq.com
 * create at 2020/2/6
 * description:
 */
class CoverPageAnim(w: Int, h: Int, view: View, listener: OnPageChangeListener): HorizonPageAnim(w, h, view, listener) {

    private var mSrcRect: Rect = Rect(0, 0, mViewWidth, mViewHeight)
    private var mDestRect:android.graphics.Rect = Rect(0, 0, mViewWidth, mViewHeight)
    private lateinit var mBackShadowDrawableLR: GradientDrawable

    init {
        val mBackShadowColors = intArrayOf(0x66000000, 0x00000000)
        mBackShadowDrawableLR = GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT, mBackShadowColors
        )
        mBackShadowDrawableLR.gradientType = GradientDrawable.LINEAR_GRADIENT
    }

    override fun drawStatic(canvas: Canvas) {
        if (isCancel) {
            mNextBitmap = mCurBitmap.copy(Bitmap.Config.RGB_565, true)
            canvas.drawBitmap(mCurBitmap, 0f, 0f, null)
        } else {
            canvas.drawBitmap(mNextBitmap, 0f, 0f, null)
        }
    }

    override fun drawMove(canvas: Canvas) {
        when (mDirection) {
            Direction.NEXT -> {
                var dis = (mViewWidth - mStartX + mTouchX).toInt()
                if (dis > mViewWidth) {
                    dis = mViewWidth
                }
                //计算bitmap截取的区域
                mSrcRect.left = mViewWidth - dis
                //计算bitmap在canvas显示的区域
                mDestRect.right = dis
                canvas.drawBitmap(mNextBitmap, 0f, 0f, null)
                canvas.drawBitmap(mCurBitmap, mSrcRect, mDestRect, null)
                addShadow(dis, canvas)

            }
            else -> {
                mSrcRect.left = (mViewWidth - mTouchX).toInt()
                mDestRect.right = mTouchX.toInt()
                canvas.drawBitmap(mCurBitmap, 0f, 0f, null)
                canvas.drawBitmap(mNextBitmap, mSrcRect, mDestRect, null)
                addShadow(mTouchX.toInt(), canvas)
            }
        }
    }

    //添加阴影
    private fun addShadow(left: Int, canvas: Canvas?) {
        mBackShadowDrawableLR.setBounds(left, 0, left + 30, mScreenHeight)
        mBackShadowDrawableLR.draw(canvas!!)
    }

    override fun startAnim() {
        super.startAnim()
        var dx = 0
        when (mDirection) {
            Direction.NEXT -> if (isCancel) {
                var dis = (mViewWidth - mStartX + mTouchX).toInt()
                if (dis > mViewWidth) {
                    dis = mViewWidth
                }
                dx = mViewWidth - dis
            } else {
                dx = (-(mTouchX + (mViewWidth - mStartX))).toInt()
            }
            else -> dx = if (isCancel) {
                (-mTouchX).toInt()
            } else {
                (mViewWidth - mTouchX).toInt()
            }
        }
        //滑动速度保持一致
        val duration: Int = 400 * abs(dx) / mViewWidth
        loge("mTouchX $mTouchX dx $dx duration $duration")
        mScroller.startScroll(mTouchX.toInt(), 0, dx, 0, duration)
    }
}