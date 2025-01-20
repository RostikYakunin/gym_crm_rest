package com.crm;

import com.crm.models.TrainingType;
import com.crm.repositories.entities.Trainee;
import com.crm.repositories.entities.Trainer;
import com.crm.repositories.entities.Training;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

public abstract class UnitTestBase {
    // Captors
    @Captor
    protected ArgumentCaptor<Trainee> traineeArgumentCaptor;
    @Captor
    protected ArgumentCaptor<Trainer> trainerArgumentCaptor;
    @Captor
    protected ArgumentCaptor<Training> trainingArgumentCaptor;
    @Captor
    protected ArgumentCaptor<Long> idArgumentCaptor;

    // tested objects
    protected Training testTraining;
    protected Trainee testTrainee;
    protected Trainer testTrainer;

    private AutoCloseable mocks;

    @BeforeEach
    void initMocks() {
        mocks = MockitoAnnotations.openMocks(this);
        testTrainee = Trainee.builder()
                .id(1L)
                .firstName("testName")
                .lastName("testLastName")
                .username("testName.testLastName")
                .password("testPassword")
                .isActive(true)
                .address("testAddress")
                .dateOfBirth(LocalDate.parse("1999-10-10"))
                .build();

        testTrainer = Trainer.builder()
                .id(1L)
                .firstName("testName1")
                .lastName("testLastName1")
                .username("testName1.testLastName1")
                .password("testPassword1")
                .isActive(true)
                .specialization(TrainingType.FITNESS)
                .build();

        testTraining = Training.builder()
                .id(1L)
                .trainee(testTrainee)
                .trainer(testTrainer)
                .trainingDate(LocalDateTime.now())
                .trainingDuration(Duration.ZERO)
                .trainingName("TestName")
                .trainingType(TrainingType.FITNESS)
                .build();
    }

    @AfterEach
    void closeMocks() throws Exception {
        mocks.close();
        testTraining = null;
        testTrainee = null;
        testTrainer = null;
    }
}
