package com.example.coworking;

import com.example.coworking.service.AdminService;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class AdminServiceTest {

    @Test
    void givenValidInput_whenAddWorkspace_thenSuccess() {
        String simulatedInput = "Open Space\n10.0\n3\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes());
        Scanner scanner = new Scanner(inputStream);

        AdminService adminService = new AdminService();
        assertDoesNotThrow(() -> adminService.addWorkspace(scanner));
    }

    @Test
    void givenValidInput_whenRemoveWorkspace_thenSuccess() {
        String simulatedInput = "1\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes());
        Scanner scanner = new Scanner(inputStream);

        AdminService adminService = new AdminService();
        assertDoesNotThrow(() -> adminService.removeWorkspace(scanner));
    }

    @Test
    void whenViewAllReservations_thenSuccess() {
        AdminService adminService = new AdminService();
        assertDoesNotThrow(adminService::viewAllReservations);
    }
}