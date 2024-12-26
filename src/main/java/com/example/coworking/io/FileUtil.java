package com.example.coworking.io;

import com.example.coworking.model.Workspace;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FileUtil {

    public static void loadWorkspacesFromFile(List<Workspace> workspaces, Map<String, Integer> workspaceCounts, String filePath) {
        List<Workspace> loadedWorkspaces = new ArrayList<>();
        File file = new File(filePath);

        if (!file.exists()) {
            System.out.println("The file does not exist");
        }

        if (file.length() == 0) {
            System.out.println("The file is empty.");
            writeDummyData(filePath);
            System.out.println("Populating with dummy data.\n");
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                int id = Integer.parseInt(parts[0]);
                String type = parts[1];
                double price = Double.parseDouble(parts[2]);
                loadedWorkspaces.add(new Workspace(id, type, price));
            }
        }   catch (IOException | NumberFormatException e) {
            System.err.println("Error reading workspaces from file: " + e.getMessage());
            System.out.println();
        }

        workspaces.addAll(loadedWorkspaces);

        for (Workspace workspace : loadedWorkspaces) {
            workspaceCounts.put(workspace.getType(), workspaceCounts.getOrDefault(workspace.getType(), 0) + 1);
        }
    }

    public static void saveWorkspacesToFile(String filePath, List<Workspace> workspaces) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            for (Workspace workspace : workspaces) {
                writer.println(workspace.getId() + "," + workspace.getType() + "," + workspace.getPrice());
            }
        } catch (IOException e) {
            System.err.println("Error saving workspaces to file: " + e.getMessage());
            System.out.println();
        }
    }

    public static void writeDummyData(String filePath) {
        List<Workspace> dummyWorkspaces = List.of(
                new Workspace(1, "Open Space", 5.0),
                new Workspace(2, "Private Desk", 8.0),
                new Workspace(3, "Private Room", 20.0),
                new Workspace(4, "Meeting Room", 30.0),
                new Workspace(5, "Event Space", 50.0)
        );
        saveWorkspacesToFile(filePath, dummyWorkspaces);
    }
}
