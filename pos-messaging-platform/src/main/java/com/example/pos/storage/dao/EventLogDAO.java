package com.example.pos.storage.dao;

import com.example.pos.storage.model.EventLog;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface EventLogDAO {
    long save(EventLog event) throws SQLException;
    int[] batchInsert(List<EventLog> events) throws SQLException;
    List<EventLog> fetchByStatus(String status, int limit) throws SQLException;
    boolean updateStatus(List<Long> ids, String status) throws SQLException;
    boolean deleteOlderThan(int days) throws SQLException;
    Optional<EventLog> getById(Long id) throws SQLException;
}

