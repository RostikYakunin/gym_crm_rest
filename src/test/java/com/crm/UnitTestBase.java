package com.crm;

import com.crm.dtos.trainee.TraineeDto;
import com.crm.dtos.trainee.TraineeSaveDto;
import com.crm.dtos.trainee.TraineeViewDto;
import com.crm.models.TrainingType;
import com.crm.repositories.entities.Trainee;
import com.crm.repositories.entities.Trainer;
import com.crm.repositories.entities.Training;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
public abstract class UnitTestBase {
    // Captors
    @Captor
    protected ArgumentCaptor<Trainee> traineeArgumentCaptor;
    @Captor
    protected ArgumentCaptor<Trainer> trainerArgumentCaptor;
    @Captor
    protected ArgumentCaptor<Long> idArgumentCaptor;
    @Captor
    protected ArgumentCaptor<TraineeSaveDto> traineeSaveDtoArgumentCaptor;

    // objects for tests
    protected Training testTraining;
    protected Trainee testTrainee;
    protected Trainer testTrainer;
    protected TraineeSaveDto testTraineeSaveDto;
    protected TraineeDto testTraineeDto;
    protected TraineeViewDto testTraineeViewDto;

    @BeforeEach
    void init() {
        testTrainee = Trainee.builder()
                .id(1L)
                .firstName("testName")
                .lastName("testLastName")
                .userName("testName.testLastName")
                .password("testPassword")
                .isActive(true)
                .address("testAddress")
                .dateOfBirth(LocalDate.parse("1999-10-10"))
                .build();

        testTrainer = Trainer.builder()
                .id(1L)
                .firstName("testName1")
                .lastName("testLastName1")
                .userName("testName1.testLastName1")
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

        testTraineeSaveDto = TraineeSaveDto.builder()
                .firstName("firstName")
                .lastName("lastName")
                .password("paSsw2347")
                .address("address")
                .build();

        testTraineeDto = TraineeDto.builder()
                .id(1L)
                .firstName("testName")
                .lastName("testLastName")
                .userName("testName.testLastName")
                .password("testPassword")
                .isActive(true)
                .address("testAddress")
                .dateOfBirth(LocalDate.parse("1999-10-10"))
                .build();
    }

    @AfterEach
    void destroy() {
        testTraining = null;
        testTrainee = null;
        testTrainer = null;
        testTraineeSaveDto = null;
        testTraineeViewDto = null;
        testTraineeDto = null;
    }
}
