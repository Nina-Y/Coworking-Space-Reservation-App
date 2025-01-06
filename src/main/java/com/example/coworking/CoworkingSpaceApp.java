package com.example.coworking;

import com.example.coworking.classLoader.CustomClassLoader;
import com.example.coworking.io.FileUtil;
import com.example.coworking.model.Reservation;
import com.example.coworking.model.Workspace;
import com.example.coworking.service.AdminService;
import com.example.coworking.service.CustomerService;

import java.util.*;

public class  CoworkingSpaceApp {

    private static final List<Workspace> WORKSPACES = new ArrayList<>();
    private static final List<Reservation> RESERVATIONS = new ArrayList<>();
    private static final Map<String, String> USER_CREDENTIALS = new HashMap<>();
    private static final Map<String, Integer> WORKSPACE_COUNTS = new HashMap<>();
    private static int nextWorkspaceId = 6;
    private static int nextReservationId = 1;
    private static final String WORKSPACES_FILE = "src/main/java/com/example/coworking/io/workspaces.txt";

    public static void main(String[] args) {

        runCustomClassLoader();

        Scanner scanner = new Scanner(System.in);

        USER_CREDENTIALS.put("admin", "admin123");

        AdminService adminService = new AdminService(WORKSPACES, WORKSPACE_COUNTS, RESERVATIONS, nextWorkspaceId);
        CustomerService customerService = new CustomerService(WORKSPACES, WORKSPACE_COUNTS, RESERVATIONS, nextReservationId);

        FileUtil.loadWorkspacesFromFile(WORKSPACES, WORKSPACE_COUNTS, WORKSPACES_FILE);

        boolean isRunning = true;
        while (isRunning) {
            System.out.println("""
                \nWelcome to Coworking Space Reservation App
                1. Admin Login
                2. Customer Login
                3. Customer Registration
                4. Store state in a file
                5. Exit
                Choose an option:
                """.trim());

            int choice = getValidatedIntInput(scanner);
            scanner.nextLine();

            switch (choice) {
                case 1:
                    if (login(scanner, "admin")) {
                        adminMenu(scanner, adminService);
                    } else {
                        System.out.println("Invalid credentials. Access denied.");
                    }
                    break;
                case 2:
                    if (login(scanner, "customer")) {
                        customerMenu(scanner, customerService);
                    } else {
                        System.out.println("Invalid credentials. Access denied.");
                    }
                    break;
                case 3:
                    registerCustomer(scanner);
                    break;
                case 4:
                    manageState(scanner);
                    break;
                case 5:
                    FileUtil.saveWorkspacesToFile(WORKSPACES_FILE, WORKSPACES);
                    System.out.println("Goodbye!");
                    isRunning = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    public static void runCustomClassLoader() {
        try {
            String classDir = "./";
            CustomClassLoader loader = new CustomClassLoader(classDir);

            Class<?> classToLoad = loader.loadClass("com.example.coworking.classLoader.ClassForCustomClassLoader");

            Object instance = classToLoad.getDeclaredConstructor().newInstance();

            classToLoad.getMethod("showWelcomeMessage").invoke(instance);
        }   catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void manageState(Scanner scanner) {
        System.out.println("""
            \nState Menu
            1. Save Current State (file)
            2. View Current State (ArrayList)
            3. Back to Main Menu
            Choose an option:
            """.trim());

        int choice = getValidatedIntInput(scanner);
        scanner.nextLine();

        switch (choice) {
            case 1 -> {
                FileUtil.saveWorkspacesToFile(WORKSPACES_FILE, WORKSPACES);
                System.out.println("Current state saved successfully!");
            }
            case 2 -> {
                System.out.println("Current State of Workspaces:");
                WORKSPACES.forEach(System.out::println);
            }
            case 3 -> System.out.println("Returning to Main Menu");
            default -> System.out.println("Invalid choice. Please try again.");
        }
    }

    private static boolean login(Scanner scanner, String userType) {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        return USER_CREDENTIALS.containsKey(username) && USER_CREDENTIALS.get(username).equals(password) &&
                ((userType.equals("admin") && username.equals("admin")) ||
                 (userType.equals("customer") && !username.equals("admin")));
    }

    private static void registerCustomer(Scanner scanner) {
        System.out.print("Enter a username (letters, numbers and underscores): ");
        String username = scanner.nextLine();
        if (!username.matches("^[A-Za-z0-9_]+$")) {
            System.out.println("Invalid username. Only letters, numbers and underscores are allowed.");
            return;
        }

        if (USER_CREDENTIALS.containsKey(username)) {
            System.out.println("Username already exists. Please choose a different username.");
            return;
        }

        System.out.print("Enter a password (min 6 characters): ");
        String password = scanner.nextLine();
        if (password.length() < 6) {
            System.out.println("Password must be at least 6 characters long.");
            return;
        }
        USER_CREDENTIALS.put(username, password);
        System.out.println("Registration successful! You can now log in as a customer\n.");
    }

    private static void adminMenu(Scanner scanner, AdminService adminService) {
        System.out.println("\nAdmin Menu");

        boolean isRunning = true;
        while (isRunning) {
            System.out.println("""
                \n1. Add a new coworking space
                2. Remove a coworking space
                3. View all RESERVATIONS
                4. Set 10% discount for all spaces
                5. Back to Main Menu
                Choose an option:
                """.trim());

            int choice = getValidatedIntInput(scanner);
            scanner.nextLine();

            switch (choice) {
                case 1:
                    adminService.addWorkspace(scanner);
                    break;
                case 2:
                    adminService.removeWorkspace(scanner);
                    break;
                case 3:
                    adminService.viewAllReservations();
                    break;
                case 4:
                    adminService.applyDiscount(workspace -> workspace.setPrice(workspace.getPrice() * 0.9));
                    break;
                    case 5:
                    isRunning = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void customerMenu(Scanner scanner, CustomerService customerService) {
        System.out.println("\nCustomer Menu");

        boolean isRunning = true;
        while (isRunning) {
            System.out.println("""
                \n1. Browse available spaces
                2. Make a reservation
                3. View my RESERVATIONS
                4. Cancel a reservation
                5. Back to Main Menu
                Choose an option:
                """.trim());

            int choice = getValidatedIntInput(scanner);
            scanner.nextLine();

            switch (choice) {
                case 1:
                    customerService.browseSpaces();
                    break;
                case 2:
                    customerService.makeReservation(scanner);
                    break;
                case 3:
                    System.out.print("Enter your name: ");
                    String customerName = scanner.nextLine();
                    customerService.viewCustomerReservations(customerName);
                    break;
                case 4:
                    customerService.cancelReservation(scanner);
                    break;
                case 5:
                    isRunning = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
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
}
