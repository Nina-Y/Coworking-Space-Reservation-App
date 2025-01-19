package com.example.coworking.util;

import com.example.coworking.model.Workspace;

import java.sql.*;
import java.util.List;

public class DBUtil {

    private static final String URL = "jdbc:mysql://localhost:3306/coworking_spaces";
    private static final String USER = "root";
    private static final String PASSWORD = "code";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void ensureWorkspacesPopulated() {
        try (Connection connection = DBUtil.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) AS count FROM workspaces")) {

            if (resultSet.next() && resultSet.getInt("count") == 0) {
                System.out.println("No workspaces found in database. Populating with dummy data.");
                writeDummyDataToDatabase();
            }
        } catch (SQLException e) {
            System.err.println("Error checking workspaces in database: " + e.getMessage());
        }
    }

    public static void writeDummyDataToDatabase() {
        List<Workspace> dummyWorkspaces = List.of(
                new Workspace(1, "Open Space", 5.0),
                new Workspace(2, "Private Desk", 8.0),
                new Workspace(3, "Private Room", 20.0),
                new Workspace(4, "Meeting Room", 30.0),
                new Workspace(5, "Event Space", 50.0)
        );

        String query = "INSERT INTO workspaces (id, type, price) VALUES (?, ?, ?)";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            for (Workspace workspace : dummyWorkspaces) {
                preparedStatement.setInt(1, workspace.getId());
                preparedStatement.setString(2, workspace.getType());
                preparedStatement.setDouble(3, workspace.getPrice());
                preparedStatement.executeUpdate();
            }
            System.out.println("Dummy data added to database.");
        } catch (SQLException e) {
            System.err.println("Error writing dummy data to database: " + e.getMessage());
        }
    }

    public static void initializeAdminUser() {
        String query = "INSERT IGNORE INTO users (username, password) VALUES ('admin', 'admin123')";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.executeUpdate();
            System.out.println("Default admin user initialized.");
        } catch (Exception e) {
            System.err.println("Error initializing admin user: " + e.getMessage());
        }
    }

    public static boolean validateUser(String username, String password, String userType) {
        String query = "SELECT password FROM users WHERE username = ?";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String storedPassword = resultSet.getString("password");
                return storedPassword.equals(password) &&
                        ((userType.equals("admin") && username.equals("admin")) ||
                                (userType.equals("customer") && !username.equals("admin")));
            }
        } catch (Exception e) {
            System.err.println("Error during login: " + e.getMessage());
        }
        return false;
    }

    public static boolean registerUser(String username, String password) {
        String query = "INSERT IGNORE INTO users (username, password) VALUES (?, ?)";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            int rowsAffected = preparedStatement.executeUpdate();

            return rowsAffected > 0;
        } catch (Exception e) {
            System.err.println("Error registering user: " + e.getMessage());
        }
        return false;
    }
}
