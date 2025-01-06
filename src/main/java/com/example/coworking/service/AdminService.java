package com.example.coworking.service;

import com.example.coworking.InvalidWorkspaceException;
import com.example.coworking.model.Reservation;
import com.example.coworking.model.Workspace;
import com.example.coworking.util.Action;
import com.example.coworking.util.PrintUtil;

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

        Optional<Workspace> toRemove = WORKSPACES.stream()
                .filter(workspace -> workspace.getId() == id)
                .findFirst();

        if (toRemove.isPresent()) {
            WORKSPACES.remove(toRemove.get());
            WORKSPACE_COUNTS.put(toRemove.get().getType(), WORKSPACE_COUNTS.get(toRemove.get().getType()) - 1);
            System.out.println("Workspace removed successfully!");
        } else {
            System.out.println("Workspace ID not found.");
        }
    }

    public void viewAllReservations() {
        if (RESERVATIONS.isEmpty()) {
            System.out.println("No reservations found.\n");
        } else {
            PrintUtil.printList(RESERVATIONS);
            System.out.println();
        }
    }

    public void applyDiscount(Action<Workspace> action) {
        WORKSPACES.forEach(action::apply);
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

