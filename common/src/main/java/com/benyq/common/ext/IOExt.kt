package com.benyq.common.ext

import java.io.Closeable
import java.io.IOException

/**
 * @author benyq
 * @emil 1520063035@qq.com
 * create at 2020/2/7
 * description:
 */
fun ioClose(closeable: Closeable?) {
    if (closeable == null) return
    try {
        closeable.close()
    } catch (e: IOException) {
        e.printStackTrace()
        //close error
    }

}