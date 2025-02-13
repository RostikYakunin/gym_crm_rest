package com.crm.indicators;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class DiskSpaceHealthIndicator implements HealthIndicator {
    private static final long THRESHOLD_BYTES = 100 * 1024 * 1024;

    @Override
    public Health health() {
        var diskPartition = new File("/");
        long freeSpace = diskPartition.getFreeSpace();

        if (freeSpace > THRESHOLD_BYTES) {
            return Health.up().withDetail("Free Space (MB)", freeSpace / (1024 * 1024)).build();
        } else {
            return Health.down().withDetail("Free Space (MB)", freeSpace / (1024 * 1024)).build();
        }
    }
}
