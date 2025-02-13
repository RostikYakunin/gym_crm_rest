package com.crm.services;

import com.crm.dtos.trainer.TrainerDto;
import com.crm.dtos.trainer.TrainerView;
import com.crm.dtos.training.TrainingView;
import com.crm.enums.TrainingType;
import com.crm.repositories.entities.Trainer;

import java.time.LocalDate;
import java.util.Set;

public interface TrainerService extends UserService<Trainer> {
    Trainer save(String firstName, String lastName, String password, TrainingType specialization);

    Set<TrainingView> findTrainerTrainingsByCriteria(String trainerUsername, LocalDate fromDate, LocalDate toDate, String traineeUserName, TrainingType trainingType);

    TrainerDto addTrainer(TrainerDto trainerDto);

    TrainerView findProfileByUserName(String username);

    TrainerView updateTrainerProfile(Long id, TrainerDto updateDto);

    Set<TrainerDto> findNotAssignedTrainersByTraineeUserName(String username);
}
