package com.crm.repositories;

import com.crm.enums.TrainingType;
import com.crm.repositories.entities.Training;

import java.time.LocalDate;
import java.util.List;

/**
 * Custom repository for fetching trainer-specific training sessions based on dynamic criteria.
 *
 * This interface provides a method for retrieving a list of training sessions conducted by a specific trainer.
 * The search criteria are flexible, allowing filtering based on optional parameters such as date range, trainee username, and training type.
 */
public interface CustomTrainerRepo {
    /**
     * Retrieves a list of training sessions conducted by a given trainer, applying optional filters as needed.
     *
     * @param trainerUsername the username of the trainer (required)
     * @param fromDate the start date for filtering training sessions (optional, can be null)
     * @param toDate the end date for filtering training sessions (optional, can be null)
     * @param traineeUserName the username of the trainee (optional, can be null)
     * @param trainingType the type of training (optional, can be null)
     * @return a list of {@link Training} entities matching the given criteria
     */
    List<Training> getTrainerTrainingsByCriteria(
            String trainerUsername,
            LocalDate fromDate,
            LocalDate toDate,
            String traineeUserName,
            TrainingType trainingType
    );
}