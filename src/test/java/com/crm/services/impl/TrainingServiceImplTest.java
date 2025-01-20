package com.crm.services.impl;

import com.crm.UnitTestBase;
import com.crm.repositories.TrainingRepo;
import com.crm.repositories.entities.Training;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class TrainingServiceImplTest extends UnitTestBase {
    @Mock
    private TrainingRepo trainingRepo;

    @InjectMocks
    private TrainingServiceImpl trainingService;

    private Training testTraining;

    @BeforeEach
    void setUp() {
        testTraining = Training.builder()
                .id(1L)
                .build();
    }

    @AfterEach
    void destroy() {
        testTraining = null;
    }

    @Test
    @DisplayName("findById should return training when exists")
    void findById_ShouldReturnTraining_WhenExists() {
        // Given
        when(trainingRepo.findById(testTraining.getId())).thenReturn(Optional.of(testTraining));

        // When
        var result = trainingService.findById(testTraining.getId());

        // Then
        assertEquals(testTraining, result);
        verify(trainingRepo, times(1)).findById(testTraining.getId());
    }

    @Test
    @DisplayName("save should persist training and return it")
    void save_ShouldPersistTrainingAndReturnIt() {
        // Given
        when(trainingRepo.save(testTraining)).thenReturn(testTraining);

        // When
        var result = trainingService.save(testTraining);

        // Then
        assertEquals(testTraining, result);
        verify(trainingRepo, times(1)).save(testTraining);
    }
}