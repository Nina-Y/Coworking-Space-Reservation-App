package com.example.coworking.service;

import com.example.coworking.model.Reservation;
import com.example.coworking.model.Workspace;

import java.time.LocalDate;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class CustomerService {
    private final List<Workspace> workspaces;
    private final Map<String, Integer> workspaceCounts;
    private final List<Reservation> reservations;
    private int nextReservationId;

    public CustomerService(List<Workspace> workspaces, Map<String, Integer> workspaceCounts, List<Reservation> reservations, int nextReservationId) {
        this.workspaces = workspaces;
        this.workspaceCounts = workspaceCounts;
        this.reservations = reservations;
        this.nextReservationId = nextReservationId;
    }

    public void browseSpaces() {
        System.out.println("\nAvailable Workspaces:");
        for (String type : workspaceCounts.keySet()) {
            System.out.println(type + ": " + workspaceCounts.get(type) + " available");
        }
        System.out.println();
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
        if (!workspaceCounts.containsKey(type)) {
            System.out.println("Invalid workspace type. Please choose a valid option.");
            return;
        }

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

        if (workspaceCounts.getOrDefault(type, 0) > 0) {
            Workspace reservedWorkspace = null;
            for (Workspace workspace : workspaces) {
                if (workspace.getType().equals(type)) {
                    reservedWorkspace = workspace;
                    break;
                }
            }

            if (reservedWorkspace != null) {
                int durationHours = endHour - startHour;
                double totalPrice = durationHours * reservedWorkspace.getPrice();

                reservations.add(new Reservation(
                        nextReservationId++,
                        reservedWorkspace.getId(),
                        type,
                        customerName,
                        date,
                        startHour + ":00",
                        endHour + ":00",
                        totalPrice
                ));
                workspaceCounts.put(type, workspaceCounts.get(type) - 1);
                System.out.println("Reservation successful!");
                System.out.printf("You have reserved the space for %d hours. Total price: $%.2f%n%n", durationHours, totalPrice);
            }
        } else {
            System.out.println("No available workspaces of this type.");
        }
    }

    public void viewCustomerReservations(String customerName) {
        for (Reservation reservation : reservations) {
            if (reservation.getCustomerName().equals(customerName)) {
                System.out.println(reservation);
            }
        }
    }

    public void cancelReservation(Scanner scanner) {
        System.out.print("Enter reservation ID to cancel: ");
        int reservationId = getValidatedIntInput(scanner);

        Reservation toCancel = null;
        for (Reservation reservation : reservations) {
            if (reservation.getId() == reservationId) {
                toCancel = reservation;
                break;
            }
        }

        if (toCancel != null) {
            reservations.remove(toCancel);
            for (Workspace workspace : workspaces) {
                if (workspace.getId() == toCancel.getWorkspaceId()) {
                    workspaceCounts.put(workspace.getType(), workspaceCounts.get(workspace.getType()) + 1);
                }
            }
            System.out.println("Reservation cancelled successfully!");
        } else {
            System.out.println("Reservation ID not found.");
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

