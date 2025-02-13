package com.crm.services.impl;

import com.crm.UnitTestBase;
import com.crm.dtos.UserLoginDto;
import com.crm.dtos.trainer.TrainerDto;
import com.crm.dtos.trainer.TrainerView;
import com.crm.dtos.training.TrainingView;
import com.crm.enums.TrainingType;
import com.crm.exceptions.PasswordNotMatchException;
import com.crm.exceptions.UserNameChangedException;
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
import org.springframework.core.convert.ConversionService;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainerServiceImplTest extends UnitTestBase {
    @Mock
    private TrainerRepo trainerRepo;
    @Mock
    private ConversionService conversionService;

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
        when(trainerRepo.findByUserName(anyString())).thenReturn(Optional.ofNullable(testTrainer));
        when(trainerRepo.save(any(Trainer.class))).thenReturn(testTrainer);
        testTrainer.setPassword(UserUtils.hashPassword(testTrainer.getPassword()));

        // When - Then
        assertThrows(
                PasswordNotMatchException.class,
                () -> trainerService.changePassword(
                        new UserLoginDto(testTrainer.getUserName(), "wrongPass", "newPass")
                )
        );

        assertDoesNotThrow(
                () -> trainerService.changePassword(
                        new UserLoginDto(testTrainer.getUserName(), "Pasw3456", "newPass")
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
            "trainee1, 1",
            "trainee2, 0"
    })
    @DisplayName("Should return/not return list of assigned trainers")
    void getUnassignedTrainersByTraineeUsername_ShouldReturnCorrectData(String traineeUsername, int expectedSize) {
        // Given
        List<Trainer> expectedTrainers = expectedSize > 0 ? List.of(mock(Trainer.class), mock(Trainer.class)) : Collections.emptyList();
        when(trainerRepo.getUnassignedTrainersByTraineeUsername(traineeUsername)).thenReturn(expectedTrainers);
        lenient().when(conversionService.convert(any(Trainer.class), eq(TrainerDto.class))).thenReturn(new TrainerDto());

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

        lenient().when(conversionService.convert(any(Training.class), eq(TrainingView.class))).thenReturn(new TrainingView());
        when(trainerRepo.getTrainerTrainingsByCriteria(trainerUsername, fromDate, toDate, traineeUsername, trainingType))
                .thenReturn(expectedTrainings);

        // When
        var actualTrainings = trainerService.findTrainerTrainingsByCriteria(trainerUsername, fromDate, toDate, traineeUsername, trainingType);

        // Then
        assertNotNull(actualTrainings);
        assertEquals(expectedSize, actualTrainings.size());
        verify(trainerRepo, times(1)).getTrainerTrainingsByCriteria(trainerUsername, fromDate, toDate, traineeUsername, trainingType);
    }

    @Test
    @DisplayName("addTrainer - should save trainee and return it")
    void addTrainer_ShouldSave_AndReturnIt() {
        // Given
        var trainerDto = TrainerDto.builder().userName(testTrainer.getUserName()).build();
        when(conversionService.convert(any(TrainerDto.class), eq(Trainer.class))).thenReturn(testTrainer);
        when(trainerRepo.save(any(Trainer.class))).thenReturn(testTrainer);
        when(conversionService.convert(any(Trainer.class), eq(TrainerDto.class))).thenReturn(trainerDto);

        // When
        var result = trainerService.addTrainer(new TrainerDto());

        // Then
        assertEquals(testTrainer.getUserName(), result.getUserName());
        verify(trainerRepo, times(1)).save(trainerArgumentCaptor.capture());
    }

    @Test
    @DisplayName("findProfileByUserName - should find trainee and return it")
    void findProfileByUserName_ShouldFind_AndReturnIt() {
        // Given
        when(trainerRepo.findByUserName(anyString())).thenReturn(Optional.ofNullable(testTrainer));
        when(conversionService.convert(any(Trainer.class), eq(TrainerView.class))).thenReturn(new TrainerView());

        // When
        var result = trainerService.findProfileByUserName("username");

        // Then
        assertNotNull(result);
        verify(trainerRepo, times(1)).findByUserName(stringArgumentCaptor.capture());
    }

    @Test
    @DisplayName("updateTraineeProfile - should update trainee`s profile and return it")
    void updateTraineeProfile_shouldUpdateTrainee_ThenReturnIt() {
        // Given
        when(trainerRepo.findByUserName(anyString())).thenReturn(Optional.ofNullable(testTrainer));
        when(trainerRepo.save(any(Trainer.class))).thenReturn(testTrainer);
        when(conversionService.convert(any(TrainerDto.class), eq(Trainer.class))).thenReturn(testTrainer);
        when(conversionService.convert(any(Trainer.class), eq(TrainerView.class))).thenReturn(new TrainerView());
        var trainerDto = TrainerDto.builder().userName(testTrainer.getUserName()).build();

        // When
        var result = trainerService.updateTrainerProfile(1L, trainerDto);

        // Then
        assertNotNull(result);
        verify(trainerRepo, times(1)).findByUserName(stringArgumentCaptor.capture());
    }

    @Test
    @DisplayName("updateTraineeProfile - should throw Exception")
    void updateTraineeProfile_shouldThrowException() {
        // Given
        when(trainerRepo.findByUserName(anyString())).thenReturn(Optional.ofNullable(testTrainer));
        var trainerDto = TrainerDto.builder().userName("username").build();

        // When - Then
        assertThrows(
                UserNameChangedException.class,
                () -> trainerService.updateTrainerProfile(1L, trainerDto),
                "User name changing is forbidden!"
        );

        verify(trainerRepo, times(1)).findByUserName(stringArgumentCaptor.capture());
    }
}