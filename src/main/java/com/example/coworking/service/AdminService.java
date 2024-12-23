package com.example.coworking.service;

import com.example.coworking.model.Reservation;
import com.example.coworking.model.Workspace;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class AdminService {
    private final List<Workspace> workspaces;
    private final Map<String, Integer> workspaceCounts;
    private final List<Reservation> reservations;
    private int nextWorkspaceId;

    public AdminService(List<Workspace> workspaces, Map<String, Integer> workspaceCounts, List<Reservation> reservations, int nextWorkspaceId) {
        this.workspaces = workspaces;
        this.workspaceCounts = workspaceCounts;
        this.reservations = reservations;
        this.nextWorkspaceId = nextWorkspaceId;
    }

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

        addWorkspaceToInventory(type, price, quantity);
        System.out.println("Workspace added successfully!\n");
    }

    public void removeWorkspace(Scanner scanner) {
        System.out.print("Enter the ID of the workspace to remove: ");
        int id = getValidatedIntInput(scanner);

        Workspace toRemove = null;
        for (Workspace workspace : workspaces) {
            if (workspace.getId() == id) {
                toRemove = workspace;
                break;
            }
        }

        if (toRemove != null) {
            workspaces.remove(toRemove);
            workspaceCounts.put(toRemove.getType(), workspaceCounts.get(toRemove.getType()) - 1);
            System.out.println("Workspace removed successfully!");
        } else {
            System.out.println("Workspace ID not found.");
        }
        System.out.println();
    }

    public void viewAllReservations() {
        if (reservations.isEmpty()) {
            System.out.println("No reservations found.\n");
        } else {
            for (Reservation reservation : reservations) {
                System.out.println(reservation);
            }
            System.out.println();
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

    public void addWorkspaceToInventory(String type, double price, int quantity) {
        for (int i = 0; i < quantity; i++) {
            workspaces.add(new Workspace(nextWorkspaceId++, type, price));
        }
        workspaceCounts.put(type, workspaceCounts.getOrDefault(type, 0) + quantity);
    }
}

