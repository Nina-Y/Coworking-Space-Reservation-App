package com.example.coworking.io;

import com.example.coworking.model.Reservation;
import com.example.coworking.model.Workspace;
import com.example.coworking.util.DBUtil;
import org.hibernate.Session;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;

public class FileUtil {

    public static void storeStateInFile(String workspacesFile, String reservationsFile) {
        try (Session session = DBUtil.getSessionFactory().openSession()) {

            List<Workspace> workspaces = session.createQuery("FROM Workspace", Workspace.class).list();
            try (PrintWriter workspacesWriter = new PrintWriter(new FileWriter(workspacesFile))) {
                for (Workspace workspace : workspaces) {
                    workspacesWriter.printf("%d,%s,%.2f,%s%n",
                            workspace.getId(), workspace.getType(), workspace.getPrice(), workspace.getStatus());
                }
            }

            List<Reservation> reservations = session.createQuery("FROM Reservation", Reservation.class).list();
            try (PrintWriter reservationsWriter = new PrintWriter(new FileWriter(reservationsFile))) {
                for (Reservation reservation : reservations) {
                    reservationsWriter.printf("%d,%d,%s,%s,%s,%s,%s,%.2f%n",
                            reservation.getId(),
                            reservation.getWorkspaceId().getId(),  // Fetch the ID of the associated workspace
                            reservation.getType(),
                            reservation.getCustomerName(),
                            reservation.getDate(),
                            reservation.getStartTime(),
                            reservation.getEndTime(),
                            reservation.getTotalPrice());
                }
            }

            System.out.println("State successfully saved to files.");
        } catch (Exception e) {
            System.err.println("Error saving state to file: " + e.getMessage());
        }
    }
}