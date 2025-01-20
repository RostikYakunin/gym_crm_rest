package com.crm.repositories.impl;

import com.crm.DbTestBase;
import com.crm.models.TrainingType;
import jakarta.persistence.NoResultException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class TraineeRepoImplTest extends DbTestBase {

    @BeforeEach
    void init() {
        traineeRepo.save(testTrainee);
    }

    @Test
    @DisplayName("Save a trainee and verify it is persisted")
    void saveTrainee_ShouldPersistTrainee() {
        // Given
        var expectedUserName = "testName.testLastName";

        //When
        var savedTrainee = traineeRepo.save(testTrainee);

        // Then
        assertNotNull(savedTrainee.getId());
        assertEquals(expectedUserName, savedTrainee.getUsername());
    }

    @Test
    @DisplayName("Find a trainee by existing ID and verify it is returned")
    void findTraineeById_WhenIdExists_ShouldReturnTrainee() {
        // Given
        var expectedUserName = "testName.testLastName";
        var savedId = traineeRepo.save(testTrainee);

        // When
        var notEmptyResult = traineeRepo.findById(savedId.getId());
        var emptyResult = traineeRepo.findById(100000L);

        // Then
        assertTrue(notEmptyResult.isPresent());
        assertEquals(expectedUserName, notEmptyResult.get().getUsername());
        assertTrue(emptyResult.isEmpty());
    }

    @Test
    @DisplayName("Update a trainee and verify the changes are saved")
    void updateTrainee_ShouldSaveUpdatedTrainee() {
        // Given
        traineeRepo.save(testTrainee);
        testTrainee.setUsername("NewTraineeName");

        // When
        var updatedTrainee = traineeRepo.update(testTrainee);

        // Then
        assertEquals("NewTraineeName", updatedTrainee.getUsername());
    }

    @Test
    @DisplayName("Delete a trainee and verify it is removed")
    void deleteTrainee_ShouldRemoveTrainee() {
        // Given
        traineeRepo.save(testTrainee);

        // When
        traineeRepo.delete(testTrainee);

        // Then
        var deletedTrainee = traineeRepo.findById(testTrainee.getId());
        assertFalse(deletedTrainee.isPresent());
    }

    @Test
    @DisplayName("Check if trainee exists by ID and verify true is returned")
    void existsById_WhenIdExists_ShouldReturnTrue() {
        // Given
        var savedTrainee = traineeRepo.save(testTrainee);

        // When
        var trueResult = traineeRepo.isExistsById(savedTrainee.getId());
        var wrongResult = traineeRepo.isExistsById(1000L);

        // Then
        Assertions.assertTrue(trueResult);
        Assertions.assertFalse(wrongResult);
    }

    @Test
    @DisplayName("Get trainee trainings by criteria and verify result")
    void getTraineeTrainingsByCriteria_ShouldReturnCorrectTrainings() {
        // Given
        trainerRepo.save(testTrainer);
        trainingRepo.save(testTraining);

        // When
        var trainings = traineeRepo.getTraineeTrainingsByCriteria(
                testTrainee.getUsername(), LocalDate.now(), null, testTrainer.getFirstName(), TrainingType.FITNESS
        );

        // Then
        assertFalse(trainings.isEmpty());
        assertEquals(1, trainings.size());
        assertEquals(testTraining.getTrainingName(), trainings.get(0).getTrainingName());
    }

    @Test
    @DisplayName("isUserNameExists - should return result when entity was found")
    void isUserNameExists_ShouldReturnTrue_WhenEntityWasFound() {
        // Given - When
        var positiveResult = traineeRepo.isUserNameExists("testName.testLastName");
        var negativeResult = traineeRepo.isUserNameExists("unknown");

        // Then
        Assertions.assertTrue(positiveResult);
        Assertions.assertFalse(negativeResult);
    }

    @Test
    @DisplayName("findByUserName - should return entity when it was found")
    void findByUserName_ShouldReturnEntity_WhenEntityWasFound() {
        // Given - When
        var result = traineeRepo.findByUserName("testName.testLastName");

        // Then
        assertNotNull(result.get());
        assertEquals(testTrainee, result.get());
    }

    @Test
    @DisplayName("findByUserName - should throw exception when it was not found")
    void findByUserName_ShouldThrowException_WhenEntityWasNotFound() {
        // Given - When - Then
        assertThrows(
                NoResultException.class,
                () -> traineeRepo.findByUserName("unknown")
        );
    }
}