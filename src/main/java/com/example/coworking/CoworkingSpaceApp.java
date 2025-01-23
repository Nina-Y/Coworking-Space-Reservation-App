package com.example.coworking;

import com.example.coworking.classLoader.CustomClassLoader;
import com.example.coworking.io.FileUtil;
import com.example.coworking.service.AdminService;
import com.example.coworking.service.CustomerService;
import com.example.coworking.util.DBUtil;

import java.util.*;

public class CoworkingSpaceApp {
    private static final String WORKSPACES_FILE = "src/main/java/com/example/coworking/io/workspaces.txt";
    private static final String RESERVATIONS_FILE = "src/main/java/com/example/coworking/io/reservations.txt";

    public static void main(String[] args) {
        runCustomClassLoader();

        DBUtil.ensureWorkspacesPopulated();

        DBUtil.initializeAdminUser();

        Scanner scanner = new Scanner(System.in);

        AdminService adminService = new AdminService();
        CustomerService customerService = new CustomerService();

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
                    FileUtil.storeStateInFile(WORKSPACES_FILE, RESERVATIONS_FILE);
                    break;
                case 5:
                    FileUtil.storeStateInFile(WORKSPACES_FILE, RESERVATIONS_FILE);
                    System.out.println("Goodbye!");
                    isRunning = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }

        DBUtil.closeSessionFactory();
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

    private static boolean login(Scanner scanner, String userType) {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        return DBUtil.validateUser(username, password, userType);
    }

    private static void registerCustomer(Scanner scanner) {
        System.out.print("Enter a username (letters, numbers and underscores): ");
        String username = scanner.nextLine();
        if (!username.matches("^[A-Za-z0-9_]+$")) {
            System.out.println("Invalid username. Only letters, numbers and underscores are allowed.");
            return;
        }

        System.out.print("Enter a password (min 6 characters): ");
        String password = scanner.nextLine();
        if (password.length() < 6) {
            System.out.println("Password must be at least 6 characters long.");
            return;
        }

        if (DBUtil.registerUser(username, password)) {
            System.out.println("Registration successful! You can now log in as a customer.\n");
        } else {
            System.out.println("Username already exists. Please choose a different username.\n");
        }
    }

    private static void adminMenu(Scanner scanner, AdminService adminService) {

        boolean isRunning = true;
        while (isRunning) {
            System.out.println("""
                \nAdmin Menu
                1. Add a new coworking space
                2. Remove a coworking space
                3. View all RESERVATIONS
                4. Back to Main Menu
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
                    isRunning = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void customerMenu(Scanner scanner, CustomerService customerService) {
        boolean isRunning = true;
        while (isRunning) {
            System.out.println("""
                \nCustomer Menu
                1. Browse available spaces
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
