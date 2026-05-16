package com.example.devsecops.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class HealthServiceTest {

    private final HealthService healthService = new HealthService();

    @Test
    void shouldReturnHealthyMessage() {

        String response = healthService.getStatusMessage();

        Assertions.assertEquals(
                "Application is healthy",
                response
        );
    }
}
