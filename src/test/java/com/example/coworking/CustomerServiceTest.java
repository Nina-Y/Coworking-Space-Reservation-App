package com.example.coworking;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.coworking.model.Reservation;
import com.example.coworking.model.Workspace;
import com.example.coworking.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

class CustomerServiceTest {

    private CustomerService customerService;
    private List<Workspace> workspaces;
    private Map<String, Integer> workspaceCounts;
    private List<Reservation> reservations;

    @BeforeEach
    void setUp() {
        workspaces = new ArrayList<>();
        workspaceCounts = new HashMap<>();
        reservations = new ArrayList<>();
        customerService = new CustomerService(workspaces, workspaceCounts, reservations, 1);
    }

    @Test
    void testBrowseSpaces() {
        workspaceCounts.put("Open Space", 3);
        customerService.browseSpaces();
        assertEquals(3, workspaceCounts.get("Open Space"));
    }

    @Test
    void testMakeReservation() {
        Workspace workspace = new Workspace(1, "Open Space", 5.0);
        workspaces.add(workspace);
        workspaceCounts.put("Open Space", 1);

        Scanner scanner = new Scanner("Alex\nOpen Space\n2025-01-17\n10\n12\n");
        customerService.makeReservation(scanner);

        assertEquals(1, reservations.size());
        Reservation reservation = reservations.getFirst();
        assertEquals("Alex", reservation.getCustomerName());
        assertEquals("Open Space", reservation.getType());
        assertEquals("2025-01-16", reservation.getDate());
        assertEquals("10:00", reservation.getStartTime());
        assertEquals("12:00", reservation.getEndTime());
    }

    @Test
    void testCancelReservation() {
        Reservation reservation = new Reservation(1, 1, "Open Space", "Alex", "2025-01-17", "10:00", "12:00", 40.0);
        reservations.add(reservation);

        Scanner scanner = new Scanner("1\n");
        customerService.cancelReservation(scanner);

        assertEquals(0, reservations.size());
    }
}

