package com.example.coworking.util;

import com.example.coworking.model.Workspace;
import com.example.coworking.model.Reservation;
import com.example.coworking.model.User;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class DBUtil {

    public static final SessionFactory sessionFactory;

    static {
        try {
            sessionFactory = new Configuration()
                    .configure("hibernate.cfg.xml")
                    .addAnnotatedClass(Workspace.class)
                    .addAnnotatedClass(Reservation.class)
                    .addAnnotatedClass(User.class)
                    .buildSessionFactory();
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError("Failed to initialize Hibernate SessionFactory: " + ex.getMessage());
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void closeSessionFactory() {
        if (sessionFactory != null && sessionFactory.isOpen()) {
            sessionFactory.close();
            System.out.println("Hibernate SessionFactory closed.");
        }
    }

    public static void ensureWorkspacesPopulated() {
        try (var session = sessionFactory.openSession()) {
            var query = session.createQuery("SELECT COUNT(w) FROM Workspace w", Long.class);
            Long count = query.uniqueResult();
            if (count == null || count == 0) {
                writeDummyDataToDatabase();
            }
        }
    }

    public static void writeDummyDataToDatabase() {
        var dummyWorkspaces = List.of(
                new Workspace("Open Space", 5.0, "available"),
                new Workspace("Private Desk", 8.0, "available"),
                new Workspace("Private Room", 20.0, "available"),
                new Workspace("Meeting Room", 30.0, "available"),
                new Workspace("Event Space", 50.0, "available")
        );

        try (var session = sessionFactory.openSession()) {
            var transaction = session.beginTransaction();
            for (var workspace : dummyWorkspaces) {
                session.save(workspace);
            }
            transaction.commit();
            System.out.println("Dummy data added to the database.");
        } catch (Exception e) {
            System.err.println("Error writing dummy data to database: " + e.getMessage());
        }
    }

    public static void initializeAdminUser() {
        try (var session = sessionFactory.openSession()) {
            var transaction = session.beginTransaction();
            User admin = session.get(User.class, "admin");
            if (admin == null) {
                session.save(new User("admin", "admin123"));
                System.out.println("Default admin user initialized.");
            }
            transaction.commit();
        } catch (Exception e) {
            System.err.println("Error initializing admin user: " + e.getMessage());
        }
    }

    public static boolean validateUser(String username, String password, String userType) {
        try (var session = sessionFactory.openSession()) {
            User user = session.get(User.class, username);
            if (user != null && user.getPassword().equals(password)) {
                return (userType.equals("admin") && username.equals("admin")) ||
                        (userType.equals("customer") && !username.equals("admin"));
            }
        } catch (Exception e) {
            System.err.println("Error validating user: " + e.getMessage());
        }
        return false;
    }

    public static boolean registerUser(String username, String password) {
        try (var session = sessionFactory.openSession()) {
            var transaction = session.beginTransaction();
            User existingUser = session.get(User.class, username);
            if (existingUser == null) {
                session.save(new User(username, password));
                transaction.commit();
                return true;
            }
        } catch (Exception e) {
            System.err.println("Error registering user: " + e.getMessage());
        }
        return false;
    }
}
