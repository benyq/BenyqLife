package com.benyq.novel.adapter

import android.widget.ImageView
import com.benyq.novel.R
import com.benyq.novel.local.database.enity.BookInfoEntity
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

/**
 * @author benyq
 * @emil 1520063035@qq.com
 * create at 2020/2/9
 * description:
 */
class BookShelfAdapter : BaseQuickAdapter<BookInfoEntity, BaseViewHolder>(R.layout.novel_item_book_shelf){
    override fun convert(helper: BaseViewHolder, item: BookInfoEntity?) {
        item?.run {
            helper.setText(R.id.tvBookName, name.replace(".txt", ""))
                .setText(R.id.tvBookAuthor, "作者 $author")
                .setText(R.id.tvBookUpdateTime, latestUpdateTime)
                .setText(R.id.tvBookSource, "来源 ${if (isLocal) "本地" else noteUrl}")

            val ivCover = helper.getView<ImageView>(R.id.ivBookCover)
            Glide.with(mContext).load(coverUrl)
                .placeholder(R.drawable.novel_ic_txt)
                .error(R.drawable.novel_ic_txt)
                .into(ivCover)
            helper.setGone(R.id.cbSelected, false)
        }
    }
}