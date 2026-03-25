package com.example.shopflowers.model.entity;

public class OperatorDetails {

    private final int userId;
    private final double salary;
    private final int contractYear;
    private final int annualHours;

    public OperatorDetails(int userId, double salary, int contractYear, int annualHours) {
        this.userId = userId;
        this.salary = salary;
        this.contractYear = contractYear;
        this.annualHours = annualHours;
    }

    public int getUserId() {
        return userId;
    }

    public double getSalary() {
        return salary;
    }

    public int getContractYear() {
        return contractYear;
    }

    public int getAnnualHours() {
        return annualHours;
    }
}