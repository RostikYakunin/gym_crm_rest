package com.crm.repositories;

import com.crm.models.TrainingType;
import com.crm.repositories.entities.Trainer;
import com.crm.repositories.entities.Training;

import java.time.LocalDate;
import java.util.List;

public interface TrainerRepo extends UserRepo<Trainer> {
    List<Training> getTrainerTrainingsByCriteria(String trainerUsername, LocalDate fromDate, LocalDate toDate, String traineeUserName, TrainingType trainingType);

    List<Trainer> getUnassignedTrainersByTraineeUsername(String traineeUsername);
}
