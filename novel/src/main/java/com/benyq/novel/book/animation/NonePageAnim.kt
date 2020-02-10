package com.benyq.novel.book.animation

import android.graphics.Canvas
import android.view.View

/**
 * @author benyq
 * @emil 1520063035@qq.com
 * create at 2020/2/6
 * description:
 */
class NonePageAnim(w: Int, h: Int, view: View, listener: OnPageChangeListener): HorizonPageAnim(w, h, view, listener) {

    override fun drawMove(canvas: Canvas) {
        if (isCancel) {
            canvas.drawBitmap(mCurBitmap, 0f, 0f, null)
        }else {
            canvas.drawBitmap(mNextBitmap, 0f, 0f, null)
        }
    }

    override fun drawStatic(canvas: Canvas) {
        if (isCancel) {
            canvas.drawBitmap(mCurBitmap, 0f, 0f, null)
        }else {
            canvas.drawBitmap(mNextBitmap, 0f, 0f, null)
        }
    }


    override fun startAnim() {}
}