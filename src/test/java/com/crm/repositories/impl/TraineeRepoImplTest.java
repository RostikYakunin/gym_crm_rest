package com.crm.repositories.impl;

import com.crm.DbTestBase;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
        assertEquals(expectedUserName, savedTrainee.getUserName());
    }

    @Test
    @DisplayName("Find a trainee by existing ID and verify it is returned")
    void findTraineeById_WhenIdExists_ShouldReturnTrainee() {
        // Given
        var savedId = traineeRepo.save(testTrainee);

        // When
        var notEmptyResult = traineeRepo.findById(savedId.getId());
        var emptyResult = traineeRepo.findById(100000L);

        // Then
        assertTrue(notEmptyResult.isPresent());
        assertEquals(testTrainee.getUserName(), notEmptyResult.get().getUserName());
        assertTrue(emptyResult.isEmpty());
    }

    @Test
    @DisplayName("Update a trainee and verify the changes are saved")
    void updateTrainee_ShouldSaveUpdatedTrainee() {
        // Given
        traineeRepo.save(testTrainee);
        testTrainee.setUserName("NewTraineeName");

        // When
        var updatedTrainee = traineeRepo.update(testTrainee);

        // Then
        assertEquals("NewTraineeName", updatedTrainee.getUserName());
    }

    @Test
    @DisplayName("Delete a trainee and verify it is removed")
    void deleteTrainee_ShouldRemoveTrainee() {
        // Given
        var saved = traineeRepo.save(testTrainee);

        // When
        traineeRepo.delete(saved);

        // Then
        assertThrows(
                EntityNotFoundException.class,
                () -> traineeRepo.findByUserName(saved.getUserName())
        );
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
                testTrainee.getUserName(), null, null, null, null
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
}