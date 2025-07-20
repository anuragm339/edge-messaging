package com.example.pos.storage.dao;

import com.example.pos.storage.model.EventLog;
import jakarta.inject.Singleton;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

@Singleton
public class EventLogJdbcDAO implements EventLogDAO {

    private final DataSource dataSource;

    public EventLogJdbcDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public long save(EventLog event) throws SQLException {
        String sql = "INSERT INTO event_log (topic, event_type, payload, status, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, event.getTopic());
            ps.setString(2, event.getEventType());
            ps.setString(3, event.getPayload());
            ps.setString(4, event.getStatus());
            ps.setObject(5, event.getCreatedAt());
            ps.setObject(6, event.getUpdatedAt());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        }
        return -1;
    }

    @Override
    public int[] batchInsert(List<EventLog> events) throws SQLException {
        String sql = "INSERT INTO event_log (topic, event_type, payload, status, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (EventLog e : events) {
                ps.setString(1, e.getTopic());
                ps.setString(2, e.getEventType());
                ps.setString(3, e.getPayload());
                ps.setString(4, e.getStatus());
                ps.setObject(5, e.getCreatedAt());
                ps.setObject(6, e.getUpdatedAt());
                ps.addBatch();
            }
            return ps.executeBatch();
        }
    }

    @Override
    public List<EventLog> fetchByStatus(String status, int limit) throws SQLException {
        String sql = "SELECT * FROM event_log WHERE status = ? ORDER BY created_at LIMIT ?";
        List<EventLog> events = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                EventLog e = new EventLog();
                e.setId(rs.getLong("id"));
                e.setTopic(rs.getString("topic"));
                e.setEventType(rs.getString("event_type"));
                e.setPayload(rs.getString("payload"));
                e.setStatus(rs.getString("status"));
                e.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                Timestamp upd = rs.getTimestamp("updated_at");
                e.setUpdatedAt(upd != null ? upd.toLocalDateTime() : null);
                events.add(e);
            }
        }
        return events;
    }

    @Override
    public boolean updateStatus(List<Long> ids, String status) throws SQLException {
        if (ids == null || ids.isEmpty()) return false;
        String sql = "UPDATE event_log SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE id IN (" +
                String.join(",", Collections.nCopies(ids.size(), "?")) + ")";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            for (int i = 0; i < ids.size(); i++) {
                ps.setObject(i + 2, ids.get(i));
            }
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteOlderThan(int days) throws SQLException {
        String sql = "DELETE FROM event_log WHERE created_at < datetime('now', ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "-" + days + " days");
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public Optional<EventLog> getById(Long id) throws SQLException {
        String sql = "SELECT * FROM event_log WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                EventLog e = new EventLog();
                e.setId(rs.getLong("id"));
                e.setTopic(rs.getString("topic"));
                e.setEventType(rs.getString("event_type"));
                e.setPayload(rs.getString("payload"));
                e.setStatus(rs.getString("status"));
                e.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                Timestamp upd = rs.getTimestamp("updated_at");
                e.setUpdatedAt(upd != null ? upd.toLocalDateTime() : null);
                return Optional.of(e);
            }
        }
        return Optional.empty();
    }
}

