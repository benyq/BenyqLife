package com.benyq.novel.adapter

import android.widget.ImageView
import com.benyq.novel.R
import com.benyq.novel.bean.SearchBookBean
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

/**
 * @author benyq
 * @emil 1520063035@qq.com
 * create at 2020/1/22
 * description:
 */
class SearchResultAdapter : BaseQuickAdapter<SearchBookBean, BaseViewHolder>(R.layout.novel_item_search_result){

    override fun convert(helper: BaseViewHolder, item: SearchBookBean?) {
        item?.run {
            helper.setText(R.id.tvBookName, bookName)
                .setText(R.id.tvBookAuthor, bookAuthor)
                .setText(R.id.tvBookIntroduction, bookIntroduction)
                .setText(R.id.tvBookCategory, bookCategory)
                .setText(R.id.tvUpdateState, if (updateState == 0) "连载" else "完结")

            val ivBookCover = helper.getView<ImageView>(R.id.ivBookCover)
            Glide.with(mContext).load(bookCover).into(ivBookCover)
        }
    }
}