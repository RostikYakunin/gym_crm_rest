package com.crm.repositories;

import com.crm.models.TrainingType;
import com.crm.repositories.entities.Trainee;
import com.crm.repositories.entities.Training;

import java.time.LocalDate;
import java.util.List;

public interface TraineeRepo extends UserRepo<Trainee> {
    List<Training> getTraineeTrainingsByCriteria(String traineeUsername, LocalDate fromDate, LocalDate toDate, String trainerUserName, TrainingType trainingType);
}
