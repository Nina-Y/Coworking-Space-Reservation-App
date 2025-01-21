package com.example.coworking.util;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class DatabaseInitializer {

    public static void initializeDatabase() {
        try (Connection connection = DBUtil.getConnectionWithoutSchema()) {
            try (Statement statement = connection.createStatement()) {

                String createSchemaQuery = "CREATE SCHEMA IF NOT EXISTS coworking_spaces";
                statement.executeUpdate(createSchemaQuery);

                String useSchemaQuery = "USE coworking_spaces";
                statement.executeUpdate(useSchemaQuery);

                String createWorkspacesTable = "CREATE TABLE IF NOT EXISTS workspaces (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "type VARCHAR(255) NOT NULL, " +
                        "price DOUBLE NOT NULL, " +
                        "status ENUM('available', 'reserved') DEFAULT 'available' NOT NULL)";
                statement.executeUpdate(createWorkspacesTable);

                String createReservationsTable = "CREATE TABLE IF NOT EXISTS reservations (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "workspace_id INT NOT NULL, " +
                        "type VARCHAR(255) NOT NULL, " +
                        "customer_name VARCHAR(255) NOT NULL, " +
                        "date DATE NOT NULL, " +
                        "start_time TIME NOT NULL, " +
                        "end_time TIME NOT NULL, " +
                        "total_price DOUBLE NOT NULL, " +
                        "FOREIGN KEY (workspace_id) REFERENCES workspaces(id))";
                statement.executeUpdate(createReservationsTable);

                String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                        "username VARCHAR(255) PRIMARY KEY, " +
                        "password VARCHAR(255) NOT NULL)";
                statement.executeUpdate(createUsersTable);

                System.out.println("Database and tables initialized successfully.");

            }
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }
}

