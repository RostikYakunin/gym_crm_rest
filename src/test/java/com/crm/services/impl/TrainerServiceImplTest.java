package com.crm.services.impl;

import com.crm.UnitTestBase;
import com.crm.models.TrainingType;
import com.crm.repositories.TrainerRepo;
import com.crm.repositories.entities.Trainer;
import com.crm.utils.UserUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

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
        when(trainerRepo.update(any(Trainer.class))).thenReturn(testTrainer);

        // When
        var result = trainerService.update(testTrainer);

        // Then
        assertEquals(testTrainer, result);
        verify(trainerRepo, times(1)).update(trainerArgumentCaptor.capture());
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
        when(trainerRepo.update(any(Trainer.class))).thenReturn(testTrainer);

        // When
        var result1 = trainerService.changePassword(testTrainer, initialPassword, "newPass");
        var result2 = trainerService.changePassword(testTrainer, "wrong", "newPass");

        // Then
        assertTrue(result1);
        assertFalse(result2);
        verify(trainerRepo, times(1)).update(trainerArgumentCaptor.capture());
    }

    @Test
    @DisplayName("Activate/deactivate status - should change")
    void toggleActiveStatus_ShouldDeactivateWhenCurrentlyActive() {
        // Given
        when(trainerRepo.findById(anyLong())).thenReturn(Optional.of(testTrainer));
        when(trainerRepo.update(any(Trainer.class))).thenReturn(testTrainer);

        // When
        var result1 = trainerService.activateStatus(1L);
        var result2 = trainerService.deactivateStatus(1L);

        // Then
        assertTrue(result1);
        assertFalse(result2);

        verify(trainerRepo, times(2)).findById(1L);
        verify(trainerRepo, times(2)).update(trainerArgumentCaptor.capture());
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
}