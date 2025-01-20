package com.crm.services;

import com.crm.repositories.entities.Training;

public interface TrainingService {
    Training findById(long id);

    Training save(Training training);
}
