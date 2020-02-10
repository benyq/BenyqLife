package com.benyq.novel.bean

import java.io.Serializable

/**
 * @author benyq
 * @emil 1520063035@qq.com
 * create at 2020/1/17
 * description:
 */
data class AutoCompleteBean(
    val searchKey: String,
    /**
     * 搜索类型  1 book 2 author
     *
     */
    val searchType: Int
): Serializable {
    companion object {
        const val TYPE_BOOK = 1
        const val TYPE_AUTHOR = 2
    }
}