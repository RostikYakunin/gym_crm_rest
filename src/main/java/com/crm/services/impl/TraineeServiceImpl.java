package com.crm.services.impl;

import com.crm.dtos.UserLoginDto;
import com.crm.dtos.trainee.TraineeDto;
import com.crm.dtos.trainee.TraineeTrainingUpdateDto;
import com.crm.dtos.trainee.TraineeView;
import com.crm.dtos.training.TrainingView;
import com.crm.enums.TrainingType;
import com.crm.exceptions.PasswordNotMatchException;
import com.crm.exceptions.UserNameChangedException;
import com.crm.repositories.TraineeRepo;
import com.crm.repositories.entities.Trainee;
import com.crm.repositories.entities.Training;
import com.crm.services.TraineeService;
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
public class TraineeServiceImpl implements TraineeService {
    private final TraineeRepo repository;
    private final ConversionService converter;

    @Override
    public Trainee findById(long id) {
        log.info("Searching for trainee with id={}", id);
        return repository.findById(id).orElse(null);
    }

    @Override
    public Trainee findByUsername(String username) {
        log.info("Searching for trainee with username={}", username);
        return repository.findByUserName(username).orElse(null);
    }

    @Override
    public Trainee findByUsernameOrThrow(String userName) {
        return repository.findByUserName(userName)
                .orElseThrow(() -> new EntityNotFoundException("Trainee with username " + userName + " not found"));
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

        return repository.save(newTrainee);
    }

    @Override
    public Trainee save(Trainee entity) {
        log.info("Checking if trainee already registered in the system.");
        var isExists = repository.existsTrainerByFirstAndLastName(
                entity.getFirstName(),
                entity.getLastName()
        );

        if (isExists) {
            throw new IllegalArgumentException("You can not be registered as a trainer and trainee simultaneously!");
        }

        log.info("Starting saving trainee with first name: {}", entity.getFirstName());
        var uniqueUsername = UserUtils.generateUniqueUsername(
                entity,
                repository::existsByUserName
        );

        entity.setUserName(uniqueUsername);
        entity.setPassword(UserUtils.hashPassword(entity.getPassword()));
        entity.setActive(true);

        var trainee = repository.save(entity);
        log.info("Trainee with id={} was successfully saved", trainee.getId());

        return trainee;
    }

    @Override
    public Trainee update(Trainee entity) {
        log.info("Starting updating trainee...");
        return repository.save(entity);
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
    public boolean activateStatus(long id) {
        log.info("Activating status for trainee with id={}", id);

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
        log.info("Deactivating status for trainee with id={}", id);

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
    public Set<TrainingView> findTraineeTrainingsByCriteria(String traineeUsername, LocalDate fromDate, LocalDate toDate, String trainerUserName, TrainingType trainingType) {
        log.info("Starting searching for trainings by criteria... ");
        return repository.getTraineeTrainingsByCriteria(traineeUsername, fromDate, toDate, trainerUserName, trainingType)
                .stream()
                .map(training -> converter.convert(training, TrainingView.class))
                .collect(Collectors.toSet());
    }

    @Override
    public TraineeDto addTrainee(TraineeDto traineeDto) {
        log.info("Starting adding new trainee`s profile...");
        var fromDto = converter.convert(traineeDto, Trainee.class);
        return converter.convert(save(fromDto), TraineeDto.class);
    }

    @Override
    public void changePassword(UserLoginDto loginDto) {
        log.info("Started changing password for trainee...");
        var foundTrainee = findByUsernameOrThrow(loginDto.getUserName());

        var result = UserUtils.matchesPasswordHash(loginDto.getOldPassword(), foundTrainee.getPassword());
        if (!result) {
            log.error("Inputted password does not match password from DB");
            throw new PasswordNotMatchException();
        }

        log.info("Changing password for trainee...");
        foundTrainee.setPassword(UserUtils.hashPassword(loginDto.getNewPassword()));
        repository.save(foundTrainee);
    }

    @Override
    public TraineeView findProfileByUserName(String username) {
        log.info("Started searching for trainee`s profile with user name=" + username);
        var foundTrainee = findByUsernameOrThrow(username);
        return converter.convert(foundTrainee, TraineeView.class);
    }

    @Override
    public TraineeView updateTraineeProfile(Long id, TraineeDto updateDto) {
        log.info("Starting updating trainee`s profile...");
        var foundTrainee = findByUsernameOrThrow(updateDto.getUserName());
        if (!foundTrainee.getUserName().equals(updateDto.getUserName())) {
            throw new UserNameChangedException();
        }

        var fromDto = converter.convert(updateDto, Trainee.class);
        fromDto.setId(id);

        return converter.convert(update(fromDto), TraineeView.class);
    }

    @Override
    public Set<TrainingView> updateTraineeTrainings(TraineeTrainingUpdateDto updateDto) {
        log.info("Starting updating trainee`s trainings..");
        var newTrainings = updateDto.getTrainings()
                .stream()
                .map(dto -> converter.convert(dto, Training.class))
                .collect(Collectors.toSet());

        var foundTrainee = findByUsernameOrThrow(updateDto.getUserName());

        boolean containsInvalidTrainings = newTrainings
                .stream()
                .anyMatch(training -> !training.getTrainee().getId().equals(foundTrainee.getId()));

        if (containsInvalidTrainings) {
            throw new IllegalArgumentException("Inputted trainings are not belong to user with id=" + foundTrainee.getId());
        }

        foundTrainee.getTrainings().addAll(newTrainings);
        return update(foundTrainee)
                .getTrainings()
                .stream()
                .map(training -> converter.convert(training, TrainingView.class))
                .collect(Collectors.toSet());
    }
}