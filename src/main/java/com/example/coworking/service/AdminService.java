package com.example.coworking.service;

import com.example.coworking.model.Reservation;
import com.example.coworking.model.Workspace;
import com.example.coworking.util.DBUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

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
        try (Session session = DBUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            for (int i = 0; i < quantity; i++) {
                Workspace workspace = new Workspace(type, price, "available");
                session.save(workspace);
            }

            transaction.commit();
        } catch (Exception e) {
            System.err.println("Error adding workspaces to the database: " + e.getMessage());
        }
    }

    public void removeWorkspace(Scanner scanner) {
        System.out.print("Enter the ID of the workspace to remove: ");
        int id = getValidatedIntInput(scanner);

        try (Session session = DBUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            Workspace workspace = session.get(Workspace.class, id);
            if (workspace != null) {
                session.delete(workspace);
                transaction.commit();
                System.out.println("Workspace removed successfully!");
            } else {
                System.out.println("Workspace ID not found.");
            }
        } catch (Exception e) {
            System.err.println("Error removing workspace from the database: " + e.getMessage());
        }
    }

    public void viewAllReservations() {
        try (Session session = DBUtil.getSessionFactory().openSession()) {
            List<Reservation> reservations = session.createQuery("FROM Reservation", Reservation.class).list();

            if (reservations.isEmpty()) {
                System.out.println("No reservations found.");
            } else {
                reservations.forEach(System.out::println);
            }
        } catch (Exception e) {
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