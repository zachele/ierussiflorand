package com.example.shopflowers.model.dao;

import com.example.shopflowers.model.entity.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserMemoryDAO implements UserDAO {

    private static final List<User> users = new ArrayList<>();
    private static boolean initialized = false;
    private static int nextId = 1;

    public UserMemoryDAO() {
        initializeIfNeeded();
    }

    @Override
    public User findByUsernameAndPassword(String username, String password) throws SQLException {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return copyUser(user);
            }
        }
        return null;
    }

    @Override
    public boolean existsByUsername(String username) throws SQLException {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void save(User user) throws SQLException {
        saveAndReturnId(user);
    }

    @Override
    public int saveAndReturnId(User user) throws SQLException {
        User newUser = new User(
                nextId++,
                user.getName(),
                user.getSurname(),
                user.getUsername(),
                user.getPassword(),
                user.getRole()
        );
        users.add(newUser);
        return newUser.getId();
    }

    @Override
    public List<User> findAllOperators() throws SQLException {
        List<User> operators = new ArrayList<>();

        for (User user : users) {
            if ("OPERATOR".equals(user.getRole())) {
                operators.add(copyUser(user));
            }
        }

        operators.sort((u1, u2) -> {
            int surnameCompare = safe(u1.getSurname()).compareToIgnoreCase(safe(u2.getSurname()));
            if (surnameCompare != 0) {
                return surnameCompare;
            }
            return safe(u1.getName()).compareToIgnoreCase(safe(u2.getName()));
        });

        return operators;
    }

    @Override
    public void deleteById(int userId) throws SQLException {
        users.removeIf(user -> user.getId() == userId);
    }

    @Override
    public void updateNameAndSurname(int userId, String name, String surname) throws SQLException {
        for (User user : users) {
            if (user.getId() == userId) {
                user.setName(name);
                user.setSurname(surname);
                return;
            }
        }
    }

    @Override
    public boolean existsByUsernameAndPassword(String username, String password) throws SQLException {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void updatePassword(String username, String newPassword) throws SQLException {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                user.setPassword(newPassword);
                return;
            }
        }
    }

    private void initializeIfNeeded() {
        if (initialized) {
            return;
        }

        users.clear();

        users.add(new User(nextId++, "Admin", "Demo", "admin", "admin123", "ADMIN"));
        users.add(new User(nextId++, "Mario", "Rossi", "mario_rossi", "cliente123", "CUSTOMER"));
        users.add(new User(nextId++, "Luca", "Bianchi", "operatore", "operatore123", "OPERATOR"));

        initialized = true;
    }

    private User copyUser(User user) {
        return new User(
                user.getId(),
                user.getName(),
                user.getSurname(),
                user.getUsername(),
                user.getPassword(),
                user.getRole()
        );
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}