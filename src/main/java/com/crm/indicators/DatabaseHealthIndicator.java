package com.crm.indicators;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DatabaseHealthIndicator implements HealthIndicator {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Health health() {
        try {
            var result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            if (result == 1) {
                return Health.up().withDetail("Database", "Available").build();
            }
            return Health.down().withDetail("Database", "Unexpected response").build();
        } catch (Exception e) {
            return Health.down().withDetail("Database", "Error: " + e.getMessage()).build();
        }
    }
}
