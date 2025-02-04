package com.crm;


import com.crm.enums.TrainingType;
import com.crm.init.DataInitializer;
import com.crm.repositories.TraineeRepo;
import com.crm.repositories.TrainerRepo;
import com.crm.repositories.TrainingRepo;
import com.crm.repositories.entities.Trainee;
import com.crm.repositories.entities.Trainer;
import com.crm.repositories.entities.Training;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DirtiesContext
public abstract class DbTestBase {
    // repos
    @Autowired
    protected TrainingRepo trainingRepo;
    @Autowired
    protected TrainerRepo trainerRepo;
    @Autowired
    protected TraineeRepo traineeRepo;

    // tested objects
    protected Training testTraining;
    protected Trainee testTrainee;
    protected Trainer testTrainer;

    @MockitoBean
    private DataInitializer dataInitializer;

    @BeforeEach
    void setUp() {
        testTrainee = Trainee.builder()
                .firstName("testName")
                .lastName("testLastName")
                .userName("testName.testLastName")
                .password("testPassword")
                .isActive(true)
                .address("testAddress")
                .dateOfBirth(LocalDate.parse("1999-10-10"))
                .build();

        testTrainer = Trainer.builder()
                .firstName("testName1")
                .lastName("testLastName1")
                .userName("testName1.testLastName1")
                .password("testPassword1")
                .isActive(true)
                .specialization(TrainingType.FITNESS)
                .build();

        testTraining = Training.builder()
                .trainee(testTrainee)
                .trainer(testTrainer)
                .trainingDate(LocalDateTime.now())
                .trainingDuration(Duration.ZERO)
                .trainingName("TestName")
                .trainingType(TrainingType.FITNESS)
                .build();
    }

    @AfterEach
    void destroy() {
        testTraining = null;
        testTrainee = null;
        testTrainer = null;

        trainingRepo.deleteAll();
        traineeRepo.deleteAll();
        trainerRepo.deleteAll();
    }
}
