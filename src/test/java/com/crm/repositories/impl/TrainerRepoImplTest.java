package com.crm.repositories.impl;

import com.crm.DbTestBase;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TrainerRepoImplTest extends DbTestBase {

    @BeforeEach
    void init() {
        trainerRepo.save(testTrainer);
    }

    @Test
    @DisplayName("Save a trainer and verify it is persisted")
    void saveTrainer_ShouldPersistTrainer() {
        // Given - When
        var savedTrainer = trainerRepo.save(testTrainer);

        // Then
        assertNotNull(savedTrainer.getId());
        assertEquals("testName1.testLastName1", savedTrainer.getUserName());
    }

    @Test
    @DisplayName("Find a trainer by existing ID and verify it is returned")
    void findTrainerById_WhenIdExists_ShouldReturnTrainer() {
        // Given
        var savedTrainer = trainerRepo.save(testTrainer);

        // When
        var foundTrainer = trainerRepo.findById(savedTrainer.getId());
        assertThrows(
                EntityNotFoundException.class,
                () -> trainerRepo.findById(999L)
        );

        // Then
        assertTrue(foundTrainer.isPresent());
        assertEquals("testName1.testLastName1", foundTrainer.get().getUserName());
    }

    @Test
    @DisplayName("Update a trainer and verify the changes are saved")
    void updateTrainer_ShouldSaveUpdatedTrainer() {
        // Given
        trainerRepo.save(testTrainer);
        testTrainer.setUserName("NewTrainerName");

        // When
        var updatedTrainer = trainerRepo.update(testTrainer);

        // Then
        assertEquals("NewTrainerName", updatedTrainer.getUserName());
    }

    @Test
    @DisplayName("Delete a trainer and verify it is removed")
    void deleteTrainer_ShouldRemoveTrainer() {
        // Given
        trainerRepo.save(testTrainer);

        // When
        trainerRepo.delete(testTrainer);

        // Then
        assertThrows(
                EntityNotFoundException.class,
                () -> trainerRepo.findById(testTrainer.getId())
        );
    }

    @Test
    @DisplayName("Check if trainer exists by ID and verify result is returned")
    void existsById_WhenIdExists_ShouldReturnTrue() {
        // Given
        trainerRepo.save(testTrainer);

        // When
        var result1 = trainerRepo.isExistsById(testTrainer.getId());
        var result2 = trainerRepo.isExistsById(999L);


        // Then
        Assertions.assertTrue(result1);
        Assertions.assertFalse(result2);
    }

    @Test
    @DisplayName("Get trainer trainings by criteria and verify result")
    void getTrainerTrainingsByCriteria_ShouldReturnCorrectTrainings() {
        // Given
        traineeRepo.save(testTrainee);
        trainingRepo.save(testTraining);

        // When
        var trainings = trainerRepo.getTrainerTrainingsByCriteria(
                testTrainer.getUserName(), null, null, null, null
        );

        // Then
        assertFalse(trainings.isEmpty());
        assertEquals(1, trainings.size());
        assertEquals(testTraining.getTrainingName(), trainings.get(0).getTrainingName());
    }

    @Test
    @DisplayName("Get unassigned trainers by trainee username and verify result")
    void getUnassignedTrainersByTraineeUsername_ShouldReturnCorrectTrainers() {
        // Given
        traineeRepo.save(testTrainee);
        traineeRepo.save(testTrainee);

        // When
        var trainers = trainerRepo.getUnassignedTrainersByTraineeUsername(testTrainee.getUserName());

        // Then
        assertFalse(trainers.isEmpty());
        assertEquals(testTrainer.getUserName(), trainers.get(0).getUserName());
    }

    @Test
    @DisplayName("isUserNameExists - should return result when entity was found")
    void isUserNameExists_ShouldReturnTrue_WhenEntityWasFound() {
        // Given - When
        var positiveResult = trainerRepo.isUserNameExists("testName1.testLastName1");
        var negativeResult = trainerRepo.isUserNameExists("unknown");

        // Then
        Assertions.assertTrue(positiveResult);
        Assertions.assertFalse(negativeResult);
    }

    @Test
    @DisplayName("findByUserName - should return entity when it was found")
    void findByUserName_ShouldReturnEntity_WhenEntityWasFound() {
        // Given - When
        var result = trainerRepo.findByUserName("testName1.testLastName1");

        // Then
        assertNotNull(result.get());
        assertEquals(testTrainer, result.get());
    }
}