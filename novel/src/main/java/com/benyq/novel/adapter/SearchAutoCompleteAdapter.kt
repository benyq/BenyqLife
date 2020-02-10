package com.benyq.novel.adapter

import com.benyq.novel.R
import com.benyq.novel.bean.AutoCompleteBean
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

/**
 * @author benyq
 * @emil 1520063035@qq.com
 * create at 2020/1/21
 * description:
 */
class SearchAutoCompleteAdapter :
    BaseQuickAdapter<AutoCompleteBean, BaseViewHolder>(R.layout.novel_item_search_auto_complete) {

    override fun convert(helper: BaseViewHolder?, item: AutoCompleteBean?) {
        item?.run {
            helper?.let {
                it.setText(R.id.tvSearchKey, searchKey)
                if (searchType == AutoCompleteBean.TYPE_AUTHOR) {
                    it.setBackgroundRes(R.id.ivSearchType, R.drawable.novel_ic_author)
                    it.setGone(R.id.tvTypeAuthor, false)

                } else {
                    it.setBackgroundRes(R.id.ivSearchType, R.drawable.novel_ic_book)
                    it.setGone(R.id.tvTypeAuthor, true)
                }
            }
        }
    }
}