package com.zjj.sorm.core;

import com.zjj.po.Emp;
import com.zjj.sorm.bean.ColumnInfo;
import com.zjj.sorm.bean.TableInfo;
import com.zjj.sorm.utils.JDBCUtils;
import com.zjj.sorm.utils.ReflectUtils;
import com.zjj.sorm.vo.EmpVO;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
public class MySqlQuery implements Query {

    public static void testDML(){
        MySqlQuery test = new MySqlQuery();
        Emp emp = new Emp();
        emp.setId(2);
        emp.setEmpname("老王");
        emp.setSalary(50000.5);
//        emp.setAge(90);
//        emp.setBirthday(new Date(System.currentTimeMillis()));
        //test.insert(emp);
        //test.delete(emp);
        String[] fields = {"empname","salary"};
        //test.update(emp,fields);
    }

    public static void main(String[] args) {
        MySqlQuery test = new MySqlQuery();

//        List<Emp> list = test.queryRows("select * from emp where id>? and age<?",
//                Emp.class,new String[]{"2","90"});
//
//        System.out.println(list);
//        for(Emp emp : list){
//            System.out.println(emp.getId()+"--"+"--"+emp.getAge());
//        }

        //复杂查询
        String sql2 = "select e.id,e.empname,salary+bonus 'Money',age,d.dname 'Dep',d.address 'Address' from emp e\n" +
                "join dept d on e.deptId=d.id;";
        List<EmpVO> list2 = test.queryRows(sql2,EmpVO.class,null);

        for(EmpVO e:list2){
            System.out.println(e.getEmpname()+"--"+e.getAddress()+"--"+e.getMoney());
        }
    }


    @Override
    public int executeDML(String sql, Object[] params) {
        Connection conn = DBManager.getConn();
        int count = 0;

        try (PreparedStatement ps = conn.prepareStatement(sql);) {

            //给sql顺序设置参数
            JDBCUtils.handleParams(ps,params);
            System.out.println(ps);
            count = ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    @Override
    public void insert(Object obj) {
        //1、先从对象找到表，obj-->Class-->table
        Class clazz = obj.getClass();
        TableInfo table = TableContext.poClassTableMap.get(clazz);
        //2、设置sql语句，insert into 表名 (id,name,...) values (?,?...)
        StringBuilder sql = new StringBuilder();
        sql.append("insert into " + table.getTname() + " (");

        //3、从obj对象找到属性，也就是要插入表中的值
        int NotNullFieldCount = 0;
        Field[] fields = clazz.getDeclaredFields();  //属性的名称列表
        List<Object> listFields = new ArrayList<>(); //属性值列表

        for (Field field : fields) {
            String fieldName = field.getName();
            Object fieldValue = ReflectUtils.invokeGet(obj, fieldName);  //通过调用属性对应的get方法将属性值存入list

            if (fieldValue != null) {
                NotNullFieldCount++;
                listFields.add(fieldValue);
                sql.append(fieldName + ",");
            }
        }

        sql.setCharAt(sql.length() - 1, ')');
        sql.append(" values (");
        for (int i = 0; i < NotNullFieldCount; i++) {
            sql.append("?,");
        }
        sql.setCharAt(sql.length() - 1, ')');
        //执行sql
        executeDML(sql.toString(), listFields.toArray());
    }

    @Override
    public void delete(Class clazz, Object primaryKey) {//传入Emp,2---->delete from emp where id=2

        //通过Class对象找TableInfo
        TableInfo table = TableContext.poClassTableMap.get(clazz);
        //获得主键
        ColumnInfo onlyPrimary = table.getOnlyPriKey();

        String sql = "delete from "+table.getTname()+" where "+onlyPrimary.getName()+"=? ";

        executeDML(sql,new Object[]{primaryKey});

    }

    @Override
    public void delete(Object obj) {
        //先通过对象obj找到对应的类，然后通过类找到表结构，在通过表结构找到中间的主键(反射)，然后找到obj对象中的主键值，最后删除表中对应主键的记录
        Class clazz = obj.getClass();
        TableInfo table = TableContext.poClassTableMap.get(clazz);
        ColumnInfo primaryKey = table.getOnlyPriKey();  //主键的名称

        //通过类的结构信息找到调用主键的get方法    ,String getName();
//        try {
//            Method method = clazz.getDeclaredMethod("get"+ StringUtils.firstChar2UpperCase(primaryKey.getName()),null);
//            Object id = method.invoke(obj,null);  //执行get方法，返回主键值
//
//            delete(clazz,id);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        Object keyValue = ReflectUtils.invokeGet(obj,primaryKey.getName());

        delete(clazz,keyValue);


    }

    @Override
    public int update(Object obj, String[] fieldNames) {
        //1、通过obj获得要更新的表
        Class clazz = obj.getClass();
        TableInfo table = TableContext.poClassTableMap.get(clazz);

        //2、设置sql语句，包括要更新的属性列表｛name,age...｝和id，
        // 格式：update 表名 set name=?,age=?... where id=?
        StringBuilder sql = new StringBuilder();
        sql.append("update "+table.getTname()+" set ");
        List<Object> listFields = new ArrayList<>();

        for(String field:fieldNames){
            Object fieldValue = ReflectUtils.invokeGet(obj,field);
            listFields.add(fieldValue);

            sql.append(field+"=?,");
        }
        sql.setCharAt(sql.length()-1,' ');

        //获取主键
        String primaryKeyName = table.getOnlyPriKey().getName();
        Object primaryKeyValue = ReflectUtils.invokeGet(obj,primaryKeyName);
        listFields.add(primaryKeyValue);
        sql.append("where "+primaryKeyName+"=?");

        //执行sql语句
        return executeDML(sql.toString(),listFields.toArray());
    }

    @Override
    public List queryRows(String sql, Class clazz, Object[] params) {
        List<Object> listResult = null;
        Connection conn = DBManager.getConn();
        ResultSet rs = null;
        try (
                PreparedStatement ps = conn.prepareStatement(sql);
        ) {
            // select name,age,... from 表名 where id>? and salary<?

            //设置参数
            JDBCUtils.handleParams(ps,params);
//            for (int i = 0; i < params.length; i++) {
//                ps.setObject(i + 1, params[i]);
//            }

            rs = ps.executeQuery(); //获取满足条件所有的行
            ResultSetMetaData metaData = ps.getMetaData();  //获得表的元信息

            while (rs.next()) {
                if (listResult == null) {
                    listResult = new ArrayList<>();
                }
                //新建一个类对象
                Object obj = clazz.getDeclaredConstructor(null).newInstance();

                //遍历每列，将字段封装到类对象中
                for (int i = 0; i < metaData.getColumnCount(); i++) {
                    String fieldName = metaData.getColumnLabel(i + 1);
                    Object fieldValue = rs.getObject(i + 1);

                    ReflectUtils.invokeSet(obj, fieldName, fieldValue);
                }

                listResult.add(obj);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(rs !=null){
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return listResult;
    }

    @Override
    public Object queryUniqueRow(String sql, Class clazz, Object[] params) {
        List<Object> list = queryRows(sql, clazz, params);

        return (list != null && list.size() > 1) ? list.get(0) : null;
    }

    @Override
    public Object queryValue(String sql, Object[] params) {

        Connection conn = DBManager.getConn();
        ResultSet rs = null;
        Object obj = null;
        try (
                PreparedStatement ps = conn.prepareStatement(sql);
        ) {

            JDBCUtils.handleParams(ps,params);

            rs = ps.executeQuery(); //获取满足条件所有的行


            while (rs.next()) {
                obj = rs.getObject(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(rs !=null){
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return obj;
    }

    @Override
    public Number queryNumber(String sql, Object[] params) {

        return (Number) queryValue(sql,params);
    }
}
