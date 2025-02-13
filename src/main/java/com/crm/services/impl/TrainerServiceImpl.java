package com.crm.services.impl;

import com.crm.dtos.UserLoginDto;
import com.crm.dtos.trainer.TrainerDto;
import com.crm.dtos.trainer.TrainerView;
import com.crm.dtos.training.TrainingView;
import com.crm.enums.TrainingType;
import com.crm.exceptions.PasswordNotMatchException;
import com.crm.exceptions.UserNameChangedException;
import com.crm.repositories.TrainerRepo;
import com.crm.repositories.entities.Trainer;
import com.crm.services.TrainerService;
import com.crm.utils.UserUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class TrainerServiceImpl implements TrainerService {
    private final TrainerRepo repository;
    private final ConversionService converter;

    @Override
    public Trainer findById(long id) {
        log.info("Searching for trainer with id={}", id);
        return repository.findById(id).orElse(null);
    }

    @Override
    public Trainer findByUsername(String username) {
        log.info("Searching for trainer with username={}", username);
        return repository.findByUserName(username).orElse(null);
    }

    @Override
    public Trainer findByUsernameOrThrow(String userName) {
        return repository.findByUserName(userName)
                .orElseThrow(() -> new EntityNotFoundException("Trainer with username " + userName + " not found"));
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

        return repository.save(newTrainer);
    }

    @Override
    public Trainer save(Trainer entity) {
        log.info("Checking if trainer already registered in the system.");
        var isExists = repository.existsTraineeByFirstAndLastName(
                entity.getFirstName(),
                entity.getLastName()
        );

        if (isExists) {
            throw new IllegalArgumentException("You can not be registered as a trainer and trainee simultaneously!");
        }

        log.info("Starting saving trainer with first name: {}", entity.getFirstName());
        var uniqueUsername = UserUtils.generateUniqueUsername(
                entity,
                repository::existsByUserName
        );

        entity.setUserName(uniqueUsername);
        entity.setPassword(UserUtils.hashPassword(entity.getPassword()));
        entity.setActive(true);

        var savedTrainer = repository.save(entity);
        log.info("Trainer with id={} was successfully saved", savedTrainer.getId());

        return savedTrainer;
    }

    @Override
    public Trainer update(Trainer entity) {
        log.info("Starting updating entity...");
        return repository.save(entity);
    }

    @Override
    public boolean activateStatus(long id) {
        log.info("Activating status for trainer with id={}", id);

        var foundEntity = repository.findById(id);
        if (foundEntity.isPresent()) {
            var entity = foundEntity.get();
            entity.setActive(true);
            return repository.save(entity).isActive();
        }

        return false;
    }

    @Override
    public boolean deactivateStatus(long id) {
        log.info("Deactivating status for trainer with id={}", id);

        var foundEntity = repository.findById(id);
        if (foundEntity.isPresent()) {
            var entity = foundEntity.get();
            entity.setActive(false);
            return repository.save(entity).isActive();
        }

        return false;
    }

    @Override
    public boolean isUsernameAndPasswordMatching(String username, String inputtedPassword) {
        log.info("Started verification for user name and password matching...");
        return repository.findByUserName(username)
                .map(user -> UserUtils.matchesPasswordHash(inputtedPassword, user.getPassword()))
                .orElse(false);
    }

    @Override
    public TrainerDto addTrainer(TrainerDto trainerDto) {
        log.info("Starting adding new trainer`s profile...");
        var fromDto = converter.convert(trainerDto, Trainer.class);
        return converter.convert(save(fromDto), TrainerDto.class);
    }

    @Override
    public void changePassword(UserLoginDto loginDto) {
        log.info("Started changing password for trainer...");
        var foundTrainer = findByUsernameOrThrow(loginDto.getUserName());

        var result = UserUtils.matchesPasswordHash(loginDto.getOldPassword(), foundTrainer.getPassword());
        if (!result) {
            log.error("Inputted password does not match password from DB");
            throw new PasswordNotMatchException();
        }

        log.info("Changing password for trainee...");
        foundTrainer.setPassword(UserUtils.hashPassword(loginDto.getNewPassword()));
        repository.save(foundTrainer);
    }

    @Override
    public TrainerView findProfileByUserName(String username) {
        log.info("Started searching for trainer`s profile with user name=" + username);
        var foundTrainer = findByUsernameOrThrow(username);
        return converter.convert(foundTrainer, TrainerView.class);
    }

    @Override
    public TrainerView updateTrainerProfile(Long id, TrainerDto updateDto) {
        log.info("Starting updating trainer`s profile...");
        var foundTrainer = findByUsernameOrThrow(updateDto.getUserName());
        if (!foundTrainer.getUserName().equals(updateDto.getUserName())) {
            throw new UserNameChangedException();
        }

        var fromDto = converter.convert(updateDto, Trainer.class);
        fromDto.setId(id);

        return converter.convert(update(fromDto), TrainerView.class);
    }

    @Override
    public Set<TrainerDto> findNotAssignedTrainersByTraineeUserName(String username) {
        log.info("Starting searching for not assigned trainers by trainee user name... ");
        return repository.getUnassignedTrainersByTraineeUsername(username)
                .stream()
                .map(trainer -> converter.convert(trainer, TrainerDto.class))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<TrainingView> findTrainerTrainingsByCriteria(String trainerUsername, LocalDate fromDate, LocalDate toDate, String traineeUserName, TrainingType trainingType) {
        log.info("Starting searching for trainings by criteria... ");
        return repository.getTrainerTrainingsByCriteria(trainerUsername, fromDate, toDate, traineeUserName, trainingType)
                .stream()
                .map(training -> converter.convert(training, TrainingView.class))
                .collect(Collectors.toSet());
    }

}