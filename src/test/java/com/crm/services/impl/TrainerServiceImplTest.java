package com.crm.services.impl;

import com.crm.UnitTestBase;
import com.crm.dtos.UserLoginDto;
import com.crm.enums.TrainingType;
import com.crm.exceptions.PasswordNotMatchException;
import com.crm.repositories.TrainerRepo;
import com.crm.repositories.entities.Trainer;
import com.crm.repositories.entities.Training;
import com.crm.utils.UserUtils;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainerServiceImplTest extends UnitTestBase {
    @Mock
    private TrainerRepo trainerRepo;

    @Captor
    ArgumentCaptor<String> stringArgumentCaptor;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    @Test
    @DisplayName("findById should return trainer when exists")
    void findById_ShouldReturnTrainer_WhenExists() {
        // Given
        when(trainerRepo.findById(anyLong())).thenReturn(Optional.of(testTrainer));

        // When
        var result = trainerService.findById(testTrainer.getId());

        // Then
        assertEquals(testTrainer, result);
        verify(trainerRepo, times(1)).findById(idArgumentCaptor.capture());
    }

    @Test
    @DisplayName("save should generate username and password, then save trainer")
    void save_ShouldGenerateUsernameAndPasswordAndSaveTrainer() {
        // Given
        String expectedUserName = "testName1.testLastName1";

        when(trainerRepo.save(any(Trainer.class))).thenReturn(testTrainer);

        // When
        var result = trainerService.save(testTrainer);

        // Then
        assertEquals(expectedUserName, testTrainer.getUserName());
        assertNotNull(testTrainer.getPassword());
        assertEquals(testTrainer, result);

        verify(trainerRepo, times(1)).save(trainerArgumentCaptor.capture());
    }

    @Test
    @DisplayName("save should save using firstname and lastname, then save trainer")
    void save_ShouldSaveWhitFirstnameAndLastname() {
        // Given
        String expectedUserName = "testName1.testLastName1";

        when(trainerRepo.save(any(Trainer.class))).thenReturn(testTrainer);

        // When
        var result = trainerService.save("testName1", "testLastName1", "password", TrainingType.FITNESS);

        // Then
        assertEquals(expectedUserName, testTrainer.getUserName());
        assertNotNull(testTrainer.getPassword());
        assertEquals(testTrainer, result);

        verify(trainerRepo, times(1)).save(trainerArgumentCaptor.capture());
    }

    @Test
    @DisplayName("update should update trainer if exists")
    void update_ShouldUpdateTrainer_IfExists() {
        // Given
        when(trainerRepo.save(any(Trainer.class))).thenReturn(testTrainer);

        // When
        var result = trainerService.update(testTrainer);

        // Then
        assertEquals(testTrainer, result);
        verify(trainerRepo, times(1)).save(trainerArgumentCaptor.capture());
    }

    @Test
    @DisplayName("findByUsername - should find/don`t find entity when trainee was/was not found in DB")
    void findByUsername_ShouldFindEntity_WhenTraineeWasFound() {
        // Given
        when(trainerRepo.findByUserName(anyString()))
                .thenReturn(Optional.of(testTrainer))
                .thenReturn(Optional.empty());

        // When
        var result1 = trainerService.findByUsername(testTrainee.getUserName());
        var result2 = trainerService.findByUsername(testTrainee.getUserName());

        // Then
        assertNotNull(result1);
        assertEquals(testTrainer, result1);
        assertNull(result2);

        verify(trainerRepo, times(2)).findByUserName(stringArgumentCaptor.capture());
    }

    @Test
    @DisplayName("changePassword - should change/don`t change password when trainee`s password matches/don`t matches with found in DB")
    void changePassword_ShouldChangePass_WhenPasswordsMatches() {
        // Given
        var initialPassword = testTrainer.getPassword();
        testTrainer.setPassword(UserUtils.hashPassword(initialPassword));
        when(trainerRepo.save(any(Trainer.class))).thenReturn(testTrainer);

        // When - Then
        assertThrows(
                PasswordNotMatchException.class,
                () -> trainerService.changePassword(
                        new UserLoginDto(testTrainer.getUserName(), "testPassword", "newPass")
                )
        );

        assertDoesNotThrow(
                () -> trainerService.changePassword(
                        new UserLoginDto(testTrainer.getUserName(), testTrainer.getPassword(), "newPass")
                )
        );

        verify(trainerRepo, times(1)).save(trainerArgumentCaptor.capture());
    }

    @Test
    @DisplayName("Activate/deactivate status - should change")
    void toggleActiveStatus_ShouldDeactivateWhenCurrentlyActive() {
        // Given
        when(trainerRepo.findById(anyLong())).thenReturn(Optional.of(testTrainer));
        when(trainerRepo.save(any(Trainer.class))).thenReturn(testTrainer);

        // When
        var result1 = trainerService.activateStatus(1L);
        var result2 = trainerService.deactivateStatus(1L);

        // Then
        assertTrue(result1);
        assertFalse(result2);

        verify(trainerRepo, times(2)).findById(1L);
        verify(trainerRepo, times(2)).save(trainerArgumentCaptor.capture());
    }

    @Test
    @DisplayName("Is username and password matching - should return true/false for matching credentials")
    void isUsernameAndPasswordMatching_ShouldReturnTrueForMatchingCredentials() {
        // Given
        testTrainer.setPassword(UserUtils.hashPassword(testTrainer.getPassword()));
        when(trainerRepo.findByUserName(anyString()))
                .thenReturn(Optional.of(testTrainer))
                .thenReturn(Optional.of(testTrainer))
                .thenReturn(Optional.empty());

        // When
        var result1 = trainerService.isUsernameAndPasswordMatching(testTrainer.getUserName(), "Pasw3456");
        var result2 = trainerService.isUsernameAndPasswordMatching(testTrainer.getUserName(), "wrongPassword");
        var result3 = trainerService.isUsernameAndPasswordMatching("unknownUser", "testPassword");

        // Then
        assertTrue(result1);
        assertFalse(result2);
        assertFalse(result3);
        verify(trainerRepo, times(3)).findByUserName(stringArgumentCaptor.capture());
    }

    @Test
    @DisplayName("Should find user by user name and nothing was thrown")
    void findByUsernameOrThrow_ShouldReturnEntity_WhenUserExists() {
        // Given
        when(trainerRepo.findByUserName(anyString())).thenReturn(Optional.of(testTrainer));

        // When

        var actualUser = assertDoesNotThrow(
                () -> trainerService.findByUsernameOrThrow(testTrainee.getUserName())
        );

        // Then
        assertNotNull(actualUser);
        assertEquals(testTrainer, actualUser);
        verify(trainerRepo, times(1)).findByUserName(stringArgumentCaptor.capture());
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when nothing was found")
    void findByUsernameOrThrow_ShouldThrowException_WhenUserNotFound() {
        // Given
        when(trainerRepo.findByUserName(anyString())).thenReturn(Optional.empty());

        // When - Then
        assertThrows(
                EntityNotFoundException.class,
                () -> trainerService.findByUsernameOrThrow("username"),
                "Entity with username username not found"
        );

        verify(trainerRepo, times(1)).findByUserName(stringArgumentCaptor.capture());
    }

    @ParameterizedTest
    @CsvSource({
            "trainee1, 2",
            "trainee2, 0"
    })
    @DisplayName("Should return/not return list of assigned trainers")
    void getUnassignedTrainersByTraineeUsername_ShouldReturnCorrectData(String traineeUsername, int expectedSize) {
        // Given
        List<Trainer> expectedTrainers = expectedSize > 0 ? List.of(mock(Trainer.class), mock(Trainer.class)) : Collections.emptyList();
        when(trainerRepo.getUnassignedTrainersByTraineeUsername(traineeUsername)).thenReturn(expectedTrainers);

        // When
        var actualTrainers = trainerService.findNotAssignedTrainersByTraineeUserName(traineeUsername);

        // Then
        assertNotNull(actualTrainers);
        assertEquals(expectedSize, actualTrainers.size());
        verify(trainerRepo, times(1)).getUnassignedTrainersByTraineeUsername(traineeUsername);
    }

    @ParameterizedTest
    @CsvSource({
            "trainer1, trainee1, YOGA, 1",
            "trainer2, trainee2, YOGA, 0"
    })
    @DisplayName("Should find/not find list trainings")
    void findTrainerTrainingsByCriteria_ShouldReturnCorrectData(String trainerUsername, String traineeUsername, TrainingType trainingType, int expectedSize) {
        // Given
        var fromDate = LocalDate.of(2024, 1, 1);
        var toDate = LocalDate.of(2024, 12, 31);
        List<Training> expectedTrainings = expectedSize > 0 ? List.of(mock(Training.class)) : Collections.emptyList();

        when(trainerRepo.getTrainerTrainingsByCriteria(trainerUsername, fromDate, toDate, traineeUsername, trainingType))
                .thenReturn(expectedTrainings);

        // When
        var actualTrainings = trainerService.findTrainerTrainingsByCriteria(trainerUsername, fromDate, toDate, traineeUsername, trainingType);

        // Then
        assertNotNull(actualTrainings);
        assertEquals(expectedSize, actualTrainings.size());
        verify(trainerRepo, times(1)).getTrainerTrainingsByCriteria(trainerUsername, fromDate, toDate, traineeUsername, trainingType);
    }
}