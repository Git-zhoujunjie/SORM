package com.zjj.sorm.utils;

import com.zjj.sorm.bean.ColumnInfo;

import java.lang.reflect.Method;

/**
 *
 */
public class ReflectUtils {

    /**
     * 通过反射对象属性名称为paramName的get方法
     * @param obj  对象
     * @param paramName  属性名称
     * @return
     */
    public static Object invokeGet(Object obj, String paramName){

        //通过类的结构信息找到调用主键的get方法    ,String getName();
        try {
            Class clazz = obj.getClass();

            Method method = clazz.getDeclaredMethod("get"+ StringUtils.firstChar2UpperCase(paramName),null);
            return method.invoke(obj,null);  //执行get方法，返回主键值

            //delete(clazz,id);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 通过反射，给obj对象调用set方法，设置obj对象中名称为paramName的属性的值为paramValue
     * @param obj
     * @param paramName
     * @param paramValue
     */
    public static void invokeSet(Object obj,String paramName,Object paramValue){
        try {
            Method method = obj.getClass().getDeclaredMethod("set" +
                    StringUtils.firstChar2UpperCase(paramName), paramValue.getClass());
            method.invoke(obj, paramValue); //将field的值设到对象中
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        String s1 = "mississippi";
        String s2 = "issipi";
        int a = strStr(s1,s2);
        System.out.println(a);
    }

    public static int strStr(String haystack, String needle) {
        //return haystack.indexOf(needle);
        if(needle.length()==0 ) return 0;
        if(needle.length()>haystack.length()) return -1;

        //int i=0;
        char cn = needle.charAt(0);

        for(int j=0;j<haystack.length();j++){
            char ch = haystack.charAt(j);
            if(ch==cn){
                int m=j+1;
                int k;
                for(k=1;k<needle.length();k++){
                    if(m<haystack.length()){
                        char cnn = needle.charAt(k);
                        char chh = haystack.charAt(m);
                        if(chh!=cnn) break;
                        m++;
                    }else return j;
                }
                if(k==needle.length()) {
                    return j;
                }
            }
        }

        return -1;
    }
}
