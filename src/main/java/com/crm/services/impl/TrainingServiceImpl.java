package com.crm.services.impl;

import com.crm.dtos.training.TrainingDto;
import com.crm.dtos.training.TrainingView;
import com.crm.repositories.TrainingRepo;
import com.crm.repositories.entities.Training;
import com.crm.services.TrainingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TrainingServiceImpl implements TrainingService {
    private final TrainingRepo trainingRepo;
    private final ConversionService convertor;

    @Override
    public Training findById(long id) {
        log.info("Searching for training with id={}", id);
        return trainingRepo.findById(id).orElse(null);
    }

    @Override
    public Training save(Training training) {
        log.info("Started saving training");
        return trainingRepo.save(training);
    }

    @Override
    public TrainingView addTraining(TrainingDto trainingDto) {
        var fromDto = convertor.convert(trainingDto, Training.class);
        return convertor.convert(trainingRepo.save(fromDto), TrainingView.class);
    }
}