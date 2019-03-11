package com.zjj.sorm.utils;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 封装JDBC查询常用的操作
 */
public class JDBCUtils {

    public static void handleParams(PreparedStatement ps, Object[] params) {
        //给sql顺序设置参数
        try {
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    ps.setObject(i + 1, params[i]);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}