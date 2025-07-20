package com.example.pos.storage.db;
import io.micronaut.runtime.event.ApplicationStartupEvent;
import io.micronaut.runtime.event.annotation.EventListener;
import jakarta.inject.Singleton;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

@Singleton
public class DatabaseInitializer {

    private final DataSource dataSource;

    public DatabaseInitializer(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @EventListener
    public void onStartup(ApplicationStartupEvent event) {
        try (Connection conn = dataSource.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS event_log (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "topic TEXT NOT NULL," +
                    "event_type TEXT," +
                    "payload TEXT NOT NULL," +
                    "status TEXT DEFAULT 'NEW'," +
                    "created_at DATETIME DEFAULT CURRENT_TIMESTAMP," +
                    "updated_at DATETIME" +
                    ")");
            stmt.executeUpdate("CREATE INDEX IF NOT EXISTS idx_event_status ON event_log(status)");
        } catch (Exception e) {
            throw new RuntimeException("DB schema init failed", e);
        }
    }
}

