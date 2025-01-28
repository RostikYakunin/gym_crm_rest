package com.crm.services.impl;

import com.crm.UnitTestBase;
import com.crm.repositories.TraineeRepo;
import com.crm.repositories.entities.Trainee;
import com.crm.utils.UserUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TraineeServiceImplTest extends UnitTestBase {
    @Mock
    private TraineeRepo traineeRepo;

    @Captor
    private ArgumentCaptor<String> stringArgumentCaptor;

    @InjectMocks
    private TraineeServiceImpl traineeService;

    @Test
    @DisplayName("findById should return trainee when exists")
    void findById_ShouldReturnTrainee_WhenExists() {
        // Given
        when(traineeRepo.findById(anyLong())).thenReturn(Optional.of(testTrainee));

        // When
        var result = traineeService.findById(1);

        // Then
        assertEquals(testTrainee, result);
        verify(traineeRepo, times(1)).findById(idArgumentCaptor.capture());
    }

    @Test
    @DisplayName("save - should save trainee using parameters")
    void save_ShouldSaveTraineeUsingParameters() {
        // Given
        var expectedUserName = "testName.testLastName";

        when(traineeRepo.save(any(Trainee.class))).thenReturn(testTrainee);

        // When
        var result = traineeService.save("John", "Doe", "password", "testAddress", LocalDate.of(1998, 11, 11));

        // Then
        assertEquals(expectedUserName, result.getUserName());
        assertNotNull(result.getPassword());
        assertEquals(result, result);

        verify(traineeRepo, times(1)).save(traineeArgumentCaptor.capture());
    }

    @Test
    @DisplayName("save - should generate username and password, then save trainee")
    void save_ShouldGenerateUsernameAndPasswordAndSaveTrainee() {
        // Given
        var expectedUserName = "testName.testLastName";

        when(traineeRepo.save(any(Trainee.class))).thenReturn(testTrainee);

        // When
        var result = traineeService.save(testTrainee);

        // Then
        assertEquals(expectedUserName, result.getUserName());
        assertNotNull(result.getPassword());
        assertEquals(result, result);

        verify(traineeRepo, times(1)).save(traineeArgumentCaptor.capture());
    }

    @Test
    @DisplayName("deleteById should return true when trainee was successfully deleted")
    void deleteById_ShouldDelete_WhenTraineeExists() {
        // Given
        doNothing().when(traineeRepo).delete(any(Trainee.class));

        // When
        traineeService.delete(testTrainee);

        // Then
        verify(traineeRepo, times(1)).delete(traineeArgumentCaptor.capture());
    }

    @Test
    @DisplayName("update should return updated trainee when trainee exists")
    void update_ShouldReturnUpdatedTrainee_WhenTraineeExists() {
        // Given
        when(traineeRepo.update(any(Trainee.class))).thenReturn(testTrainee);

        // When
        var result = traineeService.update(testTrainee);

        // Then
        assertNotNull(result);
        assertEquals(testTrainee, result);

        verify(traineeRepo, times(1)).update(traineeArgumentCaptor.capture());
    }

    @Test
    @DisplayName("deleteByUserName - should delete entity when trainee was found in DB")
    void deleteByUserName_ShouldDeleteEntity_WhenTraineeWasFound() {
        // Given
        when(traineeRepo.findByUserName(anyString())).thenReturn(Optional.of(testTrainee));
        doNothing().when(traineeRepo).delete(any(Trainee.class));

        // When
        traineeService.deleteByUsername(testTrainee.getUserName());

        // Then
        verify(traineeRepo, times(1)).findByUserName(stringArgumentCaptor.capture());
        verify(traineeRepo, times(1)).delete(traineeArgumentCaptor.capture());
    }

    @Test
    @DisplayName("findByUsername - should find entity when trainee was found in DB")
    void findByUsername_ShouldFindEntity_WhenTraineeWasFound() {
        // Given
        when(traineeRepo.findByUserName(anyString()))
                .thenReturn(Optional.of(testTrainee))
                .thenReturn(Optional.empty());

        // When
        var result1 = traineeService.findByUsername(testTrainee.getUserName());
        var result2 = traineeService.findByUsername(testTrainee.getUserName());

        // Then
        assertNotNull(result1);
        assertEquals(testTrainee, result1);
        assertNull(result2);

        verify(traineeRepo, times(2)).findByUserName(stringArgumentCaptor.capture());
    }

    @Test
    @DisplayName("changePassword - should change password when trainee`s password matches with found in DB")
    void changePassword_ShouldChangePass_WhenPasswordsMatches() {
        // Given
        testTrainee.setPassword(UserUtils.hashPassword(testTrainee.getPassword()));
        when(traineeRepo.update(any(Trainee.class))).thenReturn(testTrainee);

        // When
        var result1 = traineeService.changePassword(testTrainee, "testPassword", "newPass");
        var result2 = traineeService.changePassword(testTrainee, testTrainee.getPassword(), "newPass");


        // Then
        Assertions.assertTrue(result1);
        Assertions.assertFalse(result2);
        verify(traineeRepo, times(1)).update(traineeArgumentCaptor.capture());
    }

    @Test
    @DisplayName("Toggle active status - should deactivate when currently active")
    void toggleActiveStatus_ShouldDeactivateWhenCurrentlyActive() {
        // Given
        when(traineeRepo.findById(anyLong())).thenReturn(Optional.of(testTrainee));
        when(traineeRepo.update(any(Trainee.class))).thenReturn(testTrainee);

        // When
        var result1 = traineeService.activateStatus(1L);
        var result2 = traineeService.deactivateStatus(1L);

        // Then
        Assertions.assertTrue(result1);
        Assertions.assertFalse(result2);

        verify(traineeRepo, times(2)).findById(1L);
        verify(traineeRepo, times(2)).update(traineeArgumentCaptor.capture());
    }

    @Test
    @DisplayName("Is username and password matching - should return true for matching credentials")
    void isUsernameAndPasswordMatching_ShouldReturnTrueForMatchingCredentials() {
        // Given
        testTrainee.setPassword(UserUtils.hashPassword(testTrainee.getPassword()));
        when(traineeRepo.findByUserName(anyString()))
                .thenReturn(Optional.of(testTrainee))
                .thenReturn(Optional.of(testTrainee))
                .thenReturn(Optional.empty());

        // When
        var result1 = traineeService.isUsernameAndPasswordMatching("testName.testLastName", "testPassword");
        var result2 = traineeService.isUsernameAndPasswordMatching("testName.testLastName", "wrongPassword");
        var result3 = traineeService.isUsernameAndPasswordMatching("unknownUser", "testPassword");

        // Then
        Assertions.assertTrue(result1);
        Assertions.assertFalse(result2);
        Assertions.assertFalse(result3);
        verify(traineeRepo, times(3)).findByUserName(stringArgumentCaptor.capture());
    }
}