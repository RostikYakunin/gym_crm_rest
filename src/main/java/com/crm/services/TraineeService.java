package com.crm.services;

import com.crm.models.TrainingType;
import com.crm.repositories.entities.Trainee;
import com.crm.repositories.entities.Training;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface TraineeService extends UserService<Trainee> {
    Trainee save(String firstName, String lastName, String password, String address, LocalDate dateOfBirth);

    void delete(Trainee trainee);

    void deleteByUsername(String username);

    List<Training> findTraineeTrainingsByCriteria(String traineeUsername, LocalDate fromDate, LocalDate toDate, String trainerUserName, TrainingType trainingType);

    Trainee updateTraineeTrainings(String username, Set<Training> newTrainings);
}
