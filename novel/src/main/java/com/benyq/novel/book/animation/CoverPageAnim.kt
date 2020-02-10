package com.benyq.novel.book.animation

import android.graphics.Canvas
import android.view.View

/**
 * @author benyq
 * @emil 1520063035@qq.com
 * create at 2020/2/6
 * description:
 */
class CoverPageAnim(w: Int, h: Int, view: View, listener: OnPageChangeListener): HorizonPageAnim(w, h, view, listener) {

    override fun drawStatic(canvas: Canvas) {

    }

    override fun drawMove(canvas: Canvas) {
    }
}