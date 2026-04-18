package com.example.shopflowers.model.dao;

import com.example.shopflowers.model.entity.User;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class UserFileDAO implements UserDAO {

    private static final String FILE_PATH = "data/users.csv";
    private static final String FILE_HEADER = "id;name;surname;username;password;role";
    private static final int EXPECTED_PARTS = 6;

    @Override
    public User findByUsernameAndPassword(String username, String password) throws SQLException {
        for (User user : findAllUsers()) {
            if (matchesUsernameAndPassword(user, username, password)) {
                return copyUser(user);
            }
        }

        return null;
    }

    @Override
    public boolean existsByUsername(String username) throws SQLException {
        for (User user : findAllUsers()) {
            if (matchesUsername(user, username)) {
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
        List<User> users = findAllUsers();
        int nextId = getNextId(users);

        User newUser = new User(
                nextId,
                user.getName(),
                user.getSurname(),
                user.getUsername(),
                user.getPassword(),
                user.getRole()
        );

        users.add(newUser);
        writeAll(users);
        return nextId;
    }

    @Override
    public List<User> findAllOperators() throws SQLException {
        List<User> operators = new ArrayList<>();

        for (User user : findAllUsers()) {
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
    public void deleteById(int userId) throws SQLException {
        List<User> users = findAllUsers();
        users.removeIf(user -> user.getId() == userId);
        writeAll(users);
    }

    @Override
    public void updateNameAndSurname(int userId, String name, String surname) throws SQLException {
        List<User> users = findAllUsers();

        for (User user : users) {
            if (user.getId() == userId) {
                user.setName(name);
                user.setSurname(surname);
                writeAll(users);
                return;
            }
        }
    }

    @Override
    public boolean existsByUsernameAndPassword(String username, String password) throws SQLException {
        for (User user : findAllUsers()) {
            if (matchesUsernameAndPassword(user, username, password)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void updatePassword(String username, String newPassword) throws SQLException {
        List<User> users = findAllUsers();

        for (User user : users) {
            if (matchesUsername(user, username)) {
                user.setPassword(newPassword);
                writeAll(users);
                return;
            }
        }
    }

    private List<User> findAllUsers() throws SQLException {
        ensureFileExists();

        List<User> users = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(FILE_PATH))) {
            String header = reader.readLine();

            if (header == null) {
                return users;
            }

            String line;
            while ((line = reader.readLine()) != null) {
                User parsedUser = parseUser(line);

                if (parsedUser != null) {
                    users.add(parsedUser);
                }
            }

        } catch (IOException e) {
            throw new SQLException("Errore nella lettura degli utenti da file.", e);
        }

        return users;
    }

    private void writeAll(List<User> users) throws SQLException {
        ensureFileExists();

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(FILE_PATH))) {
            writer.write(FILE_HEADER);
            writer.newLine();

            for (User user : users) {
                writer.write(String.format(
                        "%d;%s;%s;%s;%s;%s",
                        user.getId(),
                        escape(user.getName()),
                        escape(user.getSurname()),
                        escape(user.getUsername()),
                        escape(user.getPassword()),
                        escape(user.getRole())
                ));
                writer.newLine();
            }

        } catch (IOException e) {
            throw new SQLException("Errore nella scrittura degli utenti su file.", e);
        }
    }

    private void ensureFileExists() throws SQLException {
        try {
            Path filePath = Paths.get(FILE_PATH);
            Path parent = filePath.getParent();

            if (parent != null && Files.notExists(parent)) {
                Files.createDirectories(parent);
            }

            if (Files.notExists(filePath)) {
                try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
                    writer.write(FILE_HEADER);
                    writer.newLine();
                    writer.write("1;Admin;File;admin;admin123;ADMIN");
                    writer.newLine();
                    writer.write("2;Mario;Rossi;mario_rossi;cliente123;CUSTOMER");
                    writer.newLine();
                    writer.write("3;Luca;Bianchi;operatore;operatore123;OPERATOR");
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new SQLException("Errore nella creazione del file utenti.", e);
        }
    }

    private User parseUser(String line) {
        if (line == null || line.isBlank()) {
            return null;
        }

        String[] parts = line.split(";", -1);

        if (parts.length != EXPECTED_PARTS) {
            return null;
        }

        try {
            return new User(
                    Integer.parseInt(parts[0]),
                    parts[1],
                    parts[2],
                    parts[3],
                    parts[4],
                    parts[5]
            );
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private boolean matchesUsername(User user, String username) {
        return user.getUsername().equals(username);
    }

    private boolean matchesUsernameAndPassword(User user, String username, String password) {
        return matchesUsername(user, username) && user.getPassword().equals(password);
    }

    private boolean isOperator(User user) {
        return "OPERATOR".equals(user.getRole());
    }

    private int getNextId(List<User> users) {
        int maxId = 0;

        for (User user : users) {
            if (user.getId() > maxId) {
                maxId = user.getId();
            }
        }

        return maxId + 1;
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

    private String escape(String value) {
        return value == null ? "" : value.replace(";", ",");
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}