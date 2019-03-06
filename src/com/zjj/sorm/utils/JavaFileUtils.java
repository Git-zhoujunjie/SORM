package com.zjj.sorm.utils;

import com.zjj.sorm.bean.ColumnInfo;
import com.zjj.sorm.bean.JavaFieldGetSet;
import com.zjj.sorm.bean.TableInfo;
import com.zjj.sorm.core.DBManager;
import com.zjj.sorm.core.MySqlTypeConvertor;
import com.zjj.sorm.core.TableContext;
import com.zjj.sorm.core.TypeConvertor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *封装了生成Java文件（源代码）常用的操作
 */
public class JavaFileUtils {

    /**
     * 根据字段信息生产Java属性信息。如：var username---> private String username;以及相应的set/get方法
     * @param column 字段信息
     * @param convertor 类型转化器
     * @return Java属性和set/get方法
     */
    public static JavaFieldGetSet createFieldGetSetSRC(ColumnInfo column, TypeConvertor convertor){
        JavaFieldGetSet jfgs = new JavaFieldGetSet();

        String javaFieldType = convertor.databaseType2JavaType(column.getDataType());

        jfgs.setFieldInfo("\tprivate "+javaFieldType+" "+column.getName()+";\n");

        //生成get方法源码
        //public String getName(){return name;}
        StringBuilder getStr = new StringBuilder();
        getStr.append("\tpublic "+javaFieldType+" get"+StringUtils.firstChar2UpperCase(column.getName())+"(){\n");
        getStr.append("\t\treturn "+column.getName()+";\n");
        getStr.append("\t}\n");
        jfgs.setGetInfo(getStr.toString());

        //生成set方法源码
        //public void setName(String name){this.name = name;}
        StringBuilder setStr = new StringBuilder();
        setStr.append("\tpublic void set"+StringUtils.firstChar2UpperCase(column.getName())+"(" +
                javaFieldType+" "+column.getName()+"){\n");
        setStr.append("\t\tthis."+column.getName()+"="+column.getName()+";\n");
        setStr.append("\t}\n");
        jfgs.setSetInfo(setStr.toString());

        return jfgs;
    }

    /**
     * 根据表信息生成Java类的源代码
     * @param tableInfo 表信息
     * @param convertor 数据类型转化器
     * @return  类的源代码
     */
    public static String createJavaSrc(TableInfo tableInfo,TypeConvertor convertor){

        Map<String,ColumnInfo> columns = tableInfo.getColumns();
        List<JavaFieldGetSet> javaFields = new ArrayList<>();

        for(ColumnInfo c:columns.values()){
            javaFields.add(createFieldGetSetSRC(c,convertor));
        }

        StringBuilder src = new StringBuilder();
        //生成package语句
        src.append("package "+ DBManager.getConf().getPoPackage()+"\n\n");
        //生成import语句
        src.append("import java.sql.*;\n");
        src.append("import java.util.*\n\n");
        //生成类声明语句
        src.append("public class "+StringUtils.firstChar2UpperCase(tableInfo.getTname())+" {\n\n");
        //生成属性列表
        for(JavaFieldGetSet f:javaFields){
            src.append(f.getFieldInfo());
        }
        src.append("\n\n");
        //生成get方法
        for(JavaFieldGetSet f:javaFields){
            src.append(f.getGetInfo());
        }
        src.append("\n\n");
        //生成set方法
        for(JavaFieldGetSet f:javaFields){
            src.append(f.getSetInfo());
        }
        src.append("\n\n");

        src.append("}\n");

        System.out.println(src);
        return src.toString();
    }



    public static void main(String[] args) {
//        ColumnInfo ci = new ColumnInfo("name","varchar",0);
//        JavaFieldGetSet f = createFieldGetSetSRC(ci,new MySqlTypeConvertor());
//
//        System.out.println(f);

        Map<String,TableInfo> map = TableContext.tables;
        TableInfo t = map.get("emp");
        createJavaSrc(t,new MySqlTypeConvertor());
    }
}
