package com.example.coworking.service;

import com.example.coworking.util.DBUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.InputMismatchException;
import java.util.Scanner;

public class CustomerService {

    public void browseSpaces() {
        String query = "SELECT type, COUNT(*) AS available_count FROM workspaces " +
                "WHERE id NOT IN (SELECT workspace_id FROM reservations) GROUP BY type";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            System.out.println("\nAvailable Workspaces:");

            if (!resultSet.isBeforeFirst()) {
                System.out.println("No available workspaces at the moment.");
                return;
            }

            while (resultSet.next()) {
                System.out.printf("%s: %d available%n", resultSet.getString("type"), resultSet.getInt("available_count"));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving available workspaces: " + e.getMessage());
        }
    }

    public void makeReservation(Scanner scanner) {
        System.out.print("Enter your name (letters and spaces): ");
        String customerName = scanner.nextLine();
        if (!customerName.matches("^[A-Za-z ]+$")) {
            System.out.println("Invalid name. Only letters and spaces are allowed.");
            return;
        }

        System.out.print("Enter workspace type to book (Open Space/ Private Desk/ Private Room/ Meeting Room/ Event Space): ");
        String type = scanner.nextLine();

        System.out.print("Enter date (YYYY-MM-DD): ");
        String date = scanner.nextLine();
        if (!isValidDate(date)) {
            System.out.println("Invalid date format or date is in the past. Please try again.");
            return;
        }

        System.out.print("Enter start hour (8-19): ");
        int startHour = getValidatedIntInput(scanner);
        System.out.print("Enter end hour (9-20): ");
        int endHour = getValidatedIntInput(scanner);

        if (startHour < 8 || startHour >= endHour || endHour > 20) {
            System.out.println("Invalid time range. Please try again.");
            return;
        }

        try (Connection connection = DBUtil.getConnection()) {
            System.out.println("Database connection established.");

            String findWorkspaceQuery = "SELECT id, price FROM workspaces WHERE type = ? AND status = 'available' LIMIT 1";
            int workspaceId = -1;
            double pricePerHour = 0.0;

            try (PreparedStatement preparedStatement = connection.prepareStatement(findWorkspaceQuery)) {
                preparedStatement.setString(1, type);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (!resultSet.next()) {
                        System.out.println("No available workspaces of the selected type.");
                        return;
                    }

                    workspaceId = resultSet.getInt("id");
                    pricePerHour = resultSet.getDouble("price");
                }
            }

            if (workspaceId == -1) {
                System.out.println("Workspace not found.");
                return;
            }

            int durationHours = endHour - startHour;
            double totalPrice = durationHours * pricePerHour;

            String insertReservationQuery = "INSERT INTO reservations (workspace_id, type, customer_name, date, start_time, end_time, total_price) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement insertStatement = connection.prepareStatement(insertReservationQuery)) {
                insertStatement.setInt(1, workspaceId);
                insertStatement.setString(2, type);
                insertStatement.setString(3, customerName);
                insertStatement.setDate(4, java.sql.Date.valueOf(date));
                insertStatement.setTime(5, java.sql.Time.valueOf(String.format("%02d:00:00", startHour)));
                insertStatement.setTime(6, java.sql.Time.valueOf(String.format("%02d:00:00", endHour)));
                insertStatement.setDouble(7, totalPrice);
                insertStatement.executeUpdate();
                System.out.println("Reservation successfully added to the database.");
            }

            String updateWorkspaceStatusQuery = "UPDATE workspaces SET status = 'reserved' WHERE id = ?";

            try (PreparedStatement updateStatement = connection.prepareStatement(updateWorkspaceStatusQuery)) {
                updateStatement.setInt(1, workspaceId);
                updateStatement.executeUpdate();
            }

            System.out.println("Reservation successful!");
            System.out.printf("You have reserved the space for %d hours. Total price: $%.2f%n", durationHours, totalPrice);
        } catch (Exception e) {
            System.err.println("Error making reservation: " + e.getMessage());
        }
    }

    public void viewCustomerReservations(String customerName) {
        String query = "SELECT r.id, w.type AS workspace_type, r.date, r.start_time, r.end_time, r.total_price " +
                "FROM reservations r " +
                "JOIN workspaces w ON r.workspace_id = w.id " +
                "WHERE r.customer_name = ?";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, customerName);
            ResultSet resultSet = preparedStatement.executeQuery();

            System.out.println("\nYour Reservations:");

            if (!resultSet.isBeforeFirst()) {
                System.out.println("No reservations found.");
                return;
            }

            while (resultSet.next()) {
                System.out.printf("Reservation ID: %d, Workspace Type: %s, Date: %s, Start: %s, End: %s, Price: $%.2f%n",
                        resultSet.getInt("id"),
                        resultSet.getString("workspace_type"),
                        resultSet.getDate("date"),
                        resultSet.getTime("start_time"),
                        resultSet.getTime("end_time"),
                        resultSet.getDouble("total_price"));
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving reservations: " + e.getMessage());
        }
    }

    public void cancelReservation(Scanner scanner) {
        System.out.print("Enter reservation ID to cancel: ");
        int reservationId = getValidatedIntInput(scanner);

        try (Connection connection = DBUtil.getConnection()) {
            String findReservationQuery = "SELECT workspace_id FROM reservations WHERE id = ?";
            int workspaceId;

            try (PreparedStatement findStatement = connection.prepareStatement(findReservationQuery)) {
                findStatement.setInt(1, reservationId);
                ResultSet resultSet = findStatement.executeQuery();

                if (!resultSet.next()) {
                    System.out.println("Reservation ID not found.");
                    return;
                }

                workspaceId = resultSet.getInt("workspace_id");
            }

            String deleteReservationQuery = "DELETE FROM reservations WHERE id = ?";
            try (PreparedStatement deleteStatement = connection.prepareStatement(deleteReservationQuery)) {
                deleteStatement.setInt(1, reservationId);
                deleteStatement.executeUpdate();
            }

            String updateWorkspaceStatusQuery = "UPDATE workspaces SET status = 'available' WHERE id = ?";
            try (PreparedStatement updateStatement = connection.prepareStatement(updateWorkspaceStatusQuery)) {
                updateStatement.setInt(1, workspaceId);
                updateStatement.executeUpdate();
            }

            System.out.println("Reservation canceled, and workspace is now available.");
        } catch (Exception e) {
            System.err.println("Error canceling reservation: " + e.getMessage());
        }
    }

    private boolean isValidDate(String date) {
        try {
            LocalDate inputDate = LocalDate.parse(date);
            return !inputDate.isBefore(LocalDate.now());
        } catch (Exception e) {
            return false;
        }
    }

    private int getValidatedIntInput(Scanner scanner) {
        while (true) {
            try {
                return scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.print("Invalid input. Please enter a number: ");
                scanner.nextLine();
            }
        }
    }
}

