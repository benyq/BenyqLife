package com.benyq.novel.adapter

import android.widget.CheckBox
import com.benyq.novel.R
import com.benyq.novel.bean.LocalBookBean
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

/**
 * @author benyq
 * @emil 1520063035@qq.com
 * create at 2020/2/9
 * description:
 */
class LocalBooksAdapter :
    BaseQuickAdapter<LocalBookBean, BaseViewHolder>(R.layout.novel_item_local_books) {
    override fun convert(helper: BaseViewHolder, item: LocalBookBean?) {
        item?.run {
            helper.setText(R.id.tvBookName, bookName)
                .setChecked(R.id.cbLocalBook, checked)
            if (isImported) {
                helper.setGone(R.id.cbLocalBook, false)
                    .setGone(R.id.tvImported, true)
            } else {
                helper.setGone(R.id.cbLocalBook, true)
                    .setGone(R.id.tvImported, false)
            }

            val cb = helper.getView<CheckBox>(R.id.cbLocalBook)
            cb.setOnCheckedChangeListener { _, isChecked ->
                checked = isChecked
            }

        }

    }

    fun getSelectedBooks() : List<LocalBookBean>{
        return mData.filter {
            it.checked && !it.isImported
        }
    }
}