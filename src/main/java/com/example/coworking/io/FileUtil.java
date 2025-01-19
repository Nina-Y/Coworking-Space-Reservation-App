package com.example.coworking.io;

import com.example.coworking.util.DBUtil;
import java.io.*;
import java.sql.*;

public class FileUtil {

    public static void storeStateInFile(String workspacesFile, String reservationsFile) {
        try (Connection connection = DBUtil.getConnection()) {
            try (PreparedStatement workspacesStmt = connection.prepareStatement("SELECT id, type, price, status FROM workspaces");
                 ResultSet workspacesRs = workspacesStmt.executeQuery();
                 PrintWriter workspacesWriter = new PrintWriter(new FileWriter(workspacesFile))) {

                while (workspacesRs.next()) {
                    int id = workspacesRs.getInt("id");
                    String type = workspacesRs.getString("type");
                    double price = workspacesRs.getDouble("price");
                    String status = workspacesRs.getString("status");
                    workspacesWriter.printf("%d,%s,%.2f,%s%n", id, type, price, status);
                }
            }

            try (PreparedStatement reservationsStmt = connection.prepareStatement("SELECT * FROM reservations");
                 ResultSet reservationsRs = reservationsStmt.executeQuery();
                 PrintWriter reservationsWriter = new PrintWriter(new FileWriter(reservationsFile))) {

                while (reservationsRs.next()) {
                    int id = reservationsRs.getInt("id");
                    int workspaceId = reservationsRs.getInt("workspace_id");
                    String type = reservationsRs.getString("type");
                    String customerName = reservationsRs.getString("customer_name");
                    Date date = reservationsRs.getDate("date");
                    Time startTime = reservationsRs.getTime("start_time");
                    Time endTime = reservationsRs.getTime("end_time");
                    double totalPrice = reservationsRs.getDouble("total_price");

                    reservationsWriter.printf("%d,%d,%s,%s,%s,%s,%s,%.2f%n",
                            id, workspaceId, type, customerName, date, startTime, endTime, totalPrice);
                }
            }

            System.out.println("State successfully saved to files.");
        } catch (Exception e) {
            System.err.println("Error saving state to file: " + e.getMessage());
        }
    }
}
