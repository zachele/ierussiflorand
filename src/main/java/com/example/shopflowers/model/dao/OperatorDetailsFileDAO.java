package com.example.shopflowers.model.dao;

import com.example.shopflowers.model.entity.OperatorDetails;
import com.example.shopflowers.model.entity.OperatorFullData;
import com.example.shopflowers.model.entity.User;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OperatorDetailsFileDAO implements OperatorDetailsDAO {

    private static final String FILE_PATH = "data/operator_details.csv";
    private static final String FILE_HEADER = "userId;salary;contractYear;annualHours";
    private static final int EXPECTED_PARTS = 4;

    private final UserDAO userDAO;

    public OperatorDetailsFileDAO() {
        this.userDAO = new UserFileDAO();
    }

    @Override
    public void save(OperatorDetails operatorDetails) throws SQLException {
        List<OperatorDetails> detailsList = findAllDetails();
        detailsList.add(copyOperatorDetails(operatorDetails));
        writeAll(detailsList);
    }

    @Override
    public void deleteByUserId(int userId) throws SQLException {
        List<OperatorDetails> detailsList = findAllDetails();
        detailsList.removeIf(details -> details.getUserId() == userId);
        writeAll(detailsList);
    }

    @Override
    public void update(OperatorDetails operatorDetails) throws SQLException {
        List<OperatorDetails> detailsList = findAllDetails();

        for (int i = 0; i < detailsList.size(); i++) {
            if (detailsList.get(i).getUserId() == operatorDetails.getUserId()) {
                detailsList.set(i, copyOperatorDetails(operatorDetails));
                writeAll(detailsList);
                return;
            }
        }
    }

    @Override
    public List<OperatorFullData> findAllOperatorFullData() throws SQLException {
        List<OperatorFullData> result = new ArrayList<>();
        List<OperatorDetails> detailsList = findAllDetails();
        List<User> operators = userDAO.findAllOperators();

        for (User user : operators) {
            OperatorDetails matchingDetails = findDetailsByUserId(detailsList, user.getId());

            if (matchingDetails != null) {
                result.add(new OperatorFullData(
                        user.getId(),
                        user.getName(),
                        user.getSurname(),
                        user.getUsername(),
                        matchingDetails.getSalary(),
                        matchingDetails.getContractYear(),
                        matchingDetails.getAnnualHours()
                ));
            }
        }

        return result;
    }

    private List<OperatorDetails> findAllDetails() throws SQLException {
        ensureFileExists();

        List<OperatorDetails> detailsList = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(FILE_PATH))) {
            String header = reader.readLine();

            if (header == null) {
                return detailsList;
            }

            String line;
            while ((line = reader.readLine()) != null) {
                OperatorDetails details = parseOperatorDetails(line);

                if (details != null) {
                    detailsList.add(details);
                }
            }

        } catch (IOException e) {
            throw new SQLException("Errore nella lettura dei dettagli operatore da file.", e);
        }

        return detailsList;
    }

    private void writeAll(List<OperatorDetails> detailsList) throws SQLException {
        ensureFileExists();

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(FILE_PATH))) {
            writer.write(FILE_HEADER);
            writer.newLine();

            for (OperatorDetails details : detailsList) {
                writer.write(String.format(
                        "%d;%.2f;%d;%d",
                        details.getUserId(),
                        details.getSalary(),
                        details.getContractYear(),
                        details.getAnnualHours()
                ));
                writer.newLine();
            }

        } catch (IOException e) {
            throw new SQLException("Errore nella scrittura dei dettagli operatore su file.", e);
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
                    writer.write("3;1450.00;2022;1600");
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new SQLException("Errore nella creazione del file dettagli operatori.", e);
        }
    }

    private OperatorDetails parseOperatorDetails(String line) {
        if (line == null || line.isBlank()) {
            return null;
        }

        String[] parts = line.split(";", -1);

        if (parts.length != EXPECTED_PARTS) {
            return null;
        }

        try {
            return new OperatorDetails(
                    Integer.parseInt(parts[0]),
                    Double.parseDouble(parts[1]),
                    Integer.parseInt(parts[2]),
                    Integer.parseInt(parts[3])
            );
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private OperatorDetails findDetailsByUserId(List<OperatorDetails> detailsList, int userId) {
        for (OperatorDetails details : detailsList) {
            if (details.getUserId() == userId) {
                return details;
            }
        }

        return null;
    }

    private OperatorDetails copyOperatorDetails(OperatorDetails details) {
        return new OperatorDetails(
                details.getUserId(),
                details.getSalary(),
                details.getContractYear(),
                details.getAnnualHours()
        );
    }
}