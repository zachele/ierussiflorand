package com.example.shopflowers.model.entity;

public class OperatorFullData {

    private final int userId;
    private final String name;
    private final String surname;
    private final String username;
    private final double salary;
    private final int contractYear;
    private final int annualHours;

    public OperatorFullData(int userId, String name, String surname, String username,
                            double salary, int contractYear, int annualHours) {
        this.userId = userId;
        this.name = name;
        this.surname = surname;
        this.username = username;
        this.salary = salary;
        this.contractYear = contractYear;
        this.annualHours = annualHours;
    }

    public int getUserId() {
        return userId;
    }

    public int getId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getUsername() {
        return username;
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
