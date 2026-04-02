package com.example.shopflowers.model.dao;

import com.example.shopflowers.model.entity.OperatorDetails;
import com.example.shopflowers.model.entity.OperatorFullData;

import java.sql.SQLException;
import java.util.List;

public interface OperatorDetailsDAO {

    void save(OperatorDetails operatorDetails) throws SQLException;

    void deleteByUserId(int userId) throws SQLException;

    void update(OperatorDetails operatorDetails) throws SQLException;

    List<OperatorFullData> findAllOperatorFullData() throws SQLException;
}