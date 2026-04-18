package com.example.shopflowers.model.entity;

public record OperatorDetails(
        int userId,
        double salary,
        int contractYear,
        int annualHours
) {

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