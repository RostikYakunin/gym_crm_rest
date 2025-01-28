package com.crm.mappers;

import com.crm.dtos.trainee.TraineeSaveDto;
import com.crm.dtos.trainee.TraineeUpdateDto;
import com.crm.models.TrainingType;
import com.crm.repositories.entities.Trainee;
import com.crm.repositories.entities.Trainer;
import com.crm.repositories.entities.Training;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TraineeMapperTest {

    private final TraineeMapper traineeMapper = Mappers.getMapper(TraineeMapper.class);

    @Test
    @DisplayName("Should map to Trainee from TraineeSaveDto")
    void testToTraineeFromTraineeSaveDto() {
        // Given
        var traineeSaveDto = TraineeSaveDto.builder()
                .firstName("John")
                .lastName("Doe")
                .password("Password123")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .address("123 Main St")
                .build();

        // When
        var trainee = traineeMapper.toTrainee(traineeSaveDto);

        // Then
        assertNotNull(trainee);
        assertEquals("John", trainee.getFirstName());
        assertEquals("Doe", trainee.getLastName());
        assertEquals(LocalDate.of(1990, 1, 1), trainee.getDateOfBirth());
        assertEquals("123 Main St", trainee.getAddress());
        assertTrue(trainee.isActive());
        assertNull(trainee.getId());
        assertNull(trainee.getUserName());
        assertNull(trainee.getPassword());
        assertTrue(trainee.getTrainings().isEmpty());
    }

    @Test
    @DisplayName("Should map to TraineeDto from Trainee")
    void testToDto() {
        // Given
        var trainee = Trainee.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .userName("johndoe")
                .password("Password123")
                .isActive(true)
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .address("123 Main St")
                .trainings(new ArrayList<>())
                .build();

        // When
        var traineeDto = traineeMapper.toDto(trainee);

        // Then
        assertNotNull(traineeDto);
        assertEquals(1L, traineeDto.getId());
        assertEquals("John", traineeDto.getFirstName());
        assertEquals("Doe", traineeDto.getLastName());
        assertEquals("johndoe", traineeDto.getUserName());
        assertEquals("Password123", traineeDto.getPassword());
        assertTrue(traineeDto.getIsActive());
        assertEquals(LocalDate.of(1990, 1, 1), traineeDto.getDateOfBirth());
        assertEquals("123 Main St", traineeDto.getAddress());
    }

    @Test
    @DisplayName("Should update existing trainee from updated trainee")
    void testUpdateTrainee() {
        // Given
        var existingTrainee = Trainee.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .userName("johndoe")
                .password("Password123")
                .isActive(true)
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .address("123 Main St")
                .trainings(new ArrayList<>())
                .build();

        var updatedTrainee = Trainee.builder()
                .firstName("Jane")
                .lastName("Smith")
                .dateOfBirth(LocalDate.of(1995, 5, 5))
                .address("456 Elm St")
                .build();

        // When
        traineeMapper.updateTrainee(existingTrainee, updatedTrainee);

        // Then
        assertEquals("Jane", existingTrainee.getFirstName());
        assertEquals("Smith", existingTrainee.getLastName());
        assertEquals(LocalDate.of(1995, 5, 5), existingTrainee.getDateOfBirth());
        assertEquals("456 Elm St", existingTrainee.getAddress());
        assertEquals(1L, existingTrainee.getId());
        assertEquals("johndoe", existingTrainee.getUserName());
        assertEquals("Password123", existingTrainee.getPassword());
        assertTrue(existingTrainee.isActive());
    }

    @Test
    @DisplayName("Should map from TraineeUpdateDto to Trainee")
    void testToTraineeFromTraineeUpdateDto() {
        // Given
        var traineeUpdateDto = TraineeUpdateDto.builder()
                .firstName("Jane")
                .lastName("Smith")
                .userName("janesmith")
                .dateOfBirth(LocalDate.of(1995, 5, 5))
                .address("456 Elm St")
                .isActive(true)
                .build();

        // When
        var trainee = traineeMapper.toTrainee(traineeUpdateDto);

        // Then
        assertNotNull(trainee);
        assertEquals("Jane", trainee.getFirstName());
        assertEquals("Smith", trainee.getLastName());
        assertEquals(LocalDate.of(1995, 5, 5), trainee.getDateOfBirth());
        assertEquals("456 Elm St", trainee.getAddress());
        assertTrue(trainee.isActive());
        assertNull(trainee.getId());
        assertNull(trainee.getPassword());
        assertTrue(trainee.getTrainings().isEmpty());
    }

    @Test
    @DisplayName("Should map from Trainee to TraineeViewDto")
    void testToTraineeView() {
        // Given
        var trainer = Trainer.builder()
                .userName("trainer1")
                .firstName("Trainer")
                .lastName("One")
                .specialization(TrainingType.YOGA)
                .build();

        var training = Training.builder()
                .trainer(trainer)
                .build();

        var trainee = Trainee.builder()
                .firstName("John")
                .lastName("Doe")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .address("123 Main St")
                .isActive(true)
                .trainings(List.of(training))
                .build();

        // When
        var traineeViewDto = traineeMapper.toTraineeView(trainee);

        // Then
        assertNotNull(traineeViewDto);
        assertEquals("John", traineeViewDto.getFirstName());
        assertEquals("Doe", traineeViewDto.getLastName());
        assertEquals(LocalDate.of(1990, 1, 1), traineeViewDto.getDateOfBirth());
        assertEquals("123 Main St", traineeViewDto.getAddress());
        assertTrue(traineeViewDto.getIsActive());
        assertEquals(1, traineeViewDto.getTrainersList().size());

        var trainerListView = traineeViewDto.getTrainersList().iterator().next();
        assertEquals("trainer1", trainerListView.getUserName());
        assertEquals("Trainer", trainerListView.getFirstName());
        assertEquals("One", trainerListView.getLastName());
        assertEquals(TrainingType.YOGA, trainerListView.getSpecialization());
    }
}