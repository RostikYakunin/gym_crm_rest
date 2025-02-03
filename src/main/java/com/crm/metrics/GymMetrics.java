package com.crm.metrics;

import com.crm.repositories.TraineeRepo;
import com.crm.repositories.TrainerRepo;
import com.crm.repositories.TrainingRepo;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GymMetrics {
    private final TrainerRepo trainerRepository;
    private final TraineeRepo traineeRepository;
    private final TrainingRepo trainingRepository;
    private final MeterRegistry meterRegistry;

    @PostConstruct
    public void registerMetrics() {
        meterRegistry.gauge("gym.trainers.count", trainerRepository, TrainerRepo::count);
        meterRegistry.gauge("gym.trainees.count", traineeRepository, TraineeRepo::count);
        meterRegistry.gauge("gym.trainings.count", trainingRepository, TrainingRepo::count);
    }
}