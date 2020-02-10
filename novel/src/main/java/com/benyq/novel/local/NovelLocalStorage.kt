package com.benyq.novel.local

import com.benyq.common.local.MMKVValue
import com.benyq.novel.bean.AutoCompleteBean

/**
 * @author benyq
 * @emil 1520063035@qq.com
 * create at 2020/1/21
 * description:
 */
object NovelLocalStorage {

    var searchHistory by MMKVValue("searchHistory", mutableListOf<AutoCompleteBean>())


}