package com.crm.repositories.impl;

import com.crm.DbTestBase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class TrainerRepoImplTest extends DbTestBase {

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
        var emptyTrainer = trainerRepo.findById(999L);


        // Then
        assertTrue(foundTrainer.isPresent());
        assertEquals("testName1.testLastName1", foundTrainer.get().getUserName());
        assertTrue(emptyTrainer.isEmpty());
    }

    @Test
    @DisplayName("Update a trainer and verify the changes are saved")
    void updateTrainer_ShouldSaveUpdatedTrainer() {
        // Given
        trainerRepo.save(testTrainer);
        testTrainer.setUserName("NewTrainerName");

        // When
        var updatedTrainer = trainerRepo.save(testTrainer);

        // Then
        assertEquals("NewTrainerName", updatedTrainer.getUserName());
    }

    @Test
    @DisplayName("Check if trainer exists by ID and verify result is returned")
    void existsById_WhenIdExists_ShouldReturnTrue() {
        // Given
        trainerRepo.save(testTrainer);

        // When
        var result1 = trainerRepo.existsById(testTrainer.getId());
        var result2 = trainerRepo.existsById(999L);


        // Then
        Assertions.assertTrue(result1);
        Assertions.assertFalse(result2);
    }

    @Test
    @DisplayName("Get trainer trainings by criteria and verify result")
    void getTrainerTrainingsByCriteria_ShouldReturnCorrectTrainings() {
        // Given
        trainerRepo.save(testTrainer);
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
        var savedTrainee = traineeRepo.save(testTrainee);
        var savedTrainer = trainerRepo.save(testTrainer);

        // When
        var trainers = trainerRepo.getUnassignedTrainersByTraineeUsername(savedTrainee.getUserName());

        // Then
        assertFalse(trainers.isEmpty());
        assertEquals(savedTrainer.getUserName(), trainers.get(0).getUserName());
    }

    @Test
    @DisplayName("isUserNameExists - should return result when entity was found")
    void isUserNameExists_ShouldReturnTrue_WhenEntityWasFound() {
        // Given - When
        var savedTrainer = trainerRepo.save(testTrainer);
        var positiveResult = trainerRepo.existsByUserName(savedTrainer.getUserName());

        // Then
        Assertions.assertTrue(positiveResult);
    }

    @Test
    @DisplayName("findByUserName - should return entity when it was found")
    void findByUserName_ShouldReturnEntity_WhenEntityWasFound() {
        // Given - When
        var savedTrainer = trainerRepo.save(testTrainer);
        var result = trainerRepo.findByUserName(savedTrainer.getUserName());

        // Then
        assertNotNull(result.get());
        assertEquals(testTrainer, result.get());
    }
}