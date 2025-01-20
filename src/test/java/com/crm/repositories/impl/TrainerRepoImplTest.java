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
        assertEquals("testName1.testLastName1", savedTrainer.getUsername());
    }

    @Test
    @DisplayName("Find a trainer by existing ID and verify it is returned")
    void findTrainerById_WhenIdExists_ShouldReturnTrainer() {
        // Given
        var savedTrainer = trainerRepo.save(testTrainer);

        // When
        var foundTrainer = trainerRepo.findById(savedTrainer.getId());
        var unFoundTrainer = trainerRepo.findById(999L);


        // Then
        assertTrue(foundTrainer.isPresent());
        assertEquals("testName1.testLastName1", foundTrainer.get().getUsername());
        assertFalse(unFoundTrainer.isPresent());
    }

    @Test
    @DisplayName("Update a trainer and verify the changes are saved")
    void updateTrainer_ShouldSaveUpdatedTrainer() {
        // Given
        trainerRepo.save(testTrainer);
        testTrainer.setUsername("NewTrainerName");

        // When
        var updatedTrainer = trainerRepo.update(testTrainer);

        // Then
        assertEquals("NewTrainerName", updatedTrainer.getUsername());
    }

    @Test
    @DisplayName("Delete a trainer and verify it is removed")
    void deleteTrainer_ShouldRemoveTrainer() {
        // Given
        trainerRepo.save(testTrainer);

        // When
        trainerRepo.delete(testTrainer);

        // Then
        var deletedTrainer = trainerRepo.findById(testTrainer.getId());
        assertFalse(deletedTrainer.isPresent());
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
                testTrainer.getUsername(), LocalDate.now(), null, testTrainee.getFirstName(), TrainingType.FITNESS
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
        var trainers = trainerRepo.getUnassignedTrainersByTraineeUsername(testTrainee.getUsername());

        // Then
        assertFalse(trainers.isEmpty());
        assertEquals(testTrainer.getUsername(), trainers.get(0).getUsername());
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

    @Test
    @DisplayName("findByUserName - should throw exception when it was not found")
    void findByUserName_ShouldThrowException_WhenEntityWasNotFound() {
        // Given - When - Then
        assertThrows(
                NoResultException.class,
                () -> trainerRepo.findByUserName("unknown")
        );
    }
}