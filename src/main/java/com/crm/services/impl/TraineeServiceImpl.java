package com.crm.services.impl;

import com.crm.models.TrainingType;
import com.crm.repositories.TraineeRepo;
import com.crm.repositories.entities.Trainee;
import com.crm.repositories.entities.Training;
import com.crm.services.TraineeService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@Transactional
public class TraineeServiceImpl extends AbstractUserService<Trainee, TraineeRepo> implements TraineeService {
    public TraineeServiceImpl(TraineeRepo repository) {
        super(repository);
    }

    @Override
    public Trainee save(String firstName, String lastName, String password, String address, LocalDate dateOfBirth) {
        log.info("Starting saving trainee using first and last names... ");

        var newTrainee = Trainee.builder()
                .firstName(firstName)
                .lastName(lastName)
                .password(password)
                .address(address)
                .dateOfBirth(dateOfBirth)
                .build();

        return super.save(newTrainee);
    }

    @Override
    public void delete(Trainee trainee) {
        log.info("Attempting to delete trainee with id: {}", trainee.getId());
        repository.delete(trainee);
    }

    @Override
    public void deleteByUsername(String username) {
        log.info("Started deleting trainee with username= " + username);
        repository.findByUserName(username).ifPresent(repository::delete);
    }

    @Override
    public List<Training> findTraineeTrainingsByCriteria(String traineeUsername, LocalDate fromDate, LocalDate toDate, String trainerUserName, TrainingType trainingType) {
        log.info("Starting searching for trainings by criteria... ");
        return repository.getTraineeTrainingsByCriteria(traineeUsername, fromDate, toDate, trainerUserName, trainingType);
    }
}