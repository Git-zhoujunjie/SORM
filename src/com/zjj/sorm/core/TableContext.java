package com.zjj.sorm.core;

import com.zjj.sorm.bean.ColumnInfo;
import com.zjj.sorm.bean.TableInfo;
import com.zjj.sorm.utils.JavaFileUtils;
import com.zjj.sorm.utils.StringUtils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 负责获取管理数据库所有表结构和类结构的信息，并可以根据表结构生成类结构
 */
public class TableContext {
    /**
     * 表名为key,表信息对象为value
     */
    public static Map<String, TableInfo> tables = new HashMap<>();

    /**
     * 将po的class对象和表信息对象关联起来，便于重用！
     */
    public static Map<Class,TableInfo> poClassTableMap = new HashMap<>();

    private TableContext(){}

    static {

        try {
            //初始化获得表的信息
            Connection conn = DBManager.getConn();
            DatabaseMetaData dbmd = conn.getMetaData();

            ResultSet tableRet = dbmd.getTables(conn.getCatalog(),"%","%",new String[]{"TABLE"});

            while(tableRet.next()){
                String tableName = (String) tableRet.getObject("TABLE_NAME");
                TableInfo ti = new TableInfo(tableName,new HashMap<>(),new ArrayList<>());
                tables.put(tableName,ti);

                ResultSet set = dbmd.getColumns(conn.getCatalog(),"%",tableName,"%");//查询表中所有字段
                while(set.next()){
                    ColumnInfo ci = new ColumnInfo(set.getString("COLUMN_NAME"),set.getString("TYPE_NAME"),0);
                    ti.getColumns().put(set.getString("COLUMN_NAME"),ci);
                }

                ResultSet set2 = dbmd.getPrimaryKeys(conn.getCatalog(),"%",tableName);//查询t_user表中主键
                while(set2.next()){
                    ColumnInfo ci2 = ti.getColumns().get(set2.getObject("COLUMN_NAME"));
                    ci2.setKeyType(1); //设为主键类型
                    ti.getPriKeys().add(ci2);
                }

                if(ti.getPriKeys().size()>0){  //取唯一主键，方便使用。如果是联合主键，则为空！
                    ti.setOnlyPriKey(ti.getPriKeys().get(0));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //每次启动框架，都更新类结构
        updatePOFile();

        //加载po包信息
        loadPOTables();
    }


    /**
     * 根据表结构，更新配置的po包下面的Java类
     * 实现了从表结构转化为类结构
     */
    public static void updatePOFile(){
        Map<String,TableInfo> map = TableContext.tables;
        for(TableInfo t:map.values()) {
            //TableInfo t = map.get("emp");
            //createJavaSrc(t,new MySqlTypeConvertor());
            JavaFileUtils.createJavaPOFile(t, new MySqlTypeConvertor());
        }
    }

    /**
     * 加载po包下的类，将类的Class对象与表信息对接，以便重用
     */
    public static void loadPOTables() {
        //Map<Class,TableInfo> poClassTableMap

        for (TableInfo table : tables.values()) {
            Class clazz = null;
            try {
                clazz = Class.forName(DBManager.getConf().getPoPackage()
                        + "." + StringUtils.firstChar2UpperCase(table.getTname()));
                poClassTableMap.put(clazz, table);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) {
//        Map<String ,TableInfo> tables = TableContext.tables;
//        System.out.println(tables);
        //System.out.println(f(new Date()));

        String s = " ";
        int n = lengthOfLongestSubstring(s);
    }

    public static int lengthOfLongestSubstring(String s) {
        int result=0;  //结果长度
        for(int i=0;i<s.length();i++){
            StringBuilder sb = new StringBuilder();
            sb.append(s.charAt(i));
            result = 1;
            for(int j=i+1;j<s.length();j++){
                if(sb.toString().contains(""+s.charAt(j))){  //计算出最长子串
                    //计算sb长度
                    int len = sb.toString().length();
                    if(len>result){
                        result=len;
                    }
                    break;
                }
                sb.append(s.charAt(j));
            }
        }
        return result;
    }
}
