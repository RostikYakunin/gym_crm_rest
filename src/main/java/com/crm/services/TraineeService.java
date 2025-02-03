package com.crm.services;

import com.crm.dtos.trainee.TraineeDto;
import com.crm.dtos.trainee.TraineeTrainingUpdateDto;
import com.crm.dtos.trainee.TraineeView;
import com.crm.dtos.training.TrainingView;
import com.crm.enums.TrainingType;
import com.crm.repositories.entities.Trainee;

import java.time.LocalDate;
import java.util.Set;

public interface TraineeService extends UserService<Trainee> {
    Trainee save(String firstName, String lastName, String password, String address, LocalDate dateOfBirth);

    void delete(Trainee trainee);

    void deleteByUsername(String username);

    Set<TrainingView> findTraineeTrainingsByCriteria(String traineeUsername, LocalDate fromDate, LocalDate toDate, String trainerUserName, TrainingType trainingType);

    TraineeDto addTrainee(TraineeDto traineeDto);

    TraineeView findProfileByUserName(String username);

    TraineeView updateTraineeProfile(Long id, TraineeDto updateDto);

    Set<TrainingView> updateTraineeTrainings(TraineeTrainingUpdateDto updateDto);
}
