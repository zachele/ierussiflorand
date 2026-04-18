package com.example.shopflowers.model.dao;

import com.example.shopflowers.model.entity.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class UserMemoryDAO implements UserDAO {

    private static final List<User> USERS = new ArrayList<>();
    private static boolean initialized = false;
    private static int nextId = 1;

    public UserMemoryDAO() {
        initializeIfNeeded();
    }

    @Override
    public User findByUsernameAndPassword(String username, String password) {
        for (User user : USERS) {
            if (matchesUsernameAndPassword(user, username, password)) {
                return copyUser(user);
            }
        }

        return null;
    }

    @Override
    public boolean existsByUsername(String username) {
        for (User user : USERS) {
            if (matchesUsername(user, username)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void save(User user) {
        saveAndReturnId(user);
    }

    @Override
    public int saveAndReturnId(User user) {
        int newUserId = getNextId();

        User newUser = new User(
                newUserId,
                user.getName(),
                user.getSurname(),
                user.getUsername(),
                user.getPassword(),
                user.getRole()
        );

        USERS.add(newUser);
        return newUser.getId();
    }

    @Override
    public List<User> findAllOperators() {
        List<User> operators = new ArrayList<>();

        for (User user : USERS) {
            if (isOperator(user)) {
                operators.add(copyUser(user));
            }
        }

        operators.sort(Comparator
                .comparing((User user) -> safe(user.getSurname()), String.CASE_INSENSITIVE_ORDER)
                .thenComparing(user -> safe(user.getName()), String.CASE_INSENSITIVE_ORDER));

        return operators;
    }

    @Override
    public void deleteById(int userId) {
        USERS.removeIf(user -> user.getId() == userId);
    }

    @Override
    public void updateNameAndSurname(int userId, String name, String surname) {
        for (User user : USERS) {
            if (user.getId() == userId) {
                user.setName(name);
                user.setSurname(surname);
                return;
            }
        }
    }

    @Override
    public boolean existsByUsernameAndPassword(String username, String password) {
        for (User user : USERS) {
            if (matchesUsernameAndPassword(user, username, password)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void updatePassword(String username, String newPassword) {
        for (User user : USERS) {
            if (matchesUsername(user, username)) {
                user.setPassword(newPassword);
                return;
            }
        }
    }

    private static void initializeIfNeeded() {
        if (initialized) {
            return;
        }

        USERS.clear();

        USERS.add(new User(getNextId(), "Admin", "Demo", "admin", "admin123", "ADMIN"));
        USERS.add(new User(getNextId(), "Mario", "Rossi", "mario_rossi", "cliente123", "CUSTOMER"));
        USERS.add(new User(getNextId(), "Luca", "Bianchi", "operatore", "operatore123", "OPERATOR"));

        initialized = true;
    }

    private static int getNextId() {
        return nextId++;
    }

    private static boolean matchesUsername(User user, String username) {
        return user.getUsername().equals(username);
    }

    private static boolean matchesUsernameAndPassword(User user, String username, String password) {
        return matchesUsername(user, username) && user.getPassword().equals(password);
    }

    private static boolean isOperator(User user) {
        return "OPERATOR".equals(user.getRole());
    }

    private static User copyUser(User user) {
        return new User(
                user.getId(),
                user.getName(),
                user.getSurname(),
                user.getUsername(),
                user.getPassword(),
                user.getRole()
        );
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}