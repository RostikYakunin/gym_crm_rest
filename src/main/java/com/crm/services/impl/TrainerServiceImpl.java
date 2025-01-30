package com.crm.services.impl;

import com.crm.models.TrainingType;
import com.crm.repositories.TrainerRepo;
import com.crm.repositories.entities.Trainer;
import com.crm.repositories.entities.Training;
import com.crm.services.TrainerService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@Transactional
public class TrainerServiceImpl extends AbstractUserService<Trainer, TrainerRepo> implements TrainerService {
    public TrainerServiceImpl(TrainerRepo repository) {
        super(repository);
    }

    @Override
    public Trainer save(String firstName, String lastName, String password, TrainingType specialization) {
        log.info("Starting saving trainer using first and last names... ");

        var newTrainer = Trainer.builder()
                .firstName(firstName)
                .lastName(lastName)
                .password(password)
                .specialization(specialization)
                .build();

        return super.save(newTrainer);
    }

    @Override
    public List<Trainer> getUnassignedTrainersByTraineeUsername(String traineeUsername) {
        log.info("Starting searching for not assigned trainers by trainee user name... ");
        return repository.getUnassignedTrainersByTraineeUsername(traineeUsername);
    }

    @Override
    public List<Training> findTrainerTrainingsByCriteria(String trainerUsername, LocalDate fromDate, LocalDate toDate, String traineeUserName, TrainingType trainingType) {
        log.info("Starting searching for trainings by criteria... ");
        return repository.getTrainerTrainingsByCriteria(trainerUsername, fromDate, toDate, traineeUserName, trainingType);
    }
}