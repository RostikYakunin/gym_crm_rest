package com.crm.services;

import com.crm.models.TrainingType;
import com.crm.repositories.entities.Trainer;
import com.crm.repositories.entities.Training;

import java.time.LocalDate;
import java.util.List;

public interface TrainerService extends UserService<Trainer> {
    Trainer save(String firstName, String lastName, String password, TrainingType specialization);

    List<Trainer> getUnassignedTrainersByTraineeUsername(String traineeUsername);

    List<Training> findTrainerTrainingsByCriteria(String trainerUsername, LocalDate fromDate, LocalDate toDate, String traineeUserName);
}
