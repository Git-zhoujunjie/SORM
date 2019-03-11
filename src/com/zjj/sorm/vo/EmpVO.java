package com.zjj.sorm.vo;

public class EmpVO {
    private Integer id;
    private String empname;
    private Double Money;
    private Integer age;
    private String Dep;
    private String Address;

    public EmpVO(Integer id, String empname, Double money, Integer age, String dep, String address) {
        this.id = id;
        this.empname = empname;
        Money = money;
        this.age = age;
        Dep = dep;
        Address = address;
    }

    public EmpVO() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmpname() {
        return empname;
    }

    public void setEmpname(String empname) {
        this.empname = empname;
    }

    public Double getMoney() {
        return Money;
    }

    public void setMoney(Double money) {
        Money = money;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getDep() {
        return Dep;
    }

    public void setDep(String dep) {
        Dep = dep;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }
}
