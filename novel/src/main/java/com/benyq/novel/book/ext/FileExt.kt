package com.benyq.novel.book.ext

import com.benyq.common.ext.ioClose
import java.io.BufferedInputStream
import java.io.FileInputStream

/**
 * @author benyq
 * @emil 1520063035@qq.com
 * create at 2020/2/10
 * description:
 */
//获取文件的编码格式
fun getCharset(fileName: String): CustomerCharset {
    var bis: BufferedInputStream? = null
    var charset = CustomerCharset.GBK
    val first3Bytes = ByteArray(3)
    try {
        var checked = false
        bis = BufferedInputStream(FileInputStream(fileName))
        bis.mark(0)
        var read = bis.read(first3Bytes, 0, 3)
        if (read == -1)
            return charset
        if (first3Bytes[0] == 0xEF.toByte()
            && first3Bytes[1] == 0xBB.toByte()
            && first3Bytes[2] == 0xBF.toByte()
        ) {
            charset = CustomerCharset.UTF8
            checked = true
        }
        /*
             * 不支持 UTF16LE 和 UTF16BE
            else if (first3Bytes[0] == (byte) 0xFF && first3Bytes[1] == (byte) 0xFE) {
                charset = CustomerCharset.UTF16LE;
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xFE
                    && first3Bytes[1] == (byte) 0xFF) {
                charset = CustomerCharset.UTF16BE;
                checked = true;
            } else */

        bis.mark(0)
        if (!checked) {
            while (bis.read().apply { read = this } != -1) {
                if (read >= 0xF0)
                    break
                if (read in 0x80..0xBF)
                // 单独出现BF以下的，也算是GBK
                    break
                if (read in 0xC0..0xDF) {
                    read = bis.read()
                    if (read in 0x80..0xBF)
                    // 双字节 (0xC0 - 0xDF)
                    // (0x80 - 0xBF),也可能在GB编码内
                        continue
                    else
                        break
                } else if (read in 0xE0..0xEF) {// 也有可能出错，但是几率较小
                    read = bis.read()
                    if (read in 0x80..0xBF) {
                        read = bis.read()
                        if (read in 0x80..0xBF) {
                            charset = CustomerCharset.UTF8
                            break
                        } else
                            break
                    } else
                        break
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        ioClose(bis)
    }
    return charset
}