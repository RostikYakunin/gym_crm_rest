package com.crm.indicators;

import com.crm.repositories.TraineeRepo;
import com.crm.repositories.TrainerRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GymDataHealthIndicator implements HealthIndicator {
    private final TraineeRepo traineeRepository;
    private final TrainerRepo trainerRepository;

    @Override
    public Health health() {
        long trainees = traineeRepository.count();
        long trainers = trainerRepository.count();

        if (trainees > 0 && trainers > 0) {
            return Health.up().withDetail("trainees", trainees).withDetail("trainers", trainers).build();
        } else {
            return Health.down()
                    .withDetail("message", "No trainees or trainers found in the system")
                    .withDetail("trainees", trainees)
                    .withDetail("trainers", trainers)
                    .build();
        }
    }
}