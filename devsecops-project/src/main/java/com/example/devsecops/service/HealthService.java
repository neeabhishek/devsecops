package com.example.devsecops.service;

import org.springframework.stereotype.Service;

@Service
public class HealthService {

    public String getStatusMessage() {
        return "Application is healthy";
    }
}
