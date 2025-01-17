package com.example.coworking;

import static org.junit.jupiter.api.Assertions.*;

import com.example.coworking.model.Reservation;
import com.example.coworking.model.Workspace;
import com.example.coworking.service.AdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

class AdminServiceTest {

    private AdminService adminService;
    private List<Workspace> workspaces;
    private Map<String, Integer> workspaceCounts;
    private List<Reservation> reservations;

    @BeforeEach
    void setUp() {
        workspaces = new ArrayList<>();
        workspaceCounts = new HashMap<>();
        reservations = new ArrayList<>();
        adminService = new AdminService(workspaces, workspaceCounts, reservations, 1);
    }

    @Test
    void testAddWorkspace() {
        adminService.addWorkspaceToInventory("Open Space", 5.0, 2);
        assertEquals(2, workspaces.size());
        assertEquals("Open Space", workspaces.getFirst().getType());
    }

    @Test
    void testRemoveWorkspace() {
        Workspace workspace = new Workspace(1, "Open Space", 5.0);
        workspaces.add(workspace);
        workspaceCounts.put("Open Space", 1);
        Scanner scanner = new Scanner("1\n");
        adminService.removeWorkspace(scanner);
        assertEquals(0, workspaces.size());
    }

    @Test
    void testViewAllReservations() {
        Reservation reservation = new Reservation(1, 1, "Open Space", "Alex", "2025-01-30", "10:00", "12:00", 10.0);
        reservations.add(reservation);
        adminService.viewAllReservations();
        assertTrue(reservations.contains(reservation));
    }
}

