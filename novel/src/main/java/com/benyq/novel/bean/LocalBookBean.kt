package com.benyq.novel.bean

/**
 * @author benyq
 * @emil 1520063035@qq.com
 * create at 2020/2/9
 * description:
 */
data class LocalBookBean(val bookName: String, val filePath: String, var isImported: Boolean = false, var checked : Boolean = false)