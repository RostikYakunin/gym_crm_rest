package com.crm.repositories;

import com.crm.enums.TrainingType;
import com.crm.repositories.entities.Training;

import java.time.LocalDate;
import java.util.List;

/**
 * Custom repository interface for fetching trainee-specific training sessions based on dynamic criteria.
 *
 * This interface provides a method for retrieving a list of training sessions associated with a specific trainee.
 * The search criteria are flexible, allowing filtering based on optional parameters such as date range, trainer username, and training type.
 */
public interface CustomTraineeRepo {
    /**
     * Retrieves a list of training sessions for a given trainee, applying optional filters as needed.
     *
     * @param traineeUsername the username of the trainee (required)
     * @param fromDate the start date for filtering training sessions (optional, can be null)
     * @param toDate the end date for filtering training sessions (optional, can be null)
     * @param trainerUserName the username of the trainer (optional, can be null)
     * @param trainingType the type of training (optional, can be null)
     * @return a list of {@link Training} entities matching the given criteria
     */
    List<Training> getTraineeTrainingsByCriteria(
            String traineeUsername,
            LocalDate fromDate,
            LocalDate toDate,
            String trainerUserName,
            TrainingType trainingType
    );
}