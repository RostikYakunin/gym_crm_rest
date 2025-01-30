package com.crm.mappers;

import com.crm.dtos.training.TrainingDto;
import com.crm.models.TrainingType;
import com.crm.repositories.entities.Trainee;
import com.crm.repositories.entities.Trainer;
import com.crm.repositories.entities.Training;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TrainingMapperTest {
    private final TrainingMapper trainingMapper = Mappers.getMapper(TrainingMapper.class);

    @Test
    @DisplayName("Should map from Training to TrainingShortView")
    void testToTrainingShortView() {
        // Given
        var trainer = Trainer.builder()
                .userName("trainer_john")
                .build();

        var training = Training.builder()
                .trainingName("Morning Run")
                .trainingDate(LocalDateTime.of(2023, 10, 10, 8, 0))
                .trainingType(TrainingType.FITNESS)
                .trainingDuration(Duration.ofHours(1))
                .trainer(trainer)
                .build();

        // When
        var trainingShortView = trainingMapper.toTrainingShortView(training);

        // Then
        assertNotNull(trainingShortView);
        assertEquals("Morning Run", trainingShortView.getName());
        assertEquals(LocalDateTime.of(2023, 10, 10, 8, 0), trainingShortView.getDate());
        assertEquals(TrainingType.FITNESS, trainingShortView.getType());
        assertEquals(Duration.ofHours(1), trainingShortView.getDuration());
        assertEquals("trainer_john", trainingShortView.getTrainerUserName());
    }

    @Test
    @DisplayName("Should map from TrainingDto to Training")
    void testToTraining() {
        // Given
        var trainee = Trainee.builder().id(1L).build();
        var trainer = Trainer.builder().id(2L).build();

        var trainingDto = TrainingDto.builder()
                .trainee(trainee)
                .trainer(trainer)
                .trainingName("Evening Yoga")
                .trainingType(TrainingType.YOGA)
                .trainingDate(LocalDateTime.of(2023, 10, 10, 18, 0))
                .trainingDuration(Duration.ofMinutes(45))
                .build();

        // When
        var training = trainingMapper.toTraining(trainingDto);

        // Then
        assertNotNull(training);
        assertEquals(trainee, training.getTrainee());
        assertEquals(trainer, training.getTrainer());
        assertEquals("Evening Yoga", training.getTrainingName());
        assertEquals(TrainingType.YOGA, training.getTrainingType());
        assertEquals(LocalDateTime.of(2023, 10, 10, 18, 0), training.getTrainingDate());
        assertEquals(Duration.ofMinutes(45), training.getTrainingDuration());
        assertNull(training.getId());
    }

    @Test
    @DisplayName("Should map from Training to TrainingView")
    void testToTrainingView() {
        // Given
        var trainee = Trainee.builder().id(1L).build();
        var trainer = Trainer.builder().id(2L).build();

        var training = Training.builder()
                .id(100L)
                .trainee(trainee)
                .trainer(trainer)
                .trainingName("Morning Run")
                .trainingType(TrainingType.FITNESS)
                .trainingDate(LocalDateTime.of(2023, 10, 10, 8, 0))
                .trainingDuration(Duration.ofHours(1))
                .build();

        // When
        var trainingView = trainingMapper.toTrainingView(training);

        // Then
        assertNotNull(trainingView);
        assertEquals(100L, trainingView.getId());
        assertEquals(1L, trainingView.getTraineeId());
        assertEquals(2L, trainingView.getTrainerId());
        assertEquals("Morning Run", trainingView.getTrainingName());
        assertEquals(TrainingType.FITNESS, trainingView.getTrainingType());
        assertEquals(LocalDateTime.of(2023, 10, 10, 8, 0), trainingView.getTrainingDate());
        assertEquals(Duration.ofHours(1), trainingView.getTrainingDuration());
    }
}