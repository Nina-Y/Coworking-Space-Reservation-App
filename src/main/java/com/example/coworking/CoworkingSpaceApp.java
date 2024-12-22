package com.example.coworking;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class CoworkingSpaceApp {

    private static List<Workspace> workspaces = new ArrayList<>();
    private static List<Reservation> reservations = new ArrayList<>();
    private static Map<String, String> userCredentials = new HashMap<>();
    private static Map<String, Integer> workspaceCounts = new HashMap<>();
    private static int nextWorkspaceId = 1;
    private static int nextReservationId = 1;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        userCredentials.put("admin", "admin123");

        addWorkspaceToInventory("Open Space", 5.0, 5);
        addWorkspaceToInventory("Private Desk", 8.0, 3);
        addWorkspaceToInventory("Private Room", 20.0, 2);
        addWorkspaceToInventory("Meeting Room", 30.0, 1);
        addWorkspaceToInventory("Event Space", 50.0, 1);

        while (true) {
            System.out.println("\nWelcome to Coworking Space Reservation App");
            System.out.println("1. Admin Login");
            System.out.println("2. Customer Login");
            System.out.println("3. Customer Registration");
            System.out.println("4. Exit");
            System.out.print("Choose an option: ");

            int choice = getValidatedIntInput(scanner);
            scanner.nextLine();

            switch (choice) {
                case 1:
                    if (login(scanner, "admin")) {
                        adminMenu(scanner);
                    } else {
                        System.out.println("Invalid credentials. Access denied.");
                    }
                    break;
                case 2:
                    if (login(scanner, "customer")) {
                        customerMenu(scanner);
                    } else {
                        System.out.println("Invalid credentials. Access denied.");
                    }
                    break;
                case 3:
                    registerCustomer(scanner);
                    break;
                case 4:
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static boolean login(Scanner scanner, String userType) {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        if (userCredentials.containsKey(username) && userCredentials.get(username).equals(password)) {
            return (userType.equals("admin") && username.equals("admin")) ||
                    (userType.equals("customer") && !username.equals("admin"));
        }
        return false;
    }

    private static void registerCustomer(Scanner scanner) {
        System.out.print("Enter a username (letters, numbers and underscores): ");
        String username = scanner.nextLine();
        if (!username.matches("^[A-Za-z0-9_]+$")) {
            System.out.println("Invalid username. Only letters, numbers and underscores are allowed.");
            return;
        }

        if (userCredentials.containsKey(username)) {
            System.out.println("Username already exists. Please choose a different username.");
            return;
        }

        System.out.print("Enter a password (min 6 characters): ");
        String password = scanner.nextLine();
        if (password.length() < 6) {
            System.out.println("Password must be at least 6 characters long.");
            return;
        }
        userCredentials.put(username, password);
        System.out.println("Registration successful! You can now log in as a customer.");
    }

    private static void adminMenu(Scanner scanner) {
        System.out.println("\nAdmin Menu");
        while (true) {
            System.out.println("1. Add a new coworking space");
            System.out.println("2. Remove a coworking space");
            System.out.println("3. View all reservations");
            System.out.println("4. Back to Main Menu");
            System.out.print("Choose an option: ");

            int choice = getValidatedIntInput(scanner);
            scanner.nextLine();

            switch (choice) {
                case 1:
                    addWorkspace(scanner);
                    break;
                case 2:
                    removeWorkspace(scanner);
                    break;
                case 3:
                    viewAllReservations();
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void customerMenu(Scanner scanner) {
        System.out.println("\nCustomer Menu");
        while (true) {
            System.out.println("1. Browse available spaces");
            System.out.println("2. Make a reservation");
            System.out.println("3. View my reservations");
            System.out.println("4. Cancel a reservation");
            System.out.println("5. Back to Main Menu");
            System.out.print("Choose an option: ");

            int choice = getValidatedIntInput(scanner);
            scanner.nextLine();

            switch (choice) {
                case 1:
                    browseSpaces();
                    break;
                case 2:
                    makeReservation(scanner);
                    break;
                case 3:
                    viewCustomerReservations(scanner);
                    break;
                case 4:
                    cancelReservation(scanner);
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void addWorkspace(Scanner scanner) {
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

    private static void removeWorkspace(Scanner scanner) {
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

    private static void viewAllReservations() {
        if (reservations.isEmpty()) {
            System.out.println("No reservations found.\n");
        } else {
            for (Reservation reservation : reservations) {
                System.out.println(reservation);
            }
            System.out.println();
        }
    }

    private static void browseSpaces() {
        System.out.println("\nAvailable Workspaces:");
        for (String type : workspaceCounts.keySet()) {
            System.out.println(type + ": " + workspaceCounts.get(type) + " available");
        }
        System.out.println();
    }

    private static void makeReservation(Scanner scanner) {
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
        scanner.nextLine();


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

    private static boolean isValidDate(String date) {
        try {
            LocalDate inputDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
            return !inputDate.isBefore(LocalDate.now());
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private static int getValidatedIntInput(Scanner scanner) {
        while (true) {
            try {
                return scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number: ");
                scanner.nextLine();
            }
        }
    }

    private static void viewCustomerReservations(Scanner scanner) {
        System.out.print("Enter your name: ");
        String customerName = scanner.nextLine();

        for (Reservation reservation : reservations) {
            if (reservation.getCustomerName().equals(customerName)) {
                System.out.println(reservation);
            }
        }
    }

    private static void cancelReservation(Scanner scanner) {
        System.out.print("Enter reservation ID to cancel: ");
        int reservationId = getValidatedIntInput(scanner);
        scanner.nextLine();

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

    private static void addWorkspaceToInventory(String type, double price, int quantity) {
        for (int i = 0; i < quantity; i++) {
            workspaces.add(new Workspace(nextWorkspaceId++, type, price));
        }
        workspaceCounts.put(type, workspaceCounts.getOrDefault(type, 0) + quantity);
    }
}
