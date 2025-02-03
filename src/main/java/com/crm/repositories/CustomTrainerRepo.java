package com.crm.repositories;

import com.crm.enums.TrainingType;
import com.crm.repositories.entities.Training;

import java.time.LocalDate;
import java.util.List;

public interface CustomTrainerRepo {
    List<Training> getTrainerTrainingsByCriteria(
            String trainerUsername,
            LocalDate fromDate,
            LocalDate toDate,
            String traineeUserName,
            TrainingType trainingType
    );
}