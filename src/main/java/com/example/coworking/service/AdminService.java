package com.example.coworking.service;

import com.example.coworking.util.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class AdminService {

    public void addWorkspace(Scanner scanner) {
        System.out.print("Enter workspace type (Open Space/ Private Desk/ Private Room/ Meeting Room/ Event Space): ");
        String type = scanner.nextLine();

        double price;
        while (true) {
            try {
                System.out.print("Enter price per hour: ");
                price = scanner.nextDouble();
                scanner.nextLine();
                if (price <= 0) {
                    System.out.println("Price must be greater than 0. Please try again.");
                } else {
                    break;
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid price (numeric value).");
                scanner.nextLine();
            }
        }

        System.out.print("Enter quantity: ");
        int quantity = getValidatedIntInput(scanner);
        scanner.nextLine();

        addWorkspacesToDB(type, price, quantity);
        System.out.println("Workspace(s) added successfully!\n");
    }

    private void addWorkspacesToDB(String type, double price, int quantity) {
        String query = "INSERT INTO workspaces (type, price) VALUES (?, ?)";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            for (int i = 0; i < quantity; i++) {
                preparedStatement.setString(1, type);
                preparedStatement.setDouble(2, price);
                preparedStatement.executeUpdate();
            }

        } catch (SQLException e) {
            System.err.println("Error adding workspaces to the database: " + e.getMessage());
        }
    }

    public void removeWorkspace(Scanner scanner) {
        System.out.print("Enter the ID of the workspace to remove: ");
        int id = getValidatedIntInput(scanner);

        String query = "DELETE FROM workspaces WHERE id = ?";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, id);
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Workspace removed successfully!");
            } else {
                System.out.println("Workspace ID not found.");
            }

        } catch (SQLException e) {
            System.err.println("Error removing workspace from the database: " + e.getMessage());
        }
    }

    public void viewAllReservations() {
        String query = "SELECT r.id, r.workspace_id, r.type, r.customer_name, r.date, r.start_time, r.end_time, r.total_price, w.type AS workspace_type " +
                "FROM reservations r " +
                "JOIN workspaces w ON r.workspace_id = w.id";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            if (!resultSet.isBeforeFirst()) {
                System.out.println("No reservations found.");
                return;
            }

            while (resultSet.next()) {
                System.out.printf("Reservation ID: %d, Workspace ID: %d, Workspace Type: %s, Customer: %s, Date: %s, Start: %s, End: %s, Price: $%.2f%n",
                        resultSet.getInt("id"),
                        resultSet.getInt("workspace_id"),
                        resultSet.getString("workspace_type"),
                        resultSet.getString("customer_name"),
                        resultSet.getDate("date"),
                        resultSet.getTime("start_time"),
                        resultSet.getTime("end_time"),
                        resultSet.getDouble("total_price"));
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving reservations from the database: " + e.getMessage());
        }
    }

    private int getValidatedIntInput(Scanner scanner) {
        while (true) {
            try {
                return scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number: ");
                scanner.nextLine();
            }
        }
    }
}

