package com.example.shopflowers.controller.application;

import com.example.shopflowers.config.AppConfig;
import com.example.shopflowers.config.AppMode;
import com.example.shopflowers.exception.InvalidOperatorDataException;
import com.example.shopflowers.exception.UserAlreadyExistsException;
import com.example.shopflowers.model.bean.OperatorBean;
import com.example.shopflowers.model.entity.OperatorFullData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ManageOperatorControllerTest {

    private ManageOperatorController manageOperatorController;

    @BeforeEach
    void setUp() {
        AppConfig.setMode(AppMode.DEMO);
        manageOperatorController = new ManageOperatorController();
    }

    @Test
    void createOperator_validBean_shouldCreateOperator()
            throws SQLException, UserAlreadyExistsException, InvalidOperatorDataException {

        int initialSize = manageOperatorController.getAllOperators().size();

        OperatorBean operatorBean = new OperatorBean();
        operatorBean.setName("Giovanni");
        operatorBean.setSurname("Test");
        operatorBean.setUsername("operatore_test_1");
        operatorBean.setPassword("password123");
        operatorBean.setSalary("1500");
        operatorBean.setContractYear("2024");
        operatorBean.setAnnualHours("1600");

        boolean result = manageOperatorController.createOperator(operatorBean);

        assertTrue(result);

        List<OperatorFullData> operators = manageOperatorController.getAllOperators();
        assertEquals(initialSize + 1, operators.size());

        OperatorFullData createdOperator = operators.stream()
                .filter(operator -> "operatore_test_1".equals(operator.getUsername()))
                .findFirst()
                .orElse(null);

        assertNotNull(createdOperator);
        assertEquals("Giovanni", createdOperator.getName());
        assertEquals("Test", createdOperator.getSurname());
        assertEquals(1500.0, createdOperator.getSalary());
        assertEquals(2024, createdOperator.getContractYear());
        assertEquals(1600, createdOperator.getAnnualHours());
    }

    @Test
    void createOperator_existingUsername_shouldThrowUserAlreadyExistsException() {
        OperatorBean operatorBean = new OperatorBean();
        operatorBean.setName("Luca");
        operatorBean.setSurname("Bianchi");
        operatorBean.setUsername("operatore");
        operatorBean.setPassword("operatore123");
        operatorBean.setSalary("1400");
        operatorBean.setContractYear("2023");
        operatorBean.setAnnualHours("1500");

        assertThrows(
                UserAlreadyExistsException.class,
                () -> manageOperatorController.createOperator(operatorBean)
        );
    }

    @Test
    void createOperator_invalidNumericData_shouldThrowInvalidOperatorDataException() {
        OperatorBean operatorBean = new OperatorBean();
        operatorBean.setName("Mario");
        operatorBean.setSurname("Verdi");
        operatorBean.setUsername("operatore_test_2");
        operatorBean.setPassword("password123");
        operatorBean.setSalary("-100");
        operatorBean.setContractYear("1990");
        operatorBean.setAnnualHours("-50");

        assertThrows(
                InvalidOperatorDataException.class,
                () -> manageOperatorController.createOperator(operatorBean)
        );
    }

    @Test
    void createOperator_blankName_shouldReturnFalse()
            throws SQLException, UserAlreadyExistsException, InvalidOperatorDataException {

        OperatorBean operatorBean = new OperatorBean();
        operatorBean.setName("");
        operatorBean.setSurname("Verdi");
        operatorBean.setUsername("operatore_test_3");
        operatorBean.setPassword("password123");
        operatorBean.setSalary("1400");
        operatorBean.setContractYear("2024");
        operatorBean.setAnnualHours("1500");

        boolean result = manageOperatorController.createOperator(operatorBean);

        assertFalse(result);
    }

    @Test
    void updateOperator_validBean_shouldUpdateOperator()
            throws SQLException, UserAlreadyExistsException, InvalidOperatorDataException {

        OperatorBean createBean = new OperatorBean();
        createBean.setName("Paolo");
        createBean.setSurname("Originale");
        createBean.setUsername("operatore_test_4");
        createBean.setPassword("password123");
        createBean.setSalary("1300");
        createBean.setContractYear("2023");
        createBean.setAnnualHours("1500");

        boolean created = manageOperatorController.createOperator(createBean);
        assertTrue(created);

        OperatorFullData createdOperator = manageOperatorController.getAllOperators().stream()
                .filter(operator -> "operatore_test_4".equals(operator.getUsername()))
                .findFirst()
                .orElse(null);

        assertNotNull(createdOperator);

        OperatorBean updateBean = new OperatorBean();
        updateBean.setUserId(createdOperator.getUserId());
        updateBean.setName("PaoloAggiornato");
        updateBean.setSurname("Nuovo");
        updateBean.setSalary("1800");
        updateBean.setContractYear("2025");
        updateBean.setAnnualHours("1700");

        boolean updated = manageOperatorController.updateOperator(updateBean);

        assertTrue(updated);

        OperatorFullData updatedOperator = manageOperatorController.getAllOperators().stream()
                .filter(operator -> operator.getUserId() == createdOperator.getUserId())
                .findFirst()
                .orElse(null);

        assertNotNull(updatedOperator);
        assertEquals("PaoloAggiornato", updatedOperator.getName());
        assertEquals("Nuovo", updatedOperator.getSurname());
        assertEquals(1800.0, updatedOperator.getSalary());
        assertEquals(2025, updatedOperator.getContractYear());
        assertEquals(1700, updatedOperator.getAnnualHours());
    }

    @Test
    void updateOperator_invalidNumericData_shouldThrowInvalidOperatorDataException()
            throws SQLException, UserAlreadyExistsException, InvalidOperatorDataException {

        OperatorBean createBean = new OperatorBean();
        createBean.setName("Marco");
        createBean.setSurname("Valido");
        createBean.setUsername("operatore_test_5");
        createBean.setPassword("password123");
        createBean.setSalary("1400");
        createBean.setContractYear("2024");
        createBean.setAnnualHours("1600");

        boolean created = manageOperatorController.createOperator(createBean);
        assertTrue(created);

        OperatorFullData createdOperator = manageOperatorController.getAllOperators().stream()
                .filter(operator -> "operatore_test_5".equals(operator.getUsername()))
                .findFirst()
                .orElse(null);

        assertNotNull(createdOperator);

        OperatorBean updateBean = new OperatorBean();
        updateBean.setUserId(createdOperator.getUserId());
        updateBean.setName("Marco");
        updateBean.setSurname("Valido");
        updateBean.setSalary("-1");
        updateBean.setContractYear("1999");
        updateBean.setAnnualHours("-50");

        assertThrows(
                InvalidOperatorDataException.class,
                () -> manageOperatorController.updateOperator(updateBean)
        );
    }

    @Test
    void deleteOperator_existingOperator_shouldRemoveOperator()
            throws SQLException, UserAlreadyExistsException, InvalidOperatorDataException {

        OperatorBean createBean = new OperatorBean();
        createBean.setName("Marco");
        createBean.setSurname("Delete");
        createBean.setUsername("operatore_test_6");
        createBean.setPassword("password123");
        createBean.setSalary("1450");
        createBean.setContractYear("2024");
        createBean.setAnnualHours("1550");

        boolean created = manageOperatorController.createOperator(createBean);
        assertTrue(created);

        OperatorFullData createdOperator = manageOperatorController.getAllOperators().stream()
                .filter(operator -> "operatore_test_6".equals(operator.getUsername()))
                .findFirst()
                .orElse(null);

        assertNotNull(createdOperator);

        manageOperatorController.deleteOperator(createdOperator.getUserId());

        OperatorFullData deletedOperator = manageOperatorController.getAllOperators().stream()
                .filter(operator -> operator.getUserId() == createdOperator.getUserId())
                .findFirst()
                .orElse(null);

        assertNull(deletedOperator);
    }
}