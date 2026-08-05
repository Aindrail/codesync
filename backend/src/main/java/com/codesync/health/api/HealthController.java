package com.codesync.health.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("api/v1/health")
public class HealthController {
    @GetMapping
    public HealthResponse health() {
        return new HealthResponse(
                "CodeSync",
                "1.0.0",
                "UP",
                Instant.now()
        );
    }
}
