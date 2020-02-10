package com.benyq.novel.adapter

import com.benyq.novel.R
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

/**
 * @author benyq
 * @emil 1520063035@qq.com
 * create at 2020/1/21
 * description:  热门书籍
 */
class HotBookAdapter : BaseQuickAdapter<String, BaseViewHolder>(R.layout.novel_item_hot_book){

    override fun convert(helper: BaseViewHolder?, item: String?) {
        helper?.setText(R.id.tvHotBook, item)
    }
}