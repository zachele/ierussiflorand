package com.example.shopflowers.model.dao;

import com.example.shopflowers.model.entity.User;

import java.sql.SQLException;
import java.util.List;

public interface UserDAO {

    User findByUsernameAndPassword(String username, String password) throws SQLException;

    boolean existsByUsername(String username) throws SQLException;

    void save(User user) throws SQLException;

    int saveAndReturnId(User user) throws SQLException;

    List<User> findAllOperators() throws SQLException;

    void deleteById(int userId) throws SQLException;

    void updateNameAndSurname(int userId, String name, String surname) throws SQLException;

    boolean existsByUsernameAndPassword(String username, String password) throws SQLException;

    void updatePassword(String username, String newPassword) throws SQLException;
}