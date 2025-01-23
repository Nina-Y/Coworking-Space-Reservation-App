package com.example.coworking.service;

import com.example.coworking.model.Reservation;
import com.example.coworking.model.Workspace;
import com.example.coworking.util.DBUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.sql.Time;
import java.time.LocalDate;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class CustomerService {

    public void browseSpaces() {
        try (Session session = DBUtil.getSessionFactory().openSession()) {
            List<Object[]> availableSpaces = session.createQuery(
                    "SELECT w.type, COUNT(w) FROM Workspace w WHERE w.status = 'available' GROUP BY w.type", Object[].class
            ).list();

            if (availableSpaces.isEmpty()) {
                System.out.println("No available workspaces at the moment.");
            } else {
                System.out.println("\nAvailable Workspaces:");
                availableSpaces.forEach(space -> System.out.printf("%s: %d available%n", space[0], space[1]));
            }
        } catch (Exception e) {
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

        try (Session session = DBUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            Workspace workspace = session.createQuery(
                    "FROM Workspace w WHERE w.type = :type AND w.status = 'available'", Workspace.class
            ).setParameter("type", type).setMaxResults(1).uniqueResult();

            if (workspace == null) {
                System.out.println("No available workspaces of the selected type.");
                return;
            }

            double pricePerHour = workspace.getPrice();
            int durationHours = endHour - startHour;
            double totalPrice = durationHours * pricePerHour;

            Reservation reservation = new Reservation(
                    workspace, type, customerName, date,
                    Time.valueOf(String.format("%02d:00:00", startHour)),
                    Time.valueOf(String.format("%02d:00:00", endHour)),
                    totalPrice
            );
            session.save(reservation);

            workspace.setStatus("reserved");
            session.update(workspace);

            transaction.commit();

            System.out.println("Reservation successful!");
            System.out.printf("You have reserved the space for %d hours. Total price: $%.2f%n", durationHours, totalPrice);
        } catch (Exception e) {
            System.err.println("Error making reservation: " + e.getMessage());
        }
    }

    public void viewCustomerReservations(String customerName) {
        try (Session session = DBUtil.getSessionFactory().openSession()) {
            List<Reservation> reservations = session.createQuery(
                    "FROM Reservation r WHERE r.customerName = :customerName", Reservation.class
            ).setParameter("customerName", customerName).list();

            if (reservations.isEmpty()) {
                System.out.println("No reservations found for " + customerName + ".");
            } else {
                System.out.println("\nReservations for " + customerName + ":");
                for (Reservation reservation : reservations) {
                    System.out.println(reservation);
                }
            }
        } catch (Exception e) {
            System.err.println("Error retrieving reservations: " + e.getMessage());
        }
    }

    public void cancelReservation(Scanner scanner) {
        System.out.print("Enter reservation ID to cancel: ");
        int reservationId = getValidatedIntInput(scanner);

        try (Session session = DBUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            Reservation reservation = session.get(Reservation.class, reservationId);
            if (reservation == null) {
                System.out.println("Reservation ID not found.");
                return;
            }

            Workspace workspace = reservation.getWorkspaceId();
            workspace.setStatus("available");
            session.update(workspace);

            session.delete(reservation);
            transaction.commit();

            System.out.println("Reservation canceled, and workspace is now available.");
        } catch (Exception e) {
            System.err.println("Error canceling reservation: " + e.getMessage());
        }
    }

    private boolean isValidDate(String date) {
        try {
            return !LocalDate.parse(date).isBefore(LocalDate.now());
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