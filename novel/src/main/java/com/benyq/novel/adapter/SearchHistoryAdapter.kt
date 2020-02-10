package com.benyq.novel.adapter

import com.benyq.novel.R
import com.benyq.novel.bean.AutoCompleteBean
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

/**
 * @author benyq
 * @emil 1520063035@qq.com
 * create at 2020/1/21
 * description: 搜索历史
 */
class SearchHistoryAdapter :
    BaseQuickAdapter<AutoCompleteBean, BaseViewHolder>(R.layout.novel_item_search_history) {

    override fun convert(helper: BaseViewHolder, item: AutoCompleteBean) {
        item.run {
            helper.setText(R.id.tvSearchKey, searchKey)
                    .setBackgroundRes(
                        R.id.ivSearchType,
                        if (searchType == AutoCompleteBean.TYPE_AUTHOR) R.drawable.novel_ic_author else R.drawable.novel_ic_book
                    )
        }
    }
}