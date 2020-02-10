package com.benyq.novel.book.ext

/**
 * @author benyq
 * @emil 1520063035@qq.com
 * create at 2020/2/10
 * description:
 */
enum class CustomerCharset(var charsetName: String) {
    UTF8("UTF-8"),
    UTF16LE("UTF-16LE"),
    UTF16BE("UTF-16BE"),
    GBK("GBK");
    companion object {
        val BLANK: Byte = 0x0a
    }
}