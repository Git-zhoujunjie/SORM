package com.zjj.sorm.utils;

/**
 *封装字符串常用的操作
 *
 */
public class StringUtils {
    /**
     * 将字符串首字母大写
     * @param str
     * @return
     */
    public static String firstChar2UpperCase(String str){
        return str.toUpperCase().substring(0,1) + str.substring(1);
    }
}
