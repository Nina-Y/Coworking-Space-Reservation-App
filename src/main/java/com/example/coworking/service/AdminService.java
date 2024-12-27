package com.example.coworking.service;

import com.example.coworking.InvalidWorkspaceException;
import com.example.coworking.model.Reservation;
import com.example.coworking.model.Workspace;

import java.util.*;

public class AdminService {
    private final List<Workspace> WORKSPACES;
    private final Map<String, Integer> WORKSPACE_COUNTS;
    private final List<Reservation> RESERVATIONS;
    private int nextWorkspaceId;

    public AdminService(List<Workspace> WORKSPACES, Map<String, Integer> WORKSPACE_COUNTS, List<Reservation> RESERVATIONS, int nextWorkspaceId) {
        this.WORKSPACES = WORKSPACES;
        this.WORKSPACE_COUNTS = WORKSPACE_COUNTS;
        this.RESERVATIONS = RESERVATIONS;
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
        for (Workspace workspace : WORKSPACES) {
            if (workspace.getId() == id) {
                toRemove = workspace;
                break;
            }
        }

        if (toRemove == null) {
            throw new InvalidWorkspaceException("Workspace with ID " + id + " does not exist.");
        }

        WORKSPACES.remove(toRemove);
        WORKSPACE_COUNTS.put(toRemove.getType(), WORKSPACE_COUNTS.get(toRemove.getType()) - 1);
        System.out.println("Workspace removed successfully!");
        System.out.println();
    }

    public void viewAllReservations() {
        if (RESERVATIONS.isEmpty()) {
            System.out.println("No reservations found.\n");
        } else {
            for (Reservation reservation : RESERVATIONS) {
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
            WORKSPACES.add(new Workspace(nextWorkspaceId++, type, price));
        }
        WORKSPACE_COUNTS.put(type, WORKSPACE_COUNTS.getOrDefault(type, 0) + quantity);
    }
}

