package com.zjj.sorm.core;

import com.zjj.sorm.bean.Configuration;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 *根据配置信息，维持连接对象的管理（后期增加连接池功能）
 */
public class DBManager {
    private static Configuration conf;

    static { //静态代码块
        Properties pros = new Properties();
        try {
            pros.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        conf = new Configuration();
        conf.setDriver(pros.getProperty("driver"));
        conf.setUrl(pros.getProperty("url"));
        conf.setUser(pros.getProperty("user"));
        conf.setPwd(pros.getProperty("pwd"));
        conf.setPoPackage(pros.getProperty("poPackage"));
        conf.setSrcPath(pros.getProperty("srcPath"));
        conf.setUsingDB(pros.getProperty("usingDB"));
    }

    public static Connection getConn(){
        try {
            Class.forName(conf.getDriver());
            //目前直接建立连接，后期增加连接池，提高效率
            return DriverManager.getConnection(conf.getUrl(),conf.getUser(),conf.getPwd());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Configuration getConf(){
        return conf;
    }
}
