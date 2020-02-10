package com.benyq.common.ext

import java.lang.reflect.ParameterizedType

/**
 * @author benyq
 * @emil 1520063035@qq.com
 * create at 2020/1/12
 * description:
 */

fun <T> getClass(t: Any): Class<T> {
    // 通过反射 获取父类泛型 (T) 对应 Class类
    return (t.javaClass.genericSuperclass as ParameterizedType)
        .actualTypeArguments[0]
            as Class<T>
}