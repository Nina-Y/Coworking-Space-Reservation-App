package com.example.coworking;

import com.example.coworking.service.CustomerService;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class CustomerServiceTest {

    @Test
    void whenBrowseSpaces_thenSuccess() {
        CustomerService customerService = new CustomerService();
        assertDoesNotThrow(customerService::browseSpaces);
    }

    @Test
    void givenValidInput_whenMakeReservation_thenSuccess() {
        String simulatedInput = "Alex\nOpen Space\n2025-01-30\n10\n12\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes());
        Scanner scanner = new Scanner(inputStream);

        CustomerService customerService = new CustomerService();
        assertDoesNotThrow(() -> customerService.makeReservation(scanner));
    }

    @Test
    void givenValidInput_whenCancelReservation_thenSuccess() {
        String simulatedInput = "1\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes());
        Scanner scanner = new Scanner(inputStream);

        CustomerService customerService = new CustomerService();
        assertDoesNotThrow(() -> customerService.cancelReservation(scanner));
    }

    @Test
    void givenCustomerName_whenViewReservations_thenSuccess() {
        CustomerService customerService = new CustomerService();
        assertDoesNotThrow(() -> customerService.viewCustomerReservations("Alex"));
    }
}