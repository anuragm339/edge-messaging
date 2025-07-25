package com.example.pos.storage.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Primary;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.File;

@Factory
public class DatabaseFactory {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseFactory.class);

    @Singleton
    @Primary
    public DataSource dataSource(@Value("${db.path:./data/pos.db}") String dbPath) {
        // Ensure database directory exists
        createDatabaseDirectory(dbPath);
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:sqlite:" + dbPath);
        // SQLite-specific configurations
        hikariConfig.setMaximumPoolSize(Runtime.getRuntime().availableProcessors() * 4);
        hikariConfig.setMinimumIdle(Runtime.getRuntime().availableProcessors());
        hikariConfig.setIdleTimeout(30000);
        hikariConfig.setMaxLifetime(45000);
        hikariConfig.setConnectionTimeout(30000);
        hikariConfig.addDataSourceProperty("journal_mode", "WAL");// Write-Ahead Logging
        hikariConfig.addDataSourceProperty("synchronous", "NORMAL");         // Balance durability and speed
        hikariConfig.addDataSourceProperty("busy_timeout", "100000");         // Wait up to 30 seconds when busy
        hikariConfig.addDataSourceProperty("cache_size", "2000");           // 2MB cache
        hikariConfig.addDataSourceProperty("foreign_keys", "ON");

        return new HikariDataSource(hikariConfig);
    }


    private void createDatabaseDirectory(String dbPath) {
        try {
            File dbFile = new File(dbPath);
            File dbDir = dbFile.getParentFile();
            if (dbDir != null && !dbDir.exists()) {
                boolean created = dbDir.mkdirs();
                if (created) {
                    logger.debug("Created database directory: {}", dbDir.getAbsolutePath());
                } else {
                    logger.warn("Failed to create database directory: {}", dbDir.getAbsolutePath());
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to create database directory for path: " + dbPath, e);
        }
    }
}

