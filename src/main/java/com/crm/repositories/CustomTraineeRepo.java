package com.crm.repositories;

import com.crm.enums.TrainingType;
import com.crm.repositories.entities.Training;

import java.time.LocalDate;
import java.util.List;

public interface CustomTraineeRepo {
    List<Training> getTraineeTrainingsByCriteria(
            String traineeUsername,
            LocalDate fromDate,
            LocalDate toDate,
            String trainerUserName,
            TrainingType trainingType
    );
}