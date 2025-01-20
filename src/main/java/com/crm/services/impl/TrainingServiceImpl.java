package com.crm.services.impl;

import com.crm.repositories.TrainingRepo;
import com.crm.repositories.entities.Training;
import com.crm.services.TrainingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TrainingServiceImpl implements TrainingService {
    private final TrainingRepo trainingRepo;

    @Override
    public Training findById(long id) {
        log.info("Searching for training with id={}", id);
        return trainingRepo.findById(id).orElse(null);
    }

    @Override
    public Training save(Training training) {
        log.info("Saving training: {}", training);
        return trainingRepo.save(training);
    }
}
