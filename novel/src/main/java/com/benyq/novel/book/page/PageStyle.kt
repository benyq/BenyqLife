package com.benyq.novel.book.page

import com.benyq.novel.R

/**
 * @author benyq
 * @emil 1520063035@qq.com
 * create at 2020/2/7
 * description:
 */
enum class PageStyle(var fontColor: Int, var bgColor: Int) {

    BG_0(R.color.novel_read_font_1, R.color.novel_read_bg_1),
    BG_1(R.color.novel_read_font_2, R.color.novel_read_bg_2),
    BG_2(R.color.novel_read_font_3, R.color.novel_read_bg_3),
    BG_3(R.color.novel_read_font_4, R.color.novel_read_bg_4),
    BG_4(R.color.novel_read_font_5, R.color.novel_read_bg_5),
    NIGHT(R.color.novel_read_font_night, R.color.novel_read_bg_night),;

}