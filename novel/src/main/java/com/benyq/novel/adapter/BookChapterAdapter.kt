package com.benyq.novel.adapter

import androidx.core.content.ContextCompat
import com.benyq.novel.R
import com.benyq.novel.local.database.enity.BookChapterEntity
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

/**
 * @author benyq
 * @emil 1520063035@qq.com
 * create at 2020/2/2
 * description:
 */
class BookChapterAdapter :
    BaseQuickAdapter<BookChapterEntity, BaseViewHolder>(R.layout.novel_item_book_chapter) {

    var currentChapterPos :Int = -1

    override fun convert(helper: BaseViewHolder, item: BookChapterEntity?) {
        item?.run {
            helper.setText(R.id.tvChapterName, "$chapterName")
                .setTextColor(R.id.tvChapterName, ContextCompat.getColor(mContext, R.color.color3C4044))
                .setTextColor(R.id.tvChapterStatus, ContextCompat.getColor(mContext, R.color.colorC0C0C0))
                .setText(R.id.tvChapterStatus, "已下载")

            if (contentFile == null) {
                helper.setTextColor(R.id.tvChapterName, ContextCompat.getColor(mContext, R.color.colorC0C0C0))
                    .setText(R.id.tvChapterStatus, "")
            }

            if (currentChapterPos == helper.layoutPosition) {
                helper.setTextColor(R.id.tvChapterName, ContextCompat.getColor(mContext, R.color.colorOrange))
                    .setTextColor(R.id.tvChapterStatus, ContextCompat.getColor(mContext, R.color.colorOrange))
                    .setText(R.id.tvChapterStatus, "正在阅读")
            }
        }
    }
}