package com.benyq.novel

import androidx.annotation.IntRange
import com.benyq.common.local.MMKVValue
import com.benyq.novel.book.animation.PageAnimation
import com.benyq.novel.book.page.PageMode
import com.benyq.novel.book.page.PageStyle
import com.tencent.mmkv.MMKV

/**
 * @author benyq
 * @emil 1520063035@qq.com
 * create at 2020/1/12
 * description:
 * 阅读设置保存信息
 */
object ReadSettingConfig {

    var brightness by MMKVValue("novelBrightness", 0)
    //阅读界面亮度随系统
    var autoBrightness by MMKVValue("novelAutoBrightness", false)
    var fontSize by MMKVValue("novelFontSize", 51)
    //是否使用系统字体大小
    var isSystemFontSize by MMKVValue("novelIsSystemFontSize", false)
    var readBackground by MMKVValue("novelReadBackground", 0xff00ff)
    //翻页模式
    var turnPageMode by MMKVValue("novelTurnPageMode", PageMode.NONE)

    var isNightMode by MMKVValue("isNightMode", false)

    var pageStyle by MMKVValue("pageStyle", PageStyle.BG_0)

}
