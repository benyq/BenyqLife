package com.benyq.novel.book.animation

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.MotionEvent
import android.view.View

/**
 * @author benyq
 * @emil 1520063035@qq.com
 * create at 2020/2/7
 * description:
 */
class ScrollPageAnim(
    w: Int,
    h: Int,
    marginWidth: Int,
    marginHeight: Int,
    view: View,
    listener: OnPageChangeListener
) : PageAnimation(w, h, marginWidth, marginHeight, view, listener) {


    protected lateinit var mCurBitmap: Bitmap
    protected lateinit var mNextBitmap: Bitmap


    override fun onTouchEvent(event: MotionEvent): Boolean {
        return true
    }

    override fun draw(canvas: Canvas) {
    }

    override fun scrollAnim() {
    }

    override fun abortAnim() {
    }

    override fun getBgBitmap() = mNextBitmap

    override fun getNextBitmap() = mNextBitmap
}