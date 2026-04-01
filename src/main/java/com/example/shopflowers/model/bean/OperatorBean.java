package com.example.shopflowers.model.bean;

public class OperatorBean {

    private int userId;
    private String name;
    private String surname;
    private String username;
    private String password;
    private String salary;
    private String contractYear;
    private String annualHours;

    public OperatorBean() {
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public String getContractYear() {
        return contractYear;
    }

    public void setContractYear(String contractYear) {
        this.contractYear = contractYear;
    }

    public String getAnnualHours() {
        return annualHours;
    }

    public void setAnnualHours(String annualHours) {
        this.annualHours = annualHours;
    }
}