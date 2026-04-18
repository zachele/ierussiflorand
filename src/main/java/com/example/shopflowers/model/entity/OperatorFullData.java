package com.example.shopflowers.model.entity;

public record OperatorFullData(
        int id,
        String name,
        String surname,
        String username,
        double salary,
        int contractYear,
        int annualHours
) {

    public int getId() {
        return id;
    }

    public int getUserId() {
        return getId();
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