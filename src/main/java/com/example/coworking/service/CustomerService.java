package com.example.coworking.service;

import com.example.coworking.model.Reservation;
import com.example.coworking.model.Workspace;
import com.example.coworking.util.PrintUtil;

import java.time.LocalDate;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class CustomerService {
    private final List<Workspace> WORKSPACES;
    private final Map<String, Integer> WORKSPACE_COUNTS;
    private final List<Reservation> RESERVATIONS;
    private int nextReservationId;

    public CustomerService(List<Workspace> WORKSPACES, Map<String, Integer> WORKSPACE_COUNTS, List<Reservation> RESERVATIONS, int nextReservationId) {
        this.WORKSPACES = WORKSPACES;
        this.WORKSPACE_COUNTS = WORKSPACE_COUNTS;
        this.RESERVATIONS = RESERVATIONS;
        this.nextReservationId = nextReservationId;
    }

    public void browseSpaces() {
        System.out.println("\nAvailable Workspaces:");
        /*for (String type : WORKSPACE_COUNTS.keySet()) {
            System.out.println(type + ": " + WORKSPACE_COUNTS.get(type) + " available");
        }*/
        PrintUtil.printMap(WORKSPACE_COUNTS, "%s: %d available%n");
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
        if (!WORKSPACE_COUNTS.containsKey(type)) {
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

        if (WORKSPACE_COUNTS.getOrDefault(type, 0) > 0) {
            Workspace reservedWorkspace = null;
            for (Workspace workspace : WORKSPACES) {
                if (workspace.getType().equals(type)) {
                    reservedWorkspace = workspace;
                    break;
                }
            }

            if (reservedWorkspace != null) {
                int durationHours = endHour - startHour;
                double totalPrice = durationHours * reservedWorkspace.getPrice();

                RESERVATIONS.add(new Reservation(
                        nextReservationId++,
                        reservedWorkspace.getId(),
                        type,
                        customerName,
                        date,
                        startHour + ":00",
                        endHour + ":00",
                        totalPrice
                ));
                WORKSPACE_COUNTS.put(type, WORKSPACE_COUNTS.get(type) - 1);
                System.out.println("Reservation successful!");
                System.out.printf("You have reserved the space for %d hours. Total price: $%.2f%n%n", durationHours, totalPrice);
            }
        } else {
            System.out.println("No available workspaces of this type.");
        }
    }

    public void viewCustomerReservations(String customerName) {
        for (Reservation reservation : RESERVATIONS) {
            if (reservation.getCustomerName().equals(customerName)) {
                System.out.println(reservation);
            }
        }
    }

    public void cancelReservation(Scanner scanner) {
        System.out.print("Enter reservation ID to cancel: ");
        int reservationId = getValidatedIntInput(scanner);

        Reservation toCancel = null;
        for (Reservation reservation : RESERVATIONS) {
            if (reservation.getId() == reservationId) {
                toCancel = reservation;
                break;
            }
        }

        if (toCancel != null) {
            RESERVATIONS.remove(toCancel);
            for (Workspace workspace : WORKSPACES) {
                if (workspace.getId() == toCancel.getWorkspaceId()) {
                    WORKSPACE_COUNTS.put(workspace.getType(), WORKSPACE_COUNTS.get(workspace.getType()) + 1);
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

