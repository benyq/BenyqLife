package com.benyq.novel.adapter

import android.graphics.Color
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.benyq.novel.R
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

/**
 * @author benyq
 * @emil 1520063035@qq.com
 * create at 2019/8/20
 * description:
 */
class ReadBgColorAdapter : BaseQuickAdapter<String, BaseViewHolder>(R.layout.novel_item_read_bg_color) {

    var index = 0

    override fun convert(helper: BaseViewHolder, item: String) {
        val cardParent = helper.getView<CardView>(R.id.llContainer)
        cardParent.setCardBackgroundColor(ContextCompat.getColor(mContext,
            if (index == helper.layoutPosition) {
                R.color.novel_light_red
            } else {
                R.color.novel_color9e9e9e
            }
        ))
        val cardChild = helper.getView<CardView>(R.id.viewContent)
        cardChild.setCardBackgroundColor(Color.parseColor(item))
    }
}