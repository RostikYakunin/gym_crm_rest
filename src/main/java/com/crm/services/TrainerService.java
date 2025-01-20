package com.crm.services;

import com.crm.models.TrainingType;
import com.crm.repositories.entities.Trainer;

public interface TrainerService extends UserService<Trainer> {
    Trainer save(String firstName, String lastName, String password, TrainingType specialization);
}
