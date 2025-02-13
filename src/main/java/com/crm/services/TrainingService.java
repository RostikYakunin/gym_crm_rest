package com.crm.services;

import com.crm.dtos.training.TrainingDto;
import com.crm.dtos.training.TrainingView;
import com.crm.repositories.entities.Training;

public interface TrainingService {
    Training findById(long id);

    Training save(Training training);

    TrainingView addTraining(TrainingDto trainingDto);
}
