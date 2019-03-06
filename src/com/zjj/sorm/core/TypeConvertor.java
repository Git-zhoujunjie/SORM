package com.zjj.sorm.core;

/**
 * 负责java数据类型和数据库数据类型的相互装换
 */
public interface TypeConvertor {
    /**
     * 将数据库类型转化成Java的数据类型
     * @param columnType  数据库字段的数据类型
     * @return Java的数据类型
     */
    String databaseType2JavaType(String columnType);

    /**
     * 将Java数据类型转化成数据可数据类型
     * @param javaDataType Java的数据类型
     * @return 数据库字段的数据类型
     */
    String javaType2DatabaseType(String javaDataType);
}
