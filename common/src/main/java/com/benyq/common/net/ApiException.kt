package com.benyq.common.net

import java.lang.Exception

/**
 * @author benyq
 * @emil 1520063035@qq.com
 * create at 2020/1/12
 * description:
 */
class ApiException(val code: Int, val msg: String) : RuntimeException()