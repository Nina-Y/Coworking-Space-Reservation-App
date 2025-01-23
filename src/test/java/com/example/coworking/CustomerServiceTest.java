package com.example.coworking;

import com.example.coworking.service.CustomerService;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class CustomerServiceTest {

    @Test
    void testBrowseSpaces() {
        CustomerService customerService = new CustomerService();
        assertDoesNotThrow(customerService::browseSpaces);
    }

    @Test
    void testMakeReservation() {
        String simulatedInput = "Alex\nOpen Space\n2025-01-30\n10\n12\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes());
        Scanner scanner = new Scanner(inputStream);

        CustomerService customerService = new CustomerService();
        assertDoesNotThrow(() -> customerService.makeReservation(scanner));
    }

    @Test
    void testCancelReservation() {
        String simulatedInput = "1\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes());
        Scanner scanner = new Scanner(inputStream);

        CustomerService customerService = new CustomerService();
        assertDoesNotThrow(() -> customerService.cancelReservation(scanner));
    }

    @Test
    void testViewCustomerReservations() {
        CustomerService customerService = new CustomerService();
        assertDoesNotThrow(() -> customerService.viewCustomerReservations("Alex"));
    }
}