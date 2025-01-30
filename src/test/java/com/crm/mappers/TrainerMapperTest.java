package com.crm.mappers;

import com.crm.models.TrainingType;
import com.crm.repositories.entities.Trainee;
import com.crm.repositories.entities.Trainer;
import com.crm.repositories.entities.Training;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TrainerMapperTest {
    private final TrainerMapper trainerMapper = Mappers.getMapper(TrainerMapper.class);

    @Test
    @DisplayName("Should map from Trainer to TrainerView")
    void testToTrainerView() {
        // Given
        var trainer = Trainer.builder()
                .firstName("John")
                .lastName("Doe")
                .specialization(TrainingType.YOGA)
                .isActive(true)
                .trainings(List.of(
                        Training.builder()
                                .trainingName("Morning Run")
                                .trainingType(TrainingType.YOGA)
                                .trainingDate(LocalDateTime.now())
                                .trainingDuration(Duration.ofHours(1))
                                .build()
                ))
                .build();

        // When
        var trainerView = trainerMapper.toTrainerView(trainer);

        // Then
        assertNotNull(trainerView);
        assertEquals("John", trainerView.getFirstName());
        assertEquals("Doe", trainerView.getLastName());
        assertEquals(TrainingType.YOGA, trainerView.getSpecialization());
        assertTrue(trainerView.getIsActive());
        assertEquals(1, trainerView.getTrainingViews().size());
    }

    @Test
    @DisplayName("Should map from Training to TrainingView")
    void testToTrainingView() {
        // Given
        var training = Training.builder()
                .trainee(Trainee.builder().id(1L).build())
                .trainer(Trainer.builder().id(2L).build())
                .trainingName("Morning Run")
                .trainingType(TrainingType.YOGA)
                .trainingDate(LocalDateTime.now())
                .trainingDuration(Duration.ofHours(1))
                .build();

        // When
        var trainingView = trainerMapper.toTrainingView(training);

        // Then
        assertNotNull(trainingView);
        assertEquals(1L, trainingView.getTraineeId());
        assertEquals(2L, trainingView.getTrainerId());
        assertEquals("Morning Run", trainingView.getTrainingName());
        assertEquals(TrainingType.YOGA, trainingView.getTrainingType());
        assertNotNull(trainingView.getTrainingDate());
        assertEquals(Duration.ofHours(1), trainingView.getTrainingDuration());
    }

    @Test
    @DisplayName("Should map from Trainer to TrainerShortView")
    void testToTrainerShortView() {
        // Given
        var trainer = Trainer.builder()
                .firstName("John")
                .lastName("Doe")
                .userName("johndoe")
                .specialization(TrainingType.YOGA)
                .build();

        // When
        var trainerShortView = trainerMapper.toTrainerShortView(trainer);

        // Then
        assertNotNull(trainerShortView);
        assertEquals("John", trainerShortView.getFirstName());
        assertEquals("Doe", trainerShortView.getLastName());
        assertEquals("johndoe", trainerShortView.getUserName());
        assertEquals(TrainingType.YOGA, trainerShortView.getSpecialization());
    }

    @Test
    @DisplayName("Should update existing Trainer with new data")
    void testUpdateTrainer() {
        // Given
        var existingTrainer = Trainer.builder()
                .firstName("John")
                .lastName("Doe")
                .specialization(TrainingType.YOGA)
                .isActive(true)
                .build();

        var updatedTrainer = Trainer.builder()
                .firstName("Jane")
                .lastName("Smith")
                .specialization(TrainingType.YOGA)
                .isActive(false)
                .build();

        // When
        trainerMapper.updateTrainer(existingTrainer, updatedTrainer);

        // Then
        assertEquals("Jane", existingTrainer.getFirstName());
        assertEquals("Smith", existingTrainer.getLastName());
        assertEquals(TrainingType.YOGA, existingTrainer.getSpecialization());
        assertFalse(existingTrainer.isActive());
    }

    @Test
    @DisplayName("Should map from Trainer to TrainerDto")
    void testToDto() {
        // Given
        var trainer = Trainer.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .userName("johndoe")
                .password("Password123")
                .specialization(TrainingType.YOGA)
                .isActive(true)
                .build();

        // When
        var trainerDto = trainerMapper.toDto(trainer);

        // Then
        assertNotNull(trainerDto);
        assertEquals(1L, trainerDto.getId());
        assertEquals("John", trainerDto.getFirstName());
        assertEquals("Doe", trainerDto.getLastName());
        assertEquals("johndoe", trainerDto.getUserName());
        assertEquals("Password123", trainerDto.getPassword());
        assertEquals(TrainingType.YOGA, trainerDto.getSpecialization());
        assertTrue(trainerDto.getIsActive());
    }
}